package com.googlecode.camelrouteviewer.content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.FromType;
import org.apache.camel.model.ProcessorType;
import org.apache.camel.model.RouteType;
import org.eclipse.jface.viewers.Viewer;

import bsh.EvalError;
import bsh.Interpreter;

import com.googlecode.camelrouteviewer.views.RouteSource;

public class RouteContentProvider extends RouteContentProviderSupport {

	private CamelContext context;

	private Interpreter interpreter;

	public RouteContentProvider() {
		context = new DefaultCamelContext();

		interpreter = new Interpreter();
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

		buildRoute(routes);
	}
}