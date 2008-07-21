import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.management.JmxSystemPropertyKeys;
import org.apache.camel.model.ChoiceType;
import org.apache.camel.model.FromType;
import org.apache.camel.model.MulticastType;
import org.apache.camel.model.ProcessorType;
import org.apache.camel.model.RouteType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;

public class RouteGraphGenerator {

	public static void generate(RouteBuilder routeBuilder) throws Exception {
		System.setProperty(JmxSystemPropertyKeys.DISABLED, "true");
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("Camel Viewer");
		shell.setLayout(new FillLayout());
		shell.setSize(850, 200);
		Graph g = new Graph(shell, SWT.NONE);

		HorizontalTreeLayoutAlgorithm hta = new HorizontalTreeLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING);

		g.setLayoutAlgorithm(hta, true);

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(routeBuilder);

		List<RouteType> routes = context.getRouteDefinitions();

		for (RouteType route : routes) {

			FromType fromType = route.getInputs().get(0);

			NodeData fromNodeData = NodeData.getNodeData(g, fromType);
			for (ProcessorType output : route.getOutputs()) {
				NodeData newData = getNodeData(g, fromNodeData, output);

				fromNodeData = newData;

			}

		}
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	public static NodeData getNodeData(Graph g, NodeData from,
			ProcessorType processorType) {
		NodeData processorNodeData = NodeData.getNodeData(g, processorType);
		new GraphConnection(g, ZestStyles.CONNECTIONS_DIRECTED, from
				.getGraphNode(), processorNodeData.getGraphNode());

		List<ProcessorType> outputs = processorNodeData.outputs;

		if (outputs != null) {
			for (ProcessorType output : outputs) {
				NodeData newNodeData = getNodeData(g, processorNodeData, output);
				if (!isMulticastNode(processorType)) {
					processorNodeData = newNodeData;
				}
			}
		}

		return processorNodeData;
	}

	protected static boolean isMulticastNode(ProcessorType node) {
		return node instanceof MulticastType || node instanceof ChoiceType;
	}

}
