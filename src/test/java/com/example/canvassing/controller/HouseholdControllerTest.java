package com.example.canvassing.controller;

import com.example.canvassing.model.CanvassList;
import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;
import com.example.canvassing.service.HouseholdService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.security.test.context.support.WithMockUser;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.canvassing.model.Questionnaire;

@WebMvcTest(controllers = HouseholdController.class)
@ExtendWith(MockitoExtension.class)
class HouseholdControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @MockBean
    private HouseholdService householdService;
    @MockBean
    private UpdateController updateController;
    @Autowired
    MockMvc mockMvc;


    @InjectMocks
    private HouseholdController householdController;

    @BeforeAll
    static void beforeAll() {
    }
  
    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(householdController);
    }

    @Test
    @WithMockUser("susan_admin")
    void getCanvassList() throws Exception {
    //given a canvass list with one household
    CanvassList canvassList = createCanvassList();
    when(householdService.getCanvassList(any())).thenReturn(canvassList);
    //retrieving a canvass list should be successful
    ResultActions response = mockMvc.perform(
    get("/household/canvassList?lat=29.5&lon=-96.5"));
    response.andExpect(status().isOk())
    .andExpect(content().string(containsString("123 Elm St")));
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

    private CanvassList createCanvassList() {
        Household household = new Household("123 Elm St", new Location(30.0, 60.0));
        Questionnaire questionnaire = new Questionnaire();
        return new CanvassList(List.of(household), questionnaire);
    }
}
