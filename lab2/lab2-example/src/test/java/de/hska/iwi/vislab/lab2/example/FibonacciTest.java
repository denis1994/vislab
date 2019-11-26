package de.hska.iwi.vislab.lab2.example;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

public class FibonacciTest {

	private HttpServer server;
	private WebTarget target;

	@Before
	public void setUp() throws Exception {
		// start the server
		server = Main.startServer();
		// create the client
		Client c = ClientBuilder.newClient();

		// uncomment the following line if you want to enable
		// support for JSON in the client (you also have to uncomment
		// dependency on jersey-media-json module in pom.xml and
		// Main.startServer())
		// --
		// c.configuration().enable(new
		// org.glassfish.jersey.media.json.JsonJaxbFeature());

		target = c.target(Main.BASE_URI);
	}

	@After
	public void tearDown() throws Exception {
		server.shutdown();
	}

	@Test
	public void getInitalState() {
		String responseMsg = target.path("fibonacci").request().accept(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"n\": 0, \"fibonacci\": 0}", responseMsg);
	}

	@Test
	public void getNextFibonacciNumber() {
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		String responseMsg = target.path("fibonacci").request().accept(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"n\": 1, \"fibonacci\": 1}", responseMsg);
	}

	@Test
	public void getFibonacciNumber8() {
		// reset state
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).delete();
		
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		String responseMsg = target.path("fibonacci").request().accept(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"n\": 8, \"fibonacci\": 21}", responseMsg);
	}

	@Test
	public void getFibonacciNumber10() {
		// reset state
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).delete();

		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		String responseMsg = target.path("fibonacci").request().accept(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"n\": 10, \"fibonacci\": 55}", responseMsg);
	}

	@Test
	public void restoreInitialState() {
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).put(Entity.json(""));
		target.path("fibonacci").request().accept(MediaType.TEXT_PLAIN).delete();
		String responseMsg = target.path("fibonacci").request().accept(MediaType.APPLICATION_JSON).get(String.class);
		assertEquals("{\"n\": 0, \"fibonacci\": 0}", responseMsg);
	}
}
