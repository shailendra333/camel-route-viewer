package com.googlecode.camelrouteviewer.content;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.DataFormatClause;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.model.AggregatorType;
import org.apache.camel.model.ChoiceType;
import org.apache.camel.model.ConvertBodyType;
import org.apache.camel.model.DelayerType;
import org.apache.camel.model.FilterType;
import org.apache.camel.model.FromType;
import org.apache.camel.model.MulticastType;
import org.apache.camel.model.OtherwiseType;
import org.apache.camel.model.ProcessorType;
import org.apache.camel.model.RecipientListType;
import org.apache.camel.model.ResequencerType;
import org.apache.camel.model.RoutingSlipType;
import org.apache.camel.model.SetBodyType;
import org.apache.camel.model.SetHeaderType;
import org.apache.camel.model.SplitterType;
import org.apache.camel.model.ThrottlerType;
import org.apache.camel.model.ToType;
import org.apache.camel.model.TransformType;
import org.apache.camel.model.WhenType;
import org.apache.camel.processor.interceptor.Debugger;
import org.apache.camel.spring.Main;
import org.apache.camel.view.NodeData;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertySource;

import com.googlecode.camelrouteviewer.utils.ImageShop;

public class RouteNode implements IAdaptable {
	
	// should we generate some dummy messages?
	protected static final boolean generateDummyResults = true;

	public String tooltop;

	public String label;

	private RouteNodeType nodeType;

	public List<ProcessorType> outputs;

	public Image image;

	public String patternDocumentationUrl;

	public String patternName;

	public ProcessorType processor;

	public RouteNode(Object node) {
        if (node instanceof ProcessorType) {
        	this.processor = (ProcessorType) node;
        	
        	// lets reuse any metadata we can from the generic code in Camel view 
        	// such as the URL of the pattern documentation
        	NodeData data = new NodeData(processor.idOrCreate(), node, "http://activemq.apache.org/camel/images/eip/");
        	this.patternDocumentationUrl = data.url;
        	this.patternName = data.nodeType;
        }
		if (node instanceof FromType) {
			FromType fromType = (FromType) node;
			this.nodeType = RouteNodeType.From;
			this.tooltop = fromType.getLabel();
			this.label = removeQueryString(this.tooltop);
		} else if (node instanceof ToType) {
			ToType toType = (ToType) node;
			this.nodeType = RouteNodeType.To;
			this.tooltop = toType.getLabel();
			this.label = removeQueryString(this.tooltop);

		} else if (node instanceof FilterType) {
			this.nodeType = RouteNodeType.Filter;
			this.label = "Filter";
			image = ImageShop.get("filter.gif");

		} else if (node instanceof ChoiceType) {
			this.nodeType = RouteNodeType.Choice;
			this.label = "Choice";
			ChoiceType choice = (ChoiceType) node;
			List<ProcessorType> outputs = new ArrayList<ProcessorType>(choice
					.getWhenClauses());
			outputs.add(choice.getOtherwise());
			this.outputs = outputs;
			image = ImageShop.get("contentBased.gif");
		} else if (node instanceof WhenType) {
			WhenType when = (WhenType) node;
			this.nodeType = RouteNodeType.When;
			this.label = "When";
			image = ImageShop.get("contentBased.gif");
		} else if (node instanceof OtherwiseType) {
			OtherwiseType otherWise = (OtherwiseType) node;
			this.nodeType = RouteNodeType.Otherwise;
			this.label = "Otherwise";
			// this.label = nodeType + ":[" + otherWise.getLabel() + "]";
			this.tooltop = "Otherwise";
			image = ImageShop.get("contentBased.gif");
		} else if (node instanceof MulticastType) {
			this.nodeType = RouteNodeType.Multicast;
			this.label = "Multicast";
			this.tooltop = "Multicast";
		} else if (node instanceof RecipientListType) {
			this.nodeType = RouteNodeType.Recipient_List;
			this.label = "Recipient List";
			image = ImageShop.get("recipient.gif");
		} else if (node instanceof RoutingSlipType) {
			this.nodeType = RouteNodeType.Routing_Slip;

			this.label = "Routing Slip";
			this.tooltop = ((RoutingSlipType) node).getHeaderName();
			image = ImageShop.get("routingSlip.gif");
		} else if (node instanceof SplitterType) {
			this.nodeType = RouteNodeType.Splitter;
			this.label = "Splitter";
			image = ImageShop.get("splitter.gif");
		} else if (node instanceof AggregatorType) {
			this.nodeType = RouteNodeType.Aggregator;
			this.label = "Aggregator";
			image = ImageShop.get("aggregator.gif");
		} else if (node instanceof ResequencerType) {
			this.nodeType = RouteNodeType.Resequencer;
			this.label = "Resequencer";
			image = ImageShop.get("resequencer.gif");
		} else if (node instanceof ThrottlerType) {
			this.nodeType = RouteNodeType.Throttler;
			this.label = "Throttler";
		} else if (node instanceof DelayerType) {
			this.nodeType = RouteNodeType.Delayer;
			this.label = "Delayer";
		} else if (node instanceof SetBodyType) {
			this.nodeType = RouteNodeType.SetBody;
			this.label = "SetBody";
			image = ImageShop.get("translator.gif");
		} else if (node instanceof SetHeaderType) {
			this.nodeType = RouteNodeType.SetHeader;
			this.label = "SetHeader";
			image = ImageShop.get("translator.gif");
		} else if (node instanceof TransformType) {
			this.nodeType = RouteNodeType.Transform;
			this.label = "Transform";
			image = ImageShop.get("translator.gif");
		} else if (node instanceof ConvertBodyType) {
			this.nodeType = RouteNodeType.ConvertBody;
			this.label = "ConvertBody";
			image = ImageShop.get("translator.gif");
		} else if (node instanceof DataFormatClause) {
			this.nodeType = RouteNodeType.DataFormatClause;
			DataFormatClause dataFormatClause = (DataFormatClause) node;
			// dataFormatClause.getOperation().! method not exist:(

			this.label = "dataFormatClause";
		} else if (node instanceof ProcessorType) {
			this.nodeType = RouteNodeType.Processor;
			this.label = "Processor";
			image = ImageShop.get("processor.gif");
		}

		if (node instanceof FromType || node instanceof ToType) {
			if (label.startsWith("direct")) {
				image = ImageShop.get("direct.gif");
			} else if (label.startsWith("mock")) {
				image = ImageShop.get("mock.gif");
			} else if (label.startsWith("activemq")) {
				image = ImageShop.get("activemq.gif");
			} else if (label.startsWith("amqp")) {
				image = ImageShop.get("amqp.gif");
			} else if (label.startsWith("atom")) {
				image = ImageShop.get("atom.gif");
			} else if (label.startsWith("file")) {
				image = ImageShop.get("file.gif");
			} else if (label.startsWith("ibatis")) {
				image = ImageShop.get("iBATIS.gif");
			} else if (label.startsWith("jdbc")) {
				image = ImageShop.get("jdbc.gif");
			} else if (label.startsWith("quartz")) {
				image = ImageShop.get("quartz.gif");
			} else if (label.startsWith("hibernate")) {
				image = ImageShop.get("hibernate.gif");
			} else if (label.startsWith("esper")) {
				image = ImageShop.get("esper.gif");
			}

			else {
				image = ImageShop.get("endpoint.gif");
			}
		}

		if (node instanceof ProcessorType && this.outputs == null) {
			ProcessorType processorType = (ProcessorType) node;
			this.outputs = processorType.getOutputs();
		}
	}

