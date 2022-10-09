package com.karine.payload;

public interface IUsePayload {
    default Payload payload(String title){
        return new Payload(title);
    }
}
