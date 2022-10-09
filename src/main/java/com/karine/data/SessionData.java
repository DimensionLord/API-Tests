package com.karine.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionData implements ISessionData {
    private final static SessionData instance = new SessionData();

    static SessionData getInstance() {
        return instance;
    }

    private final Map<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public <T> T get(String data) {
        if (!store.containsKey(data)) {
            throw new RuntimeException();
        }
        return (T) store.get(data);
    }

    @Override
    public void put(String key, Object data) {
        store.put(key, data);
    }

    @Override
    public void clear() {
        store.clear();
    }
}
