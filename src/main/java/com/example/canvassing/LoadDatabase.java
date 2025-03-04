package com.example.canvassing;

import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Questionnaire;
import com.example.canvassing.persistence.HouseholdRepository;
import com.example.canvassing.persistence.QuestionnaireRepository;
import com.example.canvassing.persistence.StatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Initialize the database with some example households and a canvassing questionnaire.
 * Initially everyone will be uncanvassed.
 */
@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    private static final List<String> STREET_NAMES = List.of("Elm", "Oak", "Limon", "Spicebrush", "Cliffwood", "Puerta Vista", "Walnut");
    private static final List<String> STREET_TYPES = List.of("Ln", "St", "Blvd", "Way", "Cove");
    private static final String ADDRESS_FORMAT = "%d %s %s";
    private final Random random = new Random();
    @Bean
    CommandLineRunner initDatabase(QuestionnaireRepository questionnaireRepository, HouseholdRepository repository, StatusRepository statusRepository) {
        //populate the statuses
        statusRepository.addStatuses();

        //populate the questionnaire
        Questionnaire questionnaire = new Questionnaire();
        List<String> answer1 = List.of("Yes", "No");
        questionnaire.addItem("Are you registered to vote?", answer1);
        List<String> answer2 = List.of("Yes", "Ineligible", "Refused", "Took mail-in");
        questionnaire.addItem("Can I register you today?", answer2);
        questionnaireRepository.createQuestionnaire(questionnaire);

        //insert some addresses
        Set<String> addresses = new HashSet<>();

        return args -> {
            for (int i = 0; i < 500; i++) {
                Location location = new Location(29.0 + random.nextDouble(), -97 + random.nextDouble());
                String address = pickUniqueAddress(addresses);
                Household household = new Household(address, location);
                log.info("Preloading household {}", household);
                repository.addHousehold(household);
            }
        };
    }
    private String pickUniqueAddress(Set<String> addresses) {
        String address = ADDRESS_FORMAT.formatted(random.nextInt(10000), STREET_NAMES.get(random.nextInt(STREET_NAMES.size())), STREET_TYPES.get(random.nextInt(STREET_TYPES.size())));
        while (addresses.contains(address)) {
            address = ADDRESS_FORMAT.formatted(random.nextInt(10000), STREET_NAMES.get(random.nextInt(STREET_NAMES.size())), STREET_TYPES.get(random.nextInt(STREET_TYPES.size())));
        }
        addresses.add(address);
        return address;
    }


}
