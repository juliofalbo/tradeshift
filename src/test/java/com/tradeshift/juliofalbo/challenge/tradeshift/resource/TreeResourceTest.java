package com.tradeshift.juliofalbo.challenge.tradeshift.resource;

import com.tradeshift.juliofalbo.challenge.tradeshift.dto.TreeRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
        sendPostRequestAndReturnValidatableResponse(new TreeRequest("", true, true))
                .statusCode(201);
    }

    @Test
    void receiveErrorThatOnlyOneRootCanBeExists() {
        sendPostRequestAndReturnValidatableResponse(new TreeRequest("", false, false))
                .statusCode(201);

        sendPostRequestAndReturnValidatableResponse(new TreeRequest("", true, true))
                .statusCode(400)
                .body("message", Matchers.equalTo("Only one root tree can be exists"));
    }

    @Test
    void inserNodeWithoutPassHasLeftNodeAndHasRightNode() {
        sendPostRequestAndReturnValidatableResponse(new TreeRequest("", null, null))
                .statusCode(201);
    }

    @Test
    void insertNodeWithParentIdThatNotExists() {
        sendPostRequestAndReturnValidatableResponse(new TreeRequest("3212142151tgf", true, true))
                .statusCode(404)
                .body("message", Matchers.equalTo("No exist tree with id 3212142151tgf"));
    }

    @Test
    void insertNodeWithParentIdThatExists() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", false, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = sendPostRequestAndReturnResponse(new TreeRequest(idRoot, true, true));
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String idChildren = returnCreatedId(secondResponse);

        ValidatableResponse validatableResponse = sendGetRequestForFindById(idChildren)
                .statusCode(200)
                .assertThat()
                .body("id", Matchers.equalTo(idChildren));

        HashMap<String, String> parentUrl = validatableResponse.extract().
                path("_links.parent");

        Assertions.assertThat(parentUrl.get("href")).contains(idRoot);
    }

    @Test
    void verifyHeightOfTheNodes() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", false, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = sendPostRequestAndReturnResponse(new TreeRequest(idRoot, true, false));
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = sendPostRequestAndReturnResponse(new TreeRequest(secondNode, true, true));
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        sendGetRequestForFindById(idRoot)
                .statusCode(200)
                .assertThat()
                .body("height", Matchers.equalTo(0));

        sendGetRequestForFindById(secondNode)
                .statusCode(200)
                .assertThat()
                .body("height", Matchers.equalTo(1));

        sendGetRequestForFindById(thirdNode)
                .statusCode(200)
                .assertThat()
                .body("height", Matchers.equalTo(2));

    }

    @Test
    void verifyChildrens() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", false, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = sendPostRequestAndReturnResponse(new TreeRequest(idRoot, true, false));
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = sendPostRequestAndReturnResponse(new TreeRequest(secondNode, true, true));
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        sendAGetRequestForGetAllChildrens(idRoot)
                .statusCode(200)
                .assertThat()
                .body("totalElements", Matchers.equalTo(6));

        sendAGetRequestForGetAllChildrens(secondNode)
                .statusCode(200)
                .assertThat()
                .body("totalElements", Matchers.equalTo(4));

        sendAGetRequestForGetAllChildrens(thirdNode)
                .statusCode(200)
                .assertThat()
                .body("totalElements", Matchers.equalTo(2));
    }

    @Test
    void tryingToInsertANodeIntoAFullTree() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", true, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        sendPostRequestAndReturnValidatableResponse(new TreeRequest(idRoot, true, false))
                .statusCode(400)
                .body("message", Matchers.equalTo("Parent Tree is Full"));
    }

    @Test
    void tryToChangeTheParentForOneOfYourChildren() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", false, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = sendPostRequestAndReturnResponse(new TreeRequest(idRoot, true, false));
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = sendPostRequestAndReturnResponse(new TreeRequest(secondNode, true, false));
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        sendPatchRequestForUpdateParent(secondNode, thirdNode)
                .statusCode(400)
                .body("message", Matchers.equalTo("The parent choiced is already your children"));

    }

    @Test
    void tryToInsertMyselfAsParent() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", false, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = sendPostRequestAndReturnResponse(new TreeRequest(idRoot, true, false));
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        sendPatchRequestForUpdateParent(secondNode, secondNode)
                .statusCode(400)
                .body("message", Matchers.equalTo("You can not insert yourself as parent"));
    }

    @Test
    void tryToChangeForTheSameParent() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", false, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = sendPostRequestAndReturnResponse(new TreeRequest(idRoot, true, false));
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        sendPatchRequestForUpdateParent(secondNode, idRoot)
                .statusCode(400)
                .body("message", Matchers.equalTo("The parent is already his parent"));
    }

    @Test
    void updateAParentWithSuccess() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", false, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = sendPostRequestAndReturnResponse(new TreeRequest(idRoot, false, false));
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        Response thirdResponse = sendPostRequestAndReturnResponse(new TreeRequest(secondNode, true, false));
        thirdResponse.then()
                .log().all()
                .statusCode(201);
        String thirdNode = returnCreatedId(thirdResponse);


        Response fourthResponse = sendPostRequestAndReturnResponse(new TreeRequest(thirdNode, true, false));
        fourthResponse.then()
                .log().all()
                .statusCode(201);
        String fourthNode = returnCreatedId(fourthResponse);


        sendPatchRequestForUpdateParent(fourthNode, secondNode)
                .statusCode(200);


        ValidatableResponse validatableResponse = sendGetRequestForFindById(fourthNode)
                .statusCode(200)
                .assertThat()
                .body("id", Matchers.equalTo(fourthNode));

        HashMap<String, String> parentUrl = validatableResponse.extract().
                path("_links.parent");

        Assertions.assertThat(parentUrl.get("href")).contains(secondNode);
    }

    @Test
    void tryToInsertAParentInARoot() {
        Response response = sendPostRequestAndReturnResponse(new TreeRequest("", false, true));
        response.then()
                .log().all()
                .statusCode(201);
        String idRoot = returnCreatedId(response);

        Response secondResponse = sendPostRequestAndReturnResponse(new TreeRequest(idRoot, false, false));
        secondResponse.then()
                .log().all()
                .statusCode(201);
        String secondNode = returnCreatedId(secondResponse);

        sendPatchRequestForUpdateParent(idRoot, secondNode)
                .statusCode(400)
                .body("message", Matchers.equalTo("It is not possible to insert a parent in a root"));
    }

    private String returnCreatedId(Response response) {
        String[] split = response.getHeader("Location").split("/");
        return split[split.length - 1];
    }

    private ValidatableResponse sendPostRequestAndReturnValidatableResponse(TreeRequest treeRequest) {
        return sendPostRequestAndReturnResponse(treeRequest)
                .then()
                .log().all();
    }

    private Response sendPostRequestAndReturnResponse(TreeRequest treeRequest) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(treeRequest)
                .when()
                .post("/trees");
    }

    private ValidatableResponse sendGetRequestForFindById(String idNode) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/trees/" + idNode)
                .then()
                .log().all();
    }

    private ValidatableResponse sendAGetRequestForGetAllChildrens(String idNode) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/trees/" + idNode + "/childrens")
                .then()
                .log().all();
    }

    private ValidatableResponse sendPatchRequestForUpdateParent(String currentNode, String newParent) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(new TreeRequest(newParent))
                .when()
                .patch("/trees/" + currentNode)
                .then()
                .log().all();
    }

}
