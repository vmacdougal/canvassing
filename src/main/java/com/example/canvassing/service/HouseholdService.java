package com.example.canvassing.service;

import com.example.canvassing.model.*;
import com.example.canvassing.persistence.HouseholdRepository;
import com.example.canvassing.persistence.QuestionnaireRepository;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseholdService {
    @Autowired
    private HouseholdRepository householdRepository;
    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    Logger logger = LoggerFactory.getLogger(HouseholdService.class);

    /**
     * get the 50 households closest to your location
     * @param location a location
     * @return 50 closest households
     */
    public List<Household> getHouseholds(@NonNull Location location) {
        //obviously this doesn't scale at all. In real life you would use a grid system or
        //Google's s2 library or something to figure this out
        List<Household> households = householdRepository.getUncanvassedHouseholds(location);
        if (households.size() < 50) {
            return households;
        }
        households.sort((a, b) -> location.compare(a.getLocation(), b.getLocation()));
        return households.subList(0, Math.min(households.size(), 50));
    }

    /**
     * get the 50 households closest to your location, along with the canvassing questionnaire
     * @param location the canvasser's location
     * @return the canvass list (households, questions, and answers)
     */
    public CanvassList getCanvassList(@NonNull Location location) {
        List<Household> households = getHouseholds(location);
        Questionnaire questionnaire = questionnaireRepository.getQuestions();
        return new CanvassList(households, questionnaire);
    }

    public Household getHousehold(int id) {
        return householdRepository.getHousehold(id);
    }

    public void resetStatus() {
        List<Household> canvassedHouseholds = householdRepository.getCanvassedHouseholds();
        for (Household household : canvassedHouseholds) {
            //wrap each one in a try-catch block so we still do the rest of the batch
            //if one fails for some reason
            try {
                household.setStatus(Status.UNCANVASSED);
                setStatus(household);
            }
            catch (RuntimeException e) {
                logger.error("Could not update household {}", household, e);
            }
        }

    }

    public boolean setStatus(@NonNull Household household) {
        return householdRepository.setStatus(household);
    }

    public boolean addHousehold(@NonNull Household household) {
        return householdRepository.addHousehold(household);
    }

    public boolean removeHousehold(int id) {
        return householdRepository.removeHousehold(id);
    }
}
