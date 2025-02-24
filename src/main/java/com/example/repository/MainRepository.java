package com.example.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Primary
@Repository
public abstract class MainRepository<T> {

    protected ObjectMapper objectMapper = new ObjectMapper();
    
    protected abstract String getDataPath();
    protected abstract Class<T[]> getArrayType();

    public MainRepository(){

    }
    public ArrayList<T> findAll() {
        try {
            File file = new File(getDataPath());
            if (!file.exists()) {
                return new ArrayList<>();
            }
            T[] array = objectMapper.readValue(file, getArrayType()); // Deserialize to array first
            return new ArrayList<>(Arrays.asList(array));
            // return objectMapper.readValue(file, new TypeReference<ArrayList<T>>(){});
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from JSON file", e);
        }
    }

    public void saveAll(ArrayList<T> data) {
        try {
            objectMapper.writeValue(new File(getDataPath()), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to JSON file", e);
        }
    }

    public void save(T data){
        ArrayList<T> allData = findAll();
        allData.add(data);
        saveAll(allData);
    }



    public void overrideData(ArrayList<T> data) {
        saveAll(data);
    }

    


}
