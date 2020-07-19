package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * GeoMagneticRotationVector
 */
public class GeoMagneticRotationVectorService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
	}
}
