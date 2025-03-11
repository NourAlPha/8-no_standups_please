package com.example.service;


import com.example.exception.InvalidActionException;
import com.example.exception.NotFoundException;
import com.example.exception.ValidationException;
import com.example.model.Identifiable;
import com.example.repository.GenericRepository;

import java.util.ArrayList;
import java.util.UUID;

public abstract class MainService<T extends Identifiable,
        K extends GenericRepository<T>> {

    private final K repository;

    public MainService(final K repository) {
        this.repository = repository;
    }

    public T addObject(final T object) {
        checkObject(object);
        checkId(object.getId());
        try {
            return repository.addObject(object);
        } catch (InvalidActionException e) {
            throw new InvalidActionException("Object "
                    + object.getId() + " already exists");
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to add object", e);
        }
    }

    public ArrayList<T> getObjects() {
        try {
            return repository.getObjects();
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to get objects", e);
        }
    }

    public T getObjectById(final UUID id) {
        checkId(id);
        try {
            return repository.getObjectById(id);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object with id: " + id,
                    e);
        }
    }

    public void deleteObjectById(final UUID id) {
        checkId(id);
        try {
            repository.deleteObjectById(id);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user with id: " + id,
                    e);
        }
    }

    protected void checkId(final UUID id) {
        if (id == null) {
            throw new ValidationException("id is null");
        }
    }

    protected void checkObject(final T object) {
        if (object == null) {
            throw new ValidationException("object is null");
        }
    }
}
