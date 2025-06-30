package com.example.canvassing.controller;

import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;
import com.example.canvassing.service.HouseholdService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class HouseholdControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @LocalServerPort
    private Integer port;

    @Autowired
    HouseholdService householdService;

    @BeforeAll
    static void beforeAll() {
    }
  
  @AfterAll
  static void afterAll() {
  }


    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void getCanvassList(){
        //retrieving a canvass list should be successful
    given()
    .auth().basic("susan_admin", "password")
    .when()
    .get("/household/canvassList?lat=29.5&lon=-96.5")
    .then()
    .statusCode(200)
    .body("questionnaire", notNullValue())
    .body("households", notNullValue());
}

    @Test
    void updateStatus() throws Exception{
    //given uncanvassed households retrieved
    RequestSpecification request = given()
    .auth().basic("susan_admin", "password");
    Response response = request.get("/households?lat=29.5&lon=-96.5");
    //and given one in particular
    JsonPath jsonPathEvaluator = response.jsonPath();
    Household household = constructHousehold(jsonPathEvaluator);

    //when its status is updated
    household.setStatus(Status.INACCESSIBLE);
    String json = mapper.writeValueAsString(household);
    given().contentType(ContentType.JSON)
    .auth().basic("susan_admin", "password")
    .body(json)
    .put("/household/status")
    .then()
    .statusCode(200);

    //then it shouldn't be in the list anymore
    request = given()
    .auth().basic("susan_admin", "password");
    response = request.get("/households?lat=29.5&lon=-96.5");
    jsonPathEvaluator = response.jsonPath();
    Household closestHousehold = constructHousehold(jsonPathEvaluator);
    assertNotEquals(household.getAddress(), closestHousehold.getAddress());

}
    private Household constructHousehold(JsonPath jsonPath) {
        int id = jsonPath.get("[0].id");
        String address = jsonPath.get("[0].address"); 
        float latitude = jsonPath.get("[0].latitude");
        float longitude = jsonPath.get("[0].longitude");
        String status = jsonPath.get("[0].status");
        Household household = new Household(address, new Location(latitude, longitude));
        household.setId((long) id);
        household.setStatus(Status.valueOf(status));
        return household;
    }
}