	protected String removeQueryString(String text) {
		int idx = text.indexOf("?");
		if (idx <= 0) {
			return text;
		} else {
			return text.substring(0, idx);
		}
	}

	public String toString() {
		return this.label;
	}

	public Object getAdapter(Class type) {
		if (type == IPropertySource.class) {
			return new RouteNodePropertySource(this);
		}
		System.out.println("Attempted to convert node: " + this + " to type: " + type);
		return null;
	}

	/**
	 * If debugging is on lets return the current list of messages sent to this
	 * node
	 */
	public Object[] getExchangeArray() {
		return getExchanges().toArray();
	}

	/**
	 * If debugging is on lets return the current list of messages sent to this
	 * node
	 * 
	 * @return
	 */
	public List<Exchange> getExchanges() {
		if (processor != null) {
			String id = processor.idOrCreate();

			// TODO how do we look up the Main that is currently running?
			// could we somehow associate it with the route node?
			Main main = null;

			if (main != null) {
				Debugger debugger = main.getDebugger();
				if (debugger != null) {
					return debugger.getExchanges(id);
				}
			}
		}
		// TODO should we hack it for now and return a standard set of exchanges so we can build up the UI stuff for now
		// while we actually wire in the Spring runtime / debugger parts?
		ArrayList<Exchange> answer = new ArrayList<Exchange>();
		if (processor != null && generateDummyResults) {
			createDummyMessages(answer);
		}
		return answer;
	}

	protected void createDummyMessages(ArrayList<Exchange> answer) {
		for (int i = 0; i < 10; i++) {
			answer.add(createDummyMessage(i));
		}
	}

	protected Exchange createDummyMessage(int i) {
		CamelContext context = null;
		Exchange answer = new DefaultExchange(context);
		Message in = answer.getIn();
		in.setHeader("counter", i);
		in.setHeader("nodeId", processor.idOrCreate());
		in.setBody("message " + i + " -> " + processor);
		return answer;
	}

}
