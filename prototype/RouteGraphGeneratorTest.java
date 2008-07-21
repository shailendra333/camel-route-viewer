import static org.apache.camel.builder.xml.XPathBuilder.xpath;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;

public class RouteGraphGeneratorTest {
	public static void main(String[] args) throws Exception {
		// new JoinRoute();
		// new ComplexRoute();
		// new PipelineRoute();
		// new SimpleRoute();
		// new FilterRoute();
		// new MulticastRoute();
		// new ChoiceRoute();
		RouteGraphGenerator.generate(new ChoiceRoute2());
	}

	static class SimpleRoute extends RouteBuilder {
		public void configure() {
			from("direct:test").to("mock:result");
		}
	}

	static class FilterRoute extends RouteBuilder {
		public void configure() {
			from("file:target/foo/bar?noop=true").filter(
					header("foo").isEqualTo("bar")).to(
					"file:target/xyz?noop=true");
		}
	}

	static class ChoiceRoute extends RouteBuilder {
		public void configure() {
			from("file:target/foo/xyz?noop=true").choice().when(
					xpath("/person/city = 'London'")).to(
					"file:target/messages/uk").otherwise().to(
					"file:target/messages/others");
		}
	}

	static class PipelineRoute extends RouteBuilder {
		public void configure() {
			from("seda:pipeline.in").to("seda:pipeline.out1",
					"seda:pipeline.out2", "seda:pipeline.out3");
		}
	}

	static class MulticastRoute extends RouteBuilder {
		public void configure() {
			from("seda:multicast.in").multicast().to("seda:multicast.out1",
					"seda:multicast.out2", "seda:multicast.out3");
		}
	}

	static class ComplexRoute extends RouteBuilder {
		public void configure() {
			from("file:target/xyz?noop=true").filter(
					header("foo").isEqualTo("bar"))
					.recipientList(header("bar")).splitter(
							XPathBuilder.xpath("/invoice/lineItems"))
					.throttler(3).to("mock:result");

			// from("seda:b").delayer(3000).to("mock:result");
		}
	}

	static class JoinRoute extends RouteBuilder {
		public void configure() {
			from("direct:a").choice().when(header("foo").isEqualTo("bar")).to(
					"direct:b").when(header("foo").isEqualTo("cheese")).to(
					"direct:c").otherwise().to("direct:d");

			from("direct:b").to("mock:result");
			from("direct:c").to("mock:result");
			from("direct:d").to("mock:result");
		}
	}

	static class ChoiceRoute2 extends RouteBuilder {
		public void configure() {
			from("direct:start").choice().when(header("foo").isEqualTo("bar"))
					.setHeader("name", constant("a")).to("mock:x").when(
							header("foo").isEqualTo("cheese")).to("mock:y")
					.otherwise().to("mock:z");
		}
	}
}
