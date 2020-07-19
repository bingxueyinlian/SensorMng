package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * Gyroscope
 * 
 */
public class GyroscopeService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_GYROSCOPE;
	}
}
