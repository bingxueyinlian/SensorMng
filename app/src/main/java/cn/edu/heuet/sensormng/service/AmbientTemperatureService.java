package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * AmbientTemperature
 */
public class AmbientTemperatureService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_AMBIENT_TEMPERATURE;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AmbientTemperatureService.class, MyConstants.JOB_ID_AMBIENTTEMPERATURE, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
