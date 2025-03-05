package com.example.canvassing.controller;

import com.example.canvassing.model.HouseholdResponse;
import com.example.canvassing.model.Response;
import com.example.canvassing.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ResponseController {
    @Autowired
    private ResponseService responseService;

    /**
     * post all the responses for a successfully canvassed household. It is a list because
     * the canvasser often has more than one question to ask
     * @param responses household's responses
     * @return success
     */
    @PostMapping("/response")
    public ResponseEntity<Boolean> addResponses(@RequestBody List<Response> responses) {
        return new ResponseEntity<>(responseService.addResponses(responses), HttpStatus.OK);
    }

    /**
     * Returns results for households that were at least attempted
     */
    @GetMapping("/response")
    public ResponseEntity<List<HouseholdResponse>> getResponses() {
        return new ResponseEntity<>(responseService.getResponses(), HttpStatus.OK);
    }

}
