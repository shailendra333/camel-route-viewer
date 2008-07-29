package com.googlecode.camelrouteviewer.content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteType;

import bsh.EvalError;
import bsh.Interpreter;

import com.googlecode.camelrouteviewer.views.RouteSource;

public class RouteSourceToDefinitionConverter {

	private CamelContext context;

	private Interpreter interpreter;

	public RouteSourceToDefinitionConverter() {
		interpreter = new Interpreter();
	}

	public List<RouteType> convert(RouteSource routeSource) {
		if (context != null) {
			try {
				context.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		context = new DefaultCamelContext();

		String routeBuilderBSH = routeSource.getBeanShellScript();
		BufferedReader bufferedreader = null;

		try {
			bufferedreader = new BufferedReader(new StringReader(
					routeBuilderBSH));
			interpreter.set("context", context);
			interpreter.eval(bufferedreader);
		} catch (EvalError e) {
			// FOR DEBUG
			e.printStackTrace();
			return new ArrayList<RouteType>();
		} finally {
			try {
				bufferedreader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<RouteType> routes = context.getRouteDefinitions();
		return routes;
	}
}