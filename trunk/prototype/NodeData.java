

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.model.AggregatorType;
import org.apache.camel.model.ChoiceType;
import org.apache.camel.model.DelayerType;
import org.apache.camel.model.FilterType;
import org.apache.camel.model.FromType;
import org.apache.camel.model.MulticastType;
import org.apache.camel.model.OtherwiseType;
import org.apache.camel.model.ProcessorType;
import org.apache.camel.model.RecipientListType;
import org.apache.camel.model.ResequencerType;
import org.apache.camel.model.RoutingSlipType;
import org.apache.camel.model.SplitterType;
import org.apache.camel.model.ThrottlerType;
import org.apache.camel.model.ToType;
import org.apache.camel.model.WhenType;
import org.eclipse.swt.SWT;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;

public class NodeData {

	private GraphNode graphNode;

	public GraphNode getGraphNode() {
		return graphNode;
	}

	public String tooltop;

	public String label;

	private String nodeType;

	public List<ProcessorType> outputs;
	private static Map map = new HashMap();

	public static Map getMap() {
		return map;
	}

	public static NodeData getNodeData(Graph g, Object node) {
		Object o = map.get(node);
		NodeData nodeData = null;
		if (o == null) {
			nodeData = new NodeData(g, node);
			map.put(node, nodeData);
		} else {
			nodeData = (NodeData) o;
		}
		return nodeData;
	}

	public NodeData(Graph g, Object node) {

		if (node instanceof FromType) {
			FromType fromType = (FromType) node;
			this.tooltop = fromType.getLabel();
			this.label = removeQueryString(this.tooltop);
		} else if (node instanceof ToType) {
			ToType toType = (ToType) node;
			this.tooltop = toType.getLabel();
			this.label = removeQueryString(this.tooltop);
		} else if (node instanceof FilterType) {
			this.nodeType = "Filter";
			this.label = this.nodeType;
		} else if (node instanceof ChoiceType) {
			this.nodeType = "Choice";
			this.label = nodeType;
			ChoiceType choice = (ChoiceType) node;
			List<ProcessorType> outputs = new ArrayList<ProcessorType>(choice
					.getWhenClauses());
			outputs.add(choice.getOtherwise());
			this.outputs = outputs;
		} else if (node instanceof WhenType) {
			WhenType when = (WhenType) node;
			this.nodeType = "When";
			this.label = nodeType;
		} else if (node instanceof OtherwiseType) {
			OtherwiseType otherWise = (OtherwiseType) node;
			this.nodeType = "Otherwise";
			this.label = nodeType;
			// this.label = nodeType + ":[" + otherWise.getLabel() + "]";
			this.tooltop = "Otherwise";
		} else if (node instanceof MulticastType) {

			this.nodeType = "Multicast";
			this.label = "Multicast";
			this.tooltop = "Multicast";
		} else if (node instanceof RecipientListType) {
			this.nodeType = "Recipient List";
			this.label = nodeType;
		} else if (node instanceof RoutingSlipType) {

			this.nodeType = "Routing Slip";
			this.label = nodeType;
			this.tooltop = ((RoutingSlipType) node).getHeaderName();
		} else if (node instanceof SplitterType) {

			this.nodeType = "Splitter";
			this.label = nodeType;
		} else if (node instanceof AggregatorType) {

			this.nodeType = "Aggregator";
			this.label = nodeType;
		} else if (node instanceof ResequencerType) {

			this.nodeType = "Resequencer";
			this.label = nodeType;
		} else if (node instanceof ThrottlerType) {
			this.nodeType = "Throttler";
			this.label = nodeType;
		} else if (node instanceof DelayerType) {
			this.nodeType = "Delayer";
			this.label = nodeType;
		}

		if (node instanceof ProcessorType && this.outputs == null) {
			ProcessorType processorType = (ProcessorType) node;
			this.outputs = processorType.getOutputs();
		}
		graphNode = new GraphNode(g, SWT.NONE, label);
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

}
