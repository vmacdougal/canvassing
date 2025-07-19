package com.example.canvassing;

import com.example.canvassing.model.Household;
import java.util.Collection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadDatabaseTest {

    @Test
    void readHouseholds() {
        LoadDatabase loadDatabase = new LoadDatabase();
        Collection<Household> households = loadDatabase.readHouseholds("Addresses.csv");
        assertEquals(9998, households.size());
    }
}
