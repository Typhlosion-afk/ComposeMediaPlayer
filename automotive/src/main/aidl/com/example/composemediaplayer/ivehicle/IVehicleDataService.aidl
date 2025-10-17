package com.example.composemediaplayer.ivehicle;

import com.example.composemediaplayer.ivehicle.IVehicleDataCallback;

interface IVehicleDataService {
    float getCurrentSpeed();
    float getBatteryLevel();

    void registerCallback(IVehicleDataCallback callback);
    void unregisterCallback(IVehicleDataCallback callback);
}
