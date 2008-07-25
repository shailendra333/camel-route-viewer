package com.googlecode.camelrouteviewer.content;

public class RouteConnection {

	public RouteConnection(RouteNode source, String connectionString,
			RouteNode destination) {
		this.source = source;
		this.connectionString = connectionString;
		this.destination = destination;
	}

	private RouteNode source;
	private RouteNode destination;
	private String connectionString;

	public Object getSource() {
		return source;
	}

	public void setSource(RouteNode source) {
		this.source = source;
	}

	public Object getDestination() {
		return destination;
	}

	public void setDestination(RouteNode destination) {
		this.destination = destination;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public String toString() {
		return "";
	}

}
