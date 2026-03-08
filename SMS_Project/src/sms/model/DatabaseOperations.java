package sms.model;

import java.util.List;

public interface DatabaseOperations<T> {
    boolean add(T obj);
    boolean update(T obj);
    boolean delete(int id);
    List<T> search(String keyword);
    List<T> getAll();
}