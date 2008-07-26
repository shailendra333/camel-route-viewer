package com.googlecode.camelrouteviewer.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedType;

public class RoutesStore {
	public List<RouteConnection> connections = new ArrayList<RouteConnection>();
	private Map<Object, RouteNode> nodeDataMap = new HashMap<Object, RouteNode>();

	public Map<Object, RouteNode> getNodeDataMap() {
		return nodeDataMap;
	}

	public List<RouteConnection> getConnections() {
		return connections;
	}

	public Object[] getConnectionsAsArray() {
		return connections.toArray();
	}

	public RouteNode getNodeData(OptionalIdentifiedType node) {
		Object o = nodeDataMap.get(node);
		RouteNode nodeData = null;
		if (o == null) {
			nodeData = new RouteNode(node);
			nodeDataMap.put(node, nodeData);
		} else {
			nodeData = (RouteNode) o;
		}
		return nodeData;
	}

	public Object getSource(RouteConnection compareConnection) {

		Iterator<RouteConnection> it = connections.iterator();
		while (it.hasNext()) {
			RouteConnection routeConnection = it.next();
			if (routeConnection.getConnectionString().equals(
					compareConnection.getConnectionString())) {
				return routeConnection.getSource();
			}
		}
		return null;

	}

	public Object getDestination(RouteConnection compareConnection) {
		Iterator<RouteConnection> it = connections.iterator();
		while (it.hasNext()) {
			RouteConnection routeConnection = it.next();
			if (routeConnection.getConnectionString().equals(
					compareConnection.getConnectionString())) {
				return routeConnection.getDestination();
			}
		}
		return null;
	}

	public void clear() {
		connections.clear();
		nodeDataMap.clear();
	}
}
