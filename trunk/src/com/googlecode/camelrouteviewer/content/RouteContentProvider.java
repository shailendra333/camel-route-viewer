package com.googlecode.camelrouteviewer.content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.management.JmxSystemPropertyKeys;
import org.apache.camel.model.ChoiceType;
import org.apache.camel.model.FromType;
import org.apache.camel.model.MulticastType;
import org.apache.camel.model.ProcessorType;
import org.apache.camel.model.RouteType;
import org.apache.camel.util.UuidGenerator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;

import com.googlecode.camelrouteviewer.views.RouteSource;

import bsh.EvalError;
import bsh.Interpreter;

public class RouteContentProvider implements IGraphContentProvider {

	private RoutesStore routesTempStore;

	private CamelContext context;

	private Interpreter interpreter;

	public RouteContentProvider() {
		routesTempStore = new RoutesStore();
		context = new DefaultCamelContext();

		interpreter = new Interpreter();
	}

	private void buildRoute(CamelContext context, String routeBuilderBSH) {
		BufferedReader bufferedreader = null;

		try {
			bufferedreader = new BufferedReader(new StringReader(
					routeBuilderBSH));
			interpreter.set("context", context);
			interpreter.eval(bufferedreader);
		} catch (EvalError e) {
			// FOR DEBUG
			e.printStackTrace();
			return;
		} finally {
			try {
				bufferedreader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<RouteType> routes = context.getRouteDefinitions();

		for (RouteType route : routes) {

			FromType fromType = route.getInputs().get(0);

			RouteNode fromNodeData = routesTempStore.getNodeData(fromType);
			for (ProcessorType output : route.getOutputs()) {
				RouteNode newData = getNodeData(routesTempStore, fromNodeData,
						output);

				fromNodeData = newData;

			}

		}
	}

	public RouteNode getNodeData(RoutesStore routesTempStore, RouteNode from,
			ProcessorType processorType) {
		RouteNode processorNodeData = routesTempStore.getNodeData(processorType);

		// ^_^ pls let me use camel utils
		UuidGenerator uuidGenerator = new UuidGenerator("routeview");
		String connectionString = uuidGenerator.generateSanitizedId();
		RouteConnection routeConnection = new RouteConnection(from,
				connectionString, processorNodeData);
		routesTempStore.getConnections().add(routeConnection);

		List<ProcessorType> outputs = processorNodeData.outputs;

		if (outputs != null) {
			for (ProcessorType output : outputs) {
				RouteNode newNodeData = getNodeData(routesTempStore,
						processorNodeData, output);
				if (!isMulticastNode(processorType)) {
					processorNodeData = newNodeData;
				}
			}
		}

		return processorNodeData;
	}

	protected boolean isMulticastNode(ProcessorType node) {
		return node instanceof MulticastType || node instanceof ChoiceType;
	}

	public Object getSource(Object rel) {
		RouteConnection compareConnection = (RouteConnection) rel;
		return routesTempStore.getSource(compareConnection);
	}

	public Object[] getElements(Object input) {
		return routesTempStore.getConnectionsAsArray();
	}

	public Object getDestination(Object rel) {
		RouteConnection compareConnection = (RouteConnection) rel;
		return routesTempStore.getDestination(compareConnection);
	}

	public double getWeight(Object connection) {
		return 0;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof RouteSource) {
			routesTempStore.clear();
			try {
				context.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context = new DefaultCamelContext();
			RouteSource routeSource = (RouteSource) newInput;

			String beanShellScript = routeSource.getBeanShellScript();
			buildRoute(context, beanShellScript);

			viewer.refresh();
		}
	}
}