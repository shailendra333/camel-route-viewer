package com.googlecode.camelrouteviewer.views;

import org.apache.camel.management.JmxSystemPropertyKeys;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.util.SelectionUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;

import com.googlecode.camelrouteviewer.content.RouteContentProvider;
import com.googlecode.camelrouteviewer.content.RouteLabelProvider;
import com.googlecode.camelrouteviewer.content.RouteNode;
import com.googlecode.camelrouteviewer.utils.ImageShop;

/**
 * Reference: <a herf="http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/infoviews/JavadocView.java"
 * >Eclipse JDT Javadoc view</a>
 */
public class RouteView extends ViewPart implements ISelectionListener {

	/**
	 * Progress monitor used to cancel pending computations.
	 * 
	 * @since 3.4
	 */
	private IProgressMonitor fComputeProgressMonitor;

	/**
	 * True if linking with selection is enabled, false otherwise.
	 * 
	 * @since 3.4
	 */
	private boolean fLinking = true;

	/**
	 * The last selected element if linking was disabled.
	 * 
	 * @since 3.4
	 */
	private IJavaElement fLastSelection;

	/** The current input. */
	protected IJavaElement fCurrentViewInput;

	/** Counts the number of background computation requests. */
	private volatile int fComputeCount;
	private LinkAction fToggleLinkAction;

	private GraphViewer viewer;

	/**
	 * The constructor.
	 */
	public RouteView() {
	}

	private IPartListener2 fPartListener = new IPartListener2() {
		public void partVisible(IWorkbenchPartReference ref) {
			if (ref.getId().equals(getSite().getId())) {
				IWorkbenchPart activePart = ref.getPage().getActivePart();
				if (activePart != null)
					selectionChanged(activePart, ref.getPage().getSelection());
				startListeningForSelectionChanges();
			}
		}

		public void partHidden(IWorkbenchPartReference ref) {
			if (ref.getId().equals(getSite().getId()))
				stopListeningForSelectionChanges();
		}

		public void partInputChanged(IWorkbenchPartReference ref) {
			if (!ref.getId().equals(getSite().getId()))
				computeAndSetInput(ref.getPart(false));
		}

		public void partActivated(IWorkbenchPartReference ref) {

		}

		public void partBroughtToTop(IWorkbenchPartReference ref) {
		}

		public void partClosed(IWorkbenchPartReference ref) {

		}

		public void partDeactivated(IWorkbenchPartReference ref) {
		}

		public void partOpened(IWorkbenchPartReference ref) {
		}
	};

	private Action fGotoInputAction;

	/**
	 * Start to listen for selection changes.
	 */
	protected void startListeningForSelectionChanges() {
		getSite().getPage().addPostSelectionListener(this);
	}

	/**
	 * Stop to listen for selection changes.
	 */
	protected void stopListeningForSelectionChanges() {
		getSite().getPage().removePostSelectionListener(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */

	public void createPartControl(Composite parent) {

		IWorkbenchPage page = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			page = window.getActivePage();
		}

		if (page == null) {
			// Look for a window and get the page off it!
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
					.getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++) {
				if (windows[i] != null) {
					window = windows[i];
					page = windows[i].getActivePage();
					if (page != null)
						break;
				}
			}

		}
		IWorkbenchPage[] pages = window.getPages();
		System.out.println(pages);

		System.setProperty(JmxSystemPropertyKeys.DISABLED, "true");
		// Display display = getSite().getShell().getDisplay();
		// RGB r1 = new RGB(99, 00, 00);
		// parent.setBackground(new Color(display,r1));
		// parent.setForeground(new Color(display,r1));

		viewer = new GraphViewer(parent, SWT.NONE);
		viewer.setContentProvider(new RouteContentProvider());
		viewer.setLabelProvider(new RouteLabelProvider());

		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		HorizontalTreeLayoutAlgorithm hta = new HorizontalTreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING);

