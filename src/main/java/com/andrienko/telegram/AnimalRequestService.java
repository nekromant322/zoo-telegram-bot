package com.andrienko.telegram;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AnimalRequestService {
    private Set<AnimalRequest> animalRequestSet;

    public AnimalRequestService(){
        animalRequestSet = new HashSet<>();
    }

    public boolean addAnimalRequest(AnimalRequest animalRequest){
        return  animalRequestSet.add(animalRequest);
    }
}
