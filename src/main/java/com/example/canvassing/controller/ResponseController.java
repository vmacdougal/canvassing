package com.example.canvassing.controller;

import com.example.canvassing.model.Household;
import com.example.canvassing.model.HouseholdResponse;
import com.example.canvassing.model.Response;
import com.example.canvassing.service.HouseholdService;
import com.example.canvassing.service.ResponseService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import java.util.List;

@RestController
public class ResponseController {
    @Autowired
    private ResponseService responseService;
    @Autowired
    private HouseholdService householdService;
    @Autowired
    private UpdateController updateController;
    private final Logger logger = LoggerFactory.getLogger(ResponseController.class);

    /**
     * post all the responses for a successfully canvassed household. It is a list because
     * the canvasser often has more than one question to ask
     * @param responses household's responses
     * @return success
     */
    @PostMapping("/response")
    public ResponseEntity<Boolean> addResponses(@RequestBody List<Response> responses) {
        if (responses.isEmpty()) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        boolean success =  responseService.addResponses(responses);
        //send updates to logged-in users.
        //don't show the canvasser an error if the real-time update fails. Log and move on
        try {
            long householdId = responses.get(0).getHouseholdId();
            Household household = householdService.getHousehold(householdId);
            updateController.updateHouseholds(household, false);
        }
        catch (Exception e) {
            logger.error("Could not update canvassers", e);
        }
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    /**
     * Returns results for households that were at least attempted
     */
    @GetMapping("/response")
    public ResponseEntity<List<HouseholdResponse>> getResponses() {
        return new ResponseEntity<>(responseService.getResponses(), HttpStatus.OK);
    }

}
