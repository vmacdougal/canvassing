package com.example.demo.service;

import com.example.demo.model.Household;
import com.example.demo.model.HouseholdResponse;
import com.example.demo.model.Response;
import com.example.demo.model.Status;
import com.example.demo.persistence.HouseholdRepository;
import com.example.demo.persistence.ResponseRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseService {
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private HouseholdRepository householdRepository;

    public boolean addResponse(Response response) {
        return responseRepository.addResponse(response);
    }

    public boolean addResponses(@NonNull List<Response> responses) {
        return responseRepository.addResponses(responses);
    }

    public List<HouseholdResponse> getResponses() {
        List<Household> canvassed = householdRepository.getCanvassedHouseholds();
        List<HouseholdResponse> responses = new ArrayList<>();
        for (Household household : canvassed) {
            HouseholdResponse householdResponse = new HouseholdResponse(household, new ArrayList<>());
            //canvassed is the only status that could reasonably have responses
            if (Status.CANVASSED.equals(household.getStatus())) {
                householdResponse.setResponses(responseRepository.getResponses(household.getId()));
            }
            responses.add(householdResponse);
        }
        return responses;
    }


}
