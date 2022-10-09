package com.karine.data;

public interface IUseSessionData {
    default ISessionData sessionData() {
        return SessionData.getInstance();
    }
}
