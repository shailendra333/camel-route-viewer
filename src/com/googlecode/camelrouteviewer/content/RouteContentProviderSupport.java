package com.googlecode.camelrouteviewer.content;

import java.util.List;

import org.apache.camel.model.ChoiceType;
import org.apache.camel.model.FromType;
import org.apache.camel.model.MulticastType;
import org.apache.camel.model.ProcessorType;
import org.apache.camel.model.RouteType;
import org.eclipse.zest.core.viewers.IGraphContentProvider;

/**
 * A base class for route content providers
 */
public abstract class RouteContentProviderSupport implements IGraphContentProvider {

	protected RoutesStore routesTempStore = new RoutesStore();
;

	public RouteContentProviderSupport() {
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