		viewer.setLayoutAlgorithm(hta);
		viewer.setInput(new Object());
		getSite().getWorkbenchWindow().getPartService().addPartListener(
				fPartListener);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof StructuredSelection) {
					StructuredSelection structuredSelection = (StructuredSelection) selection;
					Object firstElement = structuredSelection.getFirstElement();
					if (firstElement instanceof RouteNode) {
						RouteNode node = (RouteNode) firstElement;
						System.out.println("Selected node: " + node);
					}
				}
		}});
		
		// lets expose the selection to other viewers
		getViewSite().setSelectionProvider(viewer);
		
		fToggleLinkAction = new LinkAction();
		fToggleLinkAction
				.setActionDefinitionId("com.googlecode.camelrouteviewer.views.RouteView.LinkAction");
		IHandlerService handlerService = (IHandlerService) getSite()
				.getService(IHandlerService.class);
		handlerService.activateHandler(
				"com.googlecode.camelrouteviewer.views.RouteView.LinkAction",
				new ActionHandler(fToggleLinkAction));

		fGotoInputAction = new GotoInputAction(this);
		fGotoInputAction.setEnabled(false);

		IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		toolBar.add(fToggleLinkAction);
		toolBar.add(fGotoInputAction);

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	/*
	 * @see ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part.equals(this))
			return;

		if (!fLinking) {
			IJavaElement javaElement = findSelectedJavaElement(part, selection);
			if (javaElement != null)
				fLastSelection = javaElement;
		} else {
			fLastSelection = null;
			computeAndSetInput(part);
		}
	}

	/**
	 * Determines all necessary details and delegates the computation into a
	 * background thread.
	 * 
	 * @param part
	 *            the workbench part
	 */
	private void computeAndSetInput(final IWorkbenchPart part) {
		computeAndDoSetInput(part, null);
	}

	/**
	 * Sets the input for this view.
	 * 
	 * @param element
	 *            the java element
	 */
	public final void setInput(final IJavaElement element) {
		computeAndDoSetInput(null, element);
	}

	/**
	 * Determines all necessary details and delegates the computation into a
	 * background thread. One of part or element must be non-null.
	 * 
	 * @param part
	 *            the workbench part, or <code>null</code> if
	 *            <code>element</code> not <code>null</code>
	 * @param element
	 *            the java element, or <code>null</code> if <code>part</code>
	 *            not <code>null</code>
	 */
	private void computeAndDoSetInput(final IWorkbenchPart part,
			final IJavaElement element) {
		Assert.isLegal(part != null || element != null);

		final int currentCount = ++fComputeCount;

		final ISelection selection;
		if (element != null)
			selection = null;
		else {
			ISelectionProvider provider = part.getSite().getSelectionProvider();
			if (provider == null)
				return;

			selection = provider.getSelection();
			if (selection == null || selection.isEmpty())
				return;
		}

		if (fComputeProgressMonitor != null)
			fComputeProgressMonitor.setCanceled(true);
		final IProgressMonitor computeProgressMonitor = new NullProgressMonitor();
		fComputeProgressMonitor = computeProgressMonitor;

		Thread thread = new Thread("Info view input computer") { //$NON-NLS-1$
			public void run() {
				if (currentCount != fComputeCount)
					return;

				final IJavaElement je;
				if (element != null)
					je = element;
				else {
					je = findSelectedJavaElement(part, selection);
					if (isIgnoringNewInput(je, part, selection))
						return;
				}

				final RouteSource routeSource = computerInput(je);
				if (routeSource == null)
					return;
				// The actual computation
				// final Object input = computeInput(part, selection, je,
				// computeProgressMonitor);
				// if (input == null)
				// return;

				Shell shell = getSite().getShell();
				if (shell.isDisposed())
					return;

				Display display = shell.getDisplay();
				if (display.isDisposed())
					return;

				display.asyncExec(new Runnable() {
					/*
					 * @see java.lang.Runnable#run()
					 */
					public void run() {

						if (fComputeCount != currentCount
								|| getViewSite().getShell().isDisposed())
							return;

						fCurrentViewInput = je;
						doSetInput(routeSource);

						fComputeProgressMonitor = null;
					}
				});
			}
		};

		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	/**
	 * Tries to get a Java element out of the given element.
	 * 
	 * @param element
	 *            an object
	 * @return the Java element represented by the given element or
	 *         <code>null</code>
	 */
	private IJavaElement findJavaElement(Object element) {

		if (element == null)
			return null;

		IJavaElement je = null;
		if (element instanceof IAdaptable)
			je = (IJavaElement) ((IAdaptable) element)
					.getAdapter(IJavaElement.class);

		return je;
	}

	/**
	 * Tells whether the new input should be ignored if the current input is the
	 * same.
	 * 
	 * @param je
	 *            the new input
	 * @param selection
	 *            the current selection from the part that provides the input
	 * @return <code>true</code> if the new input should be ignored
	 */
	protected boolean isIgnoringNewInput(IJavaElement je, IWorkbenchPart part,
			ISelection selection) {
		return fCurrentViewInput != null && fCurrentViewInput.equals(je)
				&& je != null;
	}

	protected void doSetInput(RouteSource input) {
		fGotoInputAction.setEnabled(true);
		viewer.setInput(input);
	}

	/**
	 * Finds and returns the Java element selected in the given part.
	 * 
	 * @param part
	 *            the workbench part for which to find the selected Java element
	 * @param selection
	 *            the selection
	 * @return the selected Java element
	 */
	protected IJavaElement findSelectedJavaElement(IWorkbenchPart part,
			ISelection selection) {
		Object element;
		try {
			if (part instanceof JavaEditor
					&& selection instanceof ITextSelection) {
				IJavaElement[] elements = TextSelectionConverter.codeResolve(
						(JavaEditor) part, (ITextSelection) selection);
				if (elements != null && elements.length > 0)
					return elements[0];
				else
					return null;
			} else if (selection instanceof IStructuredSelection) {
				element = SelectionUtil.getSingleElement(selection);
			} else {
				return null;
			}
		} catch (JavaModelException e) {
			// TODO WHY exception?
			e.printStackTrace();
			return null;
		}

		return findJavaElement(element);
	}

	/**
	 * Returns the input of this view.
	 * 
	 * @return input the input object or <code>null</code> if not input is set
	 */
	protected IJavaElement getInput() {
		return fCurrentViewInput;
	}

	public RouteSource computerInput(IJavaElement je) {
		// Computer Camel Route begin
		ISourceReference sourceRef = null;

		if (je instanceof IMethod) {
			IMethod sourceMethod = (IMethod) je;

			IJavaElement sourceMethodParent = sourceMethod.getParent();

			if (sourceMethodParent instanceof IType) {
				IType sourceType = (IType) sourceMethodParent;
				try {
					String superClassName = sourceType.getSuperclassName();
					if ("RouteBuilder".equals(superClassName)) {
						sourceRef = (ISourceReference) je;
					}
				} catch (JavaModelException e) {
					// TODO WHY exception ?
					sourceRef = null;
				}
			}

		}

		if (je instanceof IType) {
			IType sourceType = (IType) je;
			String superClassName;
			try {
				superClassName = sourceType.getSuperclassName();
				if ("RouteBuilder".equals(superClassName)) {
					IMethod iMethod = sourceType.getMethod("configure", null);
					sourceRef = (ISourceReference) iMethod;
				}
			} catch (JavaModelException e) { //
				// TODO WHY exception ?
				sourceRef = null;
			}

		}
		if (sourceRef == null) {
			return null;
		}

		String source;
		try {
			source = sourceRef.getSource();
		} catch (JavaModelException e) {
			// TODO WHY exception ?
			source = null;
		}

		if (containKnowIssues(source)) {
			return null;
		}
		RouteSource routeSource = new RouteSource(source, RouteSourceType.DSL);
		return routeSource;
	}

	private boolean containKnowIssues(String source) {

		if (source.indexOf("@overwrite") != -1) {
			String a = source.substring(0, source.indexOf("@overwrite"));

			String b = source.substring(source.indexOf("@overwrite")
					+ "@overwrite".length(), source.length());

			source = a + b;
		}

		return (source.indexOf("pipeline") != -1)
				|| (source.indexOf("multicast") != -1);

	}

	/**
	 * Sets whether this info view reacts to selection changes in the workbench.
	 * 
	 * @param enabled
	 *            if true then the input is set on selection changes
	 */
	protected void setLinkingEnabled(boolean enabled) {
		fLinking = enabled;

		if (fLinking && fLastSelection != null) {
			setInput(fLastSelection);
		}
	}

	/**
	 * Returns whether this info view reacts to selection changes in the
	 * workbench.
	 * 
	 * @return true if linking with selection is enabled
	 */
	protected boolean isLinkingEnabled() {
		return fLinking;
	}

	/**
	 * Action to toggle linking with selection.
	 * 
	 * @since 3.4
	 */
	private class LinkAction extends Action {

		public LinkAction() {
			super(RouteViewMessages.RouteView_action_toogleLinking_text,
					SWT.TOGGLE);

			setTitleToolTip(RouteViewMessages.RouteView_action_toggleLinking_toolTipText);
			LinkAction.this.setImageDescriptor(ImageShop
					.getDescriptor("synced.gif"));
			setChecked(isLinkingEnabled());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			setLinkingEnabled(!isLinkingEnabled());
		}
	}

	/*
	 * @see AbstractInfoView#setForeground(Color)
	 */
	// protected void setForeground(Color color) {
	// getControl().setForeground(color);
	// }
	/*
	 * @see AbstractInfoView#setBackground(Color)
	 */
	// protected void setBackground(Color color) {
	// getControl().setBackground(color);
	// fBackgroundColorRGB = color.getRGB();
	// refresh();
	// }
	// protected Control getControl() {
	// return canvas;
	// }
	/**
	 * Returns the input of this view.
	 * 
	 * @return input the input object or <code>null</code> if not input is set
	 */

}