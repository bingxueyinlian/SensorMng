package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * GyroscopeUncalibrated
 */
public class GyroscopeUncalibratedService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_GYROSCOPE_UNCALIBRATED;
	}
}
