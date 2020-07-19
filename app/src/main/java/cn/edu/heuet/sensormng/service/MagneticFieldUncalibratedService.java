package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * MagneticFieldUncalibrated
 */
public class MagneticFieldUncalibratedService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
	}
}
