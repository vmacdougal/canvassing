package com.example.canvassing;

import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Questionnaire;
import com.example.canvassing.persistence.HouseholdRepository;
import com.example.canvassing.persistence.QuestionnaireRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Initialize the database with some example households and a canvassing questionnaire.
 * Initially everyone will be uncanvassed.
 */
@Configuration
public class LoadDatabase {
    @Autowired
    private Flyway flyway;
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    private static final Charset UTF8 = Charset.availableCharsets().get("UTF-8");

    @Bean
    CommandLineRunner initDatabase(QuestionnaireRepository questionnaireRepository, HouseholdRepository repository) {
        flyway.migrate();

        //populate the questionnaire
        Questionnaire questionnaire = new Questionnaire();
        List<String> answer1 = List.of("Yes", "No");
        questionnaire.addItem("Are you registered to vote?", answer1);
        List<String> answer2 = List.of("Yes", "Ineligible", "Refused", "Took mail-in");
        questionnaire.addItem("Can I register you today?", answer2);
        questionnaireRepository.createQuestionnaire(questionnaire);

        //insert some addresses

        Set<Household> households = readHouseholds("Addresses.csv");

        return args -> {
            for (Household household: households) {
                log.info("Preloading household {}", household);
                repository.addHousehold(household);
            }
        };
    }

    Set<Household> readHouseholds(String filename) {
        Set<Household> households = new HashSet<>();
        CSVParser parser = null;
        String currentPath = null;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            String[] headers = {"LON", "LAT", "FULL_STREET_NAME"};
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(headers)
                .setSkipHeaderRecord(true)
                .build();
            parser = CSVParser.parse(is, UTF8, csvFormat);
        }
        catch (Exception exception) {
            throw new RuntimeException(currentPath + "   Could not read in the addresses", exception);
        }
        for (CSVRecord csvRecord : parser) {
            Location location = new Location(Float.parseFloat(csvRecord.get("LAT")), Float.parseFloat(csvRecord.get("LON")));
            Household household = new Household(csvRecord.get("FULL_STREET_NAME"), location);
            households.add(household);
        }
        return households;
    }
}
