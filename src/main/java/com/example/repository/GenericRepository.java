package com.example.repository;

import com.example.exception.NotFoundException;
import com.example.model.Identifiable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Primary
@Repository
public abstract class GenericRepository<T extends Identifiable>
        extends MainRepository<T> {

    private ArrayList<T> objects;

    public ArrayList<T> getObjects() {
        initializeObjects();
        return objects;
    }

    public T addObject(final T object) {
        if (objects != null) {
            objects.add(object);
        }
        save(object);
        return object;
    }

    public T getObjectById(final UUID id) {
        initializeObjects();
        for (T object : objects) {
            if (object.getId().equals(id)) {
                return object;
            }
        }

        throw new NotFoundException(String.format("id %s not found", id));
    }

    public void deleteObjectById(final UUID id) {
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
