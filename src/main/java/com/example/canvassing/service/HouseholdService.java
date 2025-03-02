package com.example.canvassing.service;

import com.example.canvassing.model.CanvassList;
import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Questionnaire;
import com.example.canvassing.persistence.HouseholdRepository;
import com.example.canvassing.persistence.QuestionnaireRepository;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseholdService {
    @Autowired
    private HouseholdRepository householdRepository;
    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    @Autowired
    private HikariDataSource hikariDataSource;

    /**
     * get the 50 households closest to your location
     * @param location a location
     * @return 50 closest households
     */
    public List<Household> getHouseholds(@NonNull Location location) {
        //obviously this doesn't scale at all. In real life you would use a grid system or
        //Google's s2 library to figure this out
        List<Household> households = householdRepository.getUncanvassedHouseholds(location);
        if (households.size() < 50) {
            return households;
        }
        households.sort((a, b) -> location.compare(a.getLocation(), b.getLocation()));
        return households.subList(0, Math.min(households.size(), 50));
    }

    public CanvassList getCanvassList(@NonNull Location location) {
        List<Household> households = getHouseholds(location);
        Questionnaire questionnaire = questionnaireRepository.getQuestions();
        return new CanvassList(households, questionnaire);
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
