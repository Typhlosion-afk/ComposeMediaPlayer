package com.example.ivehicle.aidl;

import com.example.ivehicle.aidl.IVehicleDataCallback;

interface IVehicleDataService {
    float getCurrentSpeed();
    float getBatteryLevel();

    void registerCallback(IVehicleDataCallback callback);
    void unregisterCallback(IVehicleDataCallback callback);
}
