package com.karine.data;

public interface ISessionData {
    <T> T get(String data);

    void put(String key, Object data);

    void clear();
}
