package com.karine.driver;

public interface IUseApiDriver {
    default ApiDriver getDriver(){
        return ApiDriver.getInstance();
    }
}
