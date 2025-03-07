package com.example.MiniProject1.repository;

import com.example.MiniProject1.model.Identifiable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

public abstract class GenericRepository<T extends Identifiable>
                                                    extends MainRepository<T> {

    private ArrayList<T> objects;

    protected ArrayList<T> getObjects() {
        initializeObjects();
        return objects;
    }

    protected T addObject(final T object) {
        if (objects != null) {
            objects.add(object);
        }

        save(object);
        return object;
    }

    protected T getObjectById(final UUID id) {
        initializeObjects();
        for (T object : objects) {
            if (object.getId().equals(id)) {
                return object;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("id %s not found", id));
    }

    protected void deleteObjectById(final UUID id) {
        T object = getObjectById(id);
        objects.remove(object);
        overrideData(objects);
    }

    private void initializeObjects() {
        if (objects == null) {
            objects = findAll();
        }
    }
}
