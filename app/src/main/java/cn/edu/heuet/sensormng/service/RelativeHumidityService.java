package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * RelativeHumidity
 */
public class RelativeHumidityService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_RELATIVE_HUMIDITY;
	}
}
