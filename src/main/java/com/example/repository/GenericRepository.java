package com.example.repository;

import com.example.exception.NotFoundException;
import com.example.exception.ValidationException;
import com.example.model.Identifiable;

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
        if (object.getId() == null) {
            throw new ValidationException("Object id cannot be null");
        }

        if (objects != null) {
            objects.add(object);
        }

        save(object);
        return object;
    }

    protected T getObjectById(final UUID id) {
        if (id == null) {
            throw new ValidationException("id cannot be null");
        }
        initializeObjects();
        for (T object : objects) {
            if (object.getId().equals(id)) {
                return object;
            }
        }

        throw new NotFoundException(String.format("id %s not found", id));
    }

    protected void deleteObjectById(final UUID id) {
        if (id == null) {
            throw new ValidationException("id cannot be null");
        }
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
