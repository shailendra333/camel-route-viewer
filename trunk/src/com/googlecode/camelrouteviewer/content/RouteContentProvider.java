package com.googlecode.camelrouteviewer.content;

import java.util.List;

import org.apache.camel.model.ChoiceType;
import org.apache.camel.model.FromType;
import org.apache.camel.model.MulticastType;
import org.apache.camel.model.ProcessorType;
import org.apache.camel.model.RouteType;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;

import com.googlecode.camelrouteviewer.views.RouteSource;

/**
 * Can handle either a {@link List<RouteType>} or a {@link RouteSource} as the input
 * which is then converted into the content for the viewer
 */
public class RouteContentProvider implements IGraphContentProvider {

	private RouteSourceToDefinitionConverter converter = new RouteSourceToDefinitionConverter();
	protected RoutesStore routesTempStore = new RoutesStore();

	public RouteContentProvider() {
	}
	
	public void setRouteDefinitions(List<RouteType> routes) {
			clear();
		try {
			buildRoute(routes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List) {
			List<RouteType> routes = (List<RouteType>) newInput;
			setRouteDefinitions(routes);
		}
		else if (newInput instanceof RouteSource) {
			List<RouteType> routes = converter.convert((RouteSource) newInput);
			setRouteDefinitions(routes);
		}
	}

	public RouteNode getNodeData(RoutesStore routesTempStore, RouteNode from, ProcessorType processorType) {
		RouteNode processorNodeData = routesTempStore.getNodeData(processorType);
	
		//ensure:connectionString is unique in RoutesStore.
		String connectionString = from.id+"->"+processorType.idOrCreate();
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

	protected void buildRoute(List<RouteType> routes) {
	
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

	public void clear() {
		routesTempStore.clear();
	}
}