package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * Temperature
 */
public class TemperatureService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_TEMPERATURE;
	}
}