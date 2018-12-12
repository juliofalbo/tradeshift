package com.tradeshift.juliofalbo.challenge.tradeshift;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TradeshiftChallengeApplicationTests {

	@LocalServerPort
	private Integer serverPort;

	@BeforeEach
	public void setUp() {
		RestAssured.port = serverPort;
	}

	@Test
	public void testActuatorHeartbeat() {
		RestAssured
				.given()
				.log().all()
				.when()
				.get("/actuator/health")
				.then()
				.log().all()
				.body("status", Matchers.is("UP"));
	}

}
