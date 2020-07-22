package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * Accelerometer
 */
public class AccelerometerService extends AbstractSensorService {

	@Override
	int getSensorType() {
		return Sensor.TYPE_ACCELEROMETER;
	}

	public static void enqueueWork(Context context, Intent work) {
		enqueueWork(context, AccelerometerService.class, MyConstants.JOB_ID_ACCELEROMETER, work);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//重新启动
		enqueueWork(this, new Intent());
	}
}
