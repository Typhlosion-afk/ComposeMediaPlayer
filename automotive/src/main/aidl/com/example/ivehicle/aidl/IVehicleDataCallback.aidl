package com.example.ivehicle.aidl;

interface IVehicleDataCallback {
    void onSpeedChanged(float speed);
    void onBatteryLevelChanged(float level);
    void onChargingStateChanged(boolean charging);
}