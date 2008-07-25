package com.googlecode.camelrouteviewer.views;

public class RouteSource {

	public static final String DSL_ROUTE_SOURCE_HEADER = "import org.apache.camel.*;\n"

			+ "import org.apache.camel.builder.xml.*;\n"
			+ "import org.apache.camel.processor.interceptor.*;\n"
			+ "class CamelRouteViewerBuilder extends org.apache.camel.builder.RouteBuilder{"
			+ "\n";

	private String routeString;

	public static final String DSL_ROUTE_SOURCE_TAIL = "\n" + "}" + "\n"
			+ "context.addRoutes(new CamelRouteViewerBuilder());";
	private RouteSourceType type;

	public RouteSource(String routeString, RouteSourceType type) {
		this.routeString = routeString;
		this.type = type;
	}

	public String getBeanShellScript() {
		if (type.equals(RouteSourceType.DSL)) {
			return DSL_ROUTE_SOURCE_HEADER + routeString
					+ DSL_ROUTE_SOURCE_TAIL;
		} else {
			// TODO
			return "";
		}
	}

	public String getRouteString() {
		return routeString;
	}

	public void setRouteString(String routeString) {
		this.routeString = routeString;
	}

	public RouteSourceType getType() {
		return type;
	}

	public void setType(RouteSourceType type) {
		this.type = type;
	}

}
