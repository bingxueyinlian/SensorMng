package cn.edu.heuet.sensormng.service;

import android.hardware.Sensor;

/**
 * GameRotationVector
 */
public class GameRotationVectorService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_GAME_ROTATION_VECTOR;
	}
}
