package com.example.demo.controller;

import com.example.demo.model.HouseholdResponse;
import com.example.demo.model.Response;
import com.example.demo.service.ResponseService;
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

    @PostMapping("/response")
    public ResponseEntity<Boolean> addResponses(@RequestBody List<Response> responses) {
        return new ResponseEntity<>(responseService.addResponses(responses), HttpStatus.OK);
    }

    /**
     * Returns results for households that were at least attempted
     * @return
     */
    @GetMapping("/response")
    public ResponseEntity<List<HouseholdResponse>> getResponses() {
        return new ResponseEntity<>(responseService.getResponses(), HttpStatus.OK);
    }

}
