package com.tradeshift.juliofalbo.challenge.tradeshift.resource;

import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Integration test responsible for the tree resource")
class TreeResourceTest {

    @LocalServerPort
    private Integer serverPort;

    @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
    }

    @Test
    void insertARootNode() {
        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", true, true))
                .when()
                .post("/trees")
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    void receiveErrorThatOnlyOneRootCanBeExists() {
        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, false))
                .when()
                .post("/trees")
                .then()
                .log().all()
                .statusCode(201);

        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", true, true))
                .when()
                .post("/trees")
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("Only one root tree can be exists"));
    }

    @Test
    void inserNodeWithoutPassHasLeftNodeAndHasRightNode() {
        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", null, null))
                .when()
                .post("/trees")
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    void insertNodeWithParentIdThatNotExists() {
        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("3212142151tgf", true, true))
                .when()
                .post("/trees")
                .then()
                .log().all()
                .statusCode(404)
                .body("message", Matchers.equalTo("No exist tree with id 3212142151tgf"));
    }

    @Test
    void insertNodeWithParentIdThatExists() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);

        String idRoot = returnCreatedId(response);
        Response secondResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, true, true))
                .when()
                .post("/trees");

        secondResponse.then()
                .log().all()
                .statusCode(201);

        String idChildren = returnCreatedId(secondResponse);

        ValidatableResponse validatableResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .when()
                .get("/trees/" + idChildren)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("id", Matchers.equalTo(idChildren));

        HashMap<String, String> parentUrl = validatableResponse.extract().
                path("_links.parent");

        Assertions.assertThat(parentUrl.get("href")).contains(idRoot);
    }

    @Test
    void verifyHeightOfTheNodes() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, true, false))
                .when()
                .post("/trees");
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(secondNode, true, true))
                .when()
                .post("/trees");
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        RestAssured
                .given()
                .contentType("application/json\r\n")
                .when()
                .get("/trees/" + idRoot)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("height", Matchers.equalTo(0));

        RestAssured
                .given()
                .contentType("application/json\r\n")
                .when()
                .get("/trees/" + secondNode)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("height", Matchers.equalTo(1));

        RestAssured
                .given()
                .contentType("application/json\r\n")
                .when()
                .get("/trees/" + thirdNode)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("height", Matchers.equalTo(2));

    }

    @Test
    void verifyChildrens() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, true, false))
                .when()
                .post("/trees");
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(secondNode, true, true))
                .when()
                .post("/trees");
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        RestAssured
                .given()
                .contentType("application/json\r\n")
                .when()
                .get("/trees/" + idRoot + "/childrens")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("totalElements", Matchers.equalTo(6));

        RestAssured
                .given()
                .contentType("application/json\r\n")
                .when()
                .get("/trees/" + secondNode + "/childrens")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("totalElements", Matchers.equalTo(4));

        RestAssured
                .given()
                .contentType("application/json\r\n")
                .when()
                .get("/trees/" + thirdNode + "/childrens")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("totalElements", Matchers.equalTo(2));
    }

    @Test
    void tryingToInsertANodeIntoAFullTree() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", true, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, true, false))
                .when()
                .post("/trees")
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("Parent Tree is Full"));
    }

    @Test
    void tryToChangeTheParentForOneOfYourChildren() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, true, false))
                .when()
                .post("/trees");
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(secondNode, true, false))
                .when()
                .post("/trees");
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(thirdNode))
                .when()
                .patch("/trees/" + secondNode)
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("The parent choiced is already your children"));

    }

    @Test
    void tryToInsertMyselfAsParent() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, true, false))
                .when()
                .post("/trees");
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(secondNode))
                .when()
                .patch("/trees/" + secondNode)
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("You can not insert yourself as parent"));
    }

    @Test
    void tryToChangeForTheSameParent() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, true, false))
                .when()
                .post("/trees");
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot))
                .when()
                .patch("/trees/" + secondNode)
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("The parent is already his parent"));
    }

    @Test
    void updateAParentWithSuccess() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, false, false))
                .when()
                .post("/trees");
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(secondNode, true, false))
                .when()
                .post("/trees");
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        Response fourthResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(thirdNode, true, false))
                .when()
                .post("/trees");
        fourthResponse.then()
                .log().all()
                .statusCode(201);
        String fourthNode = returnCreatedId(fourthResponse);


        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(secondNode))
                .when()
                .patch("/trees/" + fourthNode)
                .then()
                .log().all()
                .statusCode(200);


        ValidatableResponse validatableResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .when()
                .get("/trees/" + fourthNode)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("id", Matchers.equalTo(fourthNode));

        HashMap<String, String> parentUrl = validatableResponse.extract().
                path("_links.parent");

        Assertions.assertThat(parentUrl.get("href")).contains(secondNode);
    }

    @Test
    void tryingToMakeAChangeThatWillCauseStackOverflowError() {
        Response response = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest("", false, true))
                .when()
                .post("/trees");

        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(idRoot, false, false))
                .when()
                .post("/trees");
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(secondNode, true, false))
                .when()
                .post("/trees");
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        Response fourthResponse = RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(thirdNode, true, false))
                .when()
                .post("/trees");
        fourthResponse.then()
                .log().all()
                .statusCode(201);
        String fourthNode = returnCreatedId(fourthResponse);


        RestAssured
                .given()
                .contentType("application/json\r\n")
                .body(new TreeRequest(fourthNode))
                .when()
                .patch("/trees/" + secondNode)
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("Your change will genarate a StackOverFlowError"));
    }

    private String returnCreatedId(Response response) {
        String[] split = response.getHeader("Location").split("/");
        return split[split.length - 1];
    }

}
