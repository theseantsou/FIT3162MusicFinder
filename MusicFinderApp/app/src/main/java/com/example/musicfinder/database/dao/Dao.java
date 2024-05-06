package com.example.musicfinder.database.dao;

import java.util.List;

public interface Dao<T> {
    long insertItem(T item);

    void deleteItem(T item);

    List<T> getAllItems();

    T getItemById(long id);
}
