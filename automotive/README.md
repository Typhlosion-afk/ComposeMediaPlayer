+------------------------+
|   VehicleDataService   |
| (Service, running in   |
|  a separate process)   |
|                        |
|  - currentSpeed        |
|  - batteryLevel        |
|                        |
|  + registerCallback()  |
|  + unregisterCallback()|
+------------------------+
^
| (1) registerCallback()
|
v
+------------------------+
| VehicleDataServiceAdapter |
| (Adapter Pattern)         |
|                            |
|  - speedFlow : StateFlow  |
|  - batteryFlow : StateFlow|
|                            |
|  + connect()              |
|  + disconnect()           |
+------------------------+
^
| (2) observe StateFlow
|
v
+------------------------+
|   VehicleStatusViewModel |
| (Observer Pattern)       |
|                          |
|  speed : StateFlow<Float>|
|  energyLevel : StateFlow<Float>|
+------------------------+
^
| (3) observe StateFlow
|
v
+------------------------+
|          UI (Compose)   |
| (Observer Pattern)      |
+------------------------+
