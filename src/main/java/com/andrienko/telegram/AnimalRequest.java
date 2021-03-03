package com.andrienko.telegram;

import com.andrienko.telegram.enums.AnimalType;
import com.andrienko.telegram.enums.RoomType;
import com.andrienko.telegram.enums.Location;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


import java.time.LocalDate;

@Data
@Component
public class AnimalRequest {

    private static final Logger LOG = LogManager.getLogger(AnimalRequest.class);
    public AnimalType animalType;

    public RoomType roomType;

    public LocalDate beginDate;

    public LocalDate endDate;

    public Boolean videoNeeded;

    public String name;

    public String surname;

    public String animalName;

    public String phoneNumber;

    public String email;

    public Location location;

}
