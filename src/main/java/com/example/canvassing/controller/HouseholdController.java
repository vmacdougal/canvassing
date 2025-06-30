package com.example.canvassing.controller;

import com.example.canvassing.exception.DuplicateAddressException;
import com.example.canvassing.model.CanvassList;
import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.service.HouseholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class HouseholdController {
    @Autowired
    private HouseholdService householdService;
    @Autowired
    private UpdateController updateController;

    /**
     * get the 50 uncanvassed households closest to your location
     * @param location a location
     * @return 50 closest households
     */
    @GetMapping("/households")
    public ResponseEntity<List<Household>> getHouseholds(@RequestParam double lat,
                                         @RequestParam double lon) {
        Location location = new Location(lat, lon);
        return new ResponseEntity<>(householdService.getHouseholds(location), HttpStatus.OK);
    }

    @GetMapping("/household/canvassList")
    public ResponseEntity<CanvassList> getCanvassList(@RequestParam double lat,
                                      @RequestParam double lon) {
        Location location = new Location(lat, lon);
        return new ResponseEntity<>(householdService.getCanvassList(location), HttpStatus.OK);
    }

    @PutMapping("/household/status")
    public ResponseEntity<Boolean> setStatus(@RequestBody Household household) {
        if (household.getId() == null ||  household.getStatus() == null) {
            throw new RuntimeException("This household does not have enough information to update");
        }
        return new ResponseEntity<>(householdService.setStatus(household), HttpStatus.OK);
    }

    @PostMapping("/household")
    public ResponseEntity<String> addHousehold(@RequestBody Household household) {
        try {
            householdService.addHousehold(household);
            var response = new ResponseEntity<>("success", HttpStatus.OK);
            updateController.updateHouseholds(household, true);
            return response;
        }
        catch (DuplicateAddressException e) {
            return new ResponseEntity<>("This address already exists", HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/household/{id}")
    public ResponseEntity<Boolean> removeHousehold(@PathVariable int id) {
        Household household = householdService.getHousehold(id);
        var response = new ResponseEntity<>(householdService.removeHousehold(id), HttpStatus.OK);
        updateController.updateHouseholds(household, false);
        return response;
    }

}
