package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * GeoMagneticRotationVector
 */
public class GeoMagneticRotationVectorService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
	}

	public static void enqueueWork(Context context, Intent work) {
		enqueueWork(context, GeoMagneticRotationVectorService.class, MyConstants.JOB_ID_GEOMAGNETICROTATIONVECTOR, work);
	}
}
