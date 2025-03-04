package com.example.canvassing.controller;

import com.example.canvassing.model.Household;
import com.example.canvassing.model.HouseholdUpdate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.SendTo;

@Controller
public class UpdateController {

    @MessageMapping("")
    @SendTo("/topic/householdUpdates")
    public HouseholdUpdate updateHouseholds(Household household, boolean created) {
        return new HouseholdUpdate(household, created);
    }
}
