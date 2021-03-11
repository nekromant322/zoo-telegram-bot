package com.andrienko.telegram;

import dto.AnimalRequestDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnimalRequestDTOService {

    Map<Long, AnimalRequestDTO> cachedAnimalRequestData = new HashMap<>();

    public AnimalRequestDTO findDTOByChatId(long chatId) {
        AnimalRequestDTO animalRequestDTO;
        if (cachedAnimalRequestData.containsKey(chatId)) {
            animalRequestDTO = cachedAnimalRequestData.get(chatId);
        } else {
            animalRequestDTO = new AnimalRequestDTO();
            cachedAnimalRequestData.put(chatId, animalRequestDTO);
        }
        return animalRequestDTO;
    }

    public ResponseEntity<String> sendDTO(long chatId) {
        RestTemplate restTemplate = new RestTemplate();
        AnimalRequestDTO animalRequestDTO = cachedAnimalRequestData.get(chatId);
        Map<String, AnimalRequestDTO> params = new HashMap<>();
        params.put("animalRequestDTO", animalRequestDTO);
        ResponseEntity<String> response = restTemplate.getForEntity("localhost:8080/api/animalRequestPage", String.class, params);
        return response;
    }
}
