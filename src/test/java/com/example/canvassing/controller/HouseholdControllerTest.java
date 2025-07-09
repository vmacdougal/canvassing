package com.example.canvassing.controller;

import com.example.canvassing.model.CanvassList;
import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;
import com.example.canvassing.service.HouseholdService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.path.json.JsonPath;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    //when retrieving a canvass list
    ResultActions response = mockMvc.perform(
    get("/household/canvassList?lat=29.5&lon=-96.5"));
    //then it should be successful
    response.andExpect(status().isOk())
    .andExpect(content().string(containsString("123 Elm St")));
}

    @Test
    @WithMockUser("susan_admin")
    void updateStatus() throws Exception{
    //given a canvass list with one household
    CanvassList canvassList = createCanvassList();
    when(householdService.getCanvassList(any())).thenReturn(canvassList);
    Household household = canvassList.getHouseholds().get(0);
    //when its status is updated
    household.setStatus(Status.INACCESSIBLE);
    String json = mapper.writeValueAsString(household);
    
    //when updating the status
    ResultActions response = mockMvc.perform(
    put("/household/status")
    .with(SecurityMockMvcRequestPostProcessors.csrf())
    .contentType(MediaType.APPLICATION_JSON)
    .content(json)
    .accept(MediaType.ALL));

    response.andExpect(status().isOk());
}

    private CanvassList createCanvassList() {
        Household household = new Household("123 Elm St", new Location(30.0, 60.0));
        household.setId(1L);
        Questionnaire questionnaire = new Questionnaire();
        return new CanvassList(List.of(household), questionnaire);
    }
}
