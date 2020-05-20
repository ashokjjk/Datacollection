## Sensor dataset builder
- Helps to build the dataset of sensor data from 3 different sensors
- Has 11 combination of data variables for each sensors
- CO, O3, Pressure, Temperature, Sound, PM2.4, PM10, PM1, PM4, NO2, NO, Humidity
- Collects the last 24 hours of data from Newcastle Urban Observatory API
<p>You can used docker image to collect the data</p>
```bash
docker run -it ashokjjk/datacoll
```