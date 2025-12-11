package com.example.ivehicle.aidl;

import com.example.ivehicle.aidl.IVehicleDataCallback;

interface IVehicleDataService {
    float getCurrentSpeed();
    float getBatteryLevel();
    boolean isCharging();

    void registerCallback(IVehicleDataCallback callback);
    void unregisterCallback(IVehicleDataCallback callback);
}
