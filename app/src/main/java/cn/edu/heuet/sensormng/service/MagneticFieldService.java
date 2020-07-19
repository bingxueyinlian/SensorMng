package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * MagneticField
 */
public class MagneticFieldService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_MAGNETIC_FIELD;
	}
}
