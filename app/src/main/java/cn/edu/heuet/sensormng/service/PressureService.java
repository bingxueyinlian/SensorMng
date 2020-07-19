package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * Pressure
 */
public class PressureService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_PRESSURE;
	}
}
