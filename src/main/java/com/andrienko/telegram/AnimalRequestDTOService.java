package com.andrienko.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import dto.AnimalRequestDTO;
import enums.AnimalType;
import enums.Location;
import enums.RequestStatus;
import enums.RoomType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnimalRequestDTOService {
    @Value("${url")
    String url;
    Map<Long, AnimalRequestDTO> cachedAnimalRequestData = new HashMap<>();

    public AnimalRequestDTO findDTOByChatId(long chatId) {
//        AnimalRequestDTO animalRequestDTO;
//        if (cachedAnimalRequestData.containsKey(chatId)) {
//            animalRequestDTO = cachedAnimalRequestData.get(chatId);
//            animalRequestDTO.setRequestStatus(RequestStatus.NEW);
//        } else {
//            animalRequestDTO = new AnimalRequestDTO();
//            animalRequestDTO.setRequestStatus(RequestStatus.NEW);
//            cachedAnimalRequestData.put(chatId, animalRequestDTO);
//
//        }
        return cachedAnimalRequestData.putIfAbsent(chatId, createEmptyAnimalRequest());
    }

    public void sendDTO(long chatId) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
            }
        }).create();


        AnimalRequestDTO animalRequestDTO = cachedAnimalRequestData.get(chatId);

        HttpEntity<String> entity = new HttpEntity<String>(gson.toJson(animalRequestDTO), headers);

        String responseEntity = restTemplate.postForObject(url, entity, String.class);
    }

    private AnimalRequestDTO createEmptyAnimalRequest() {
        AnimalRequestDTO animalRequestDTO = new AnimalRequestDTO();
        animalRequestDTO.setRequestStatus(RequestStatus.NEW);
        return animalRequestDTO;
    }
}
