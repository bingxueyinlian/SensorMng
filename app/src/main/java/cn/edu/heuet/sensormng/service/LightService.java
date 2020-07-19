package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * Light
 */
public class LightService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_LIGHT;
	}
}
