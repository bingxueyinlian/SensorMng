package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * RotationVector
 */
public class RotationVectorService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_ROTATION_VECTOR;
	}
}
