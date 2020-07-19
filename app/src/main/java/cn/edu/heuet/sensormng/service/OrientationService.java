package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * Orientation
 */
public class OrientationService extends AbstractSensorService {

	@SuppressWarnings("deprecation")
	@Override
	int getSensorType() {
		return Sensor.TYPE_ORIENTATION;
	}
}
