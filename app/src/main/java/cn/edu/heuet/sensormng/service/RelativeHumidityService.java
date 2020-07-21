package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * RelativeHumidity
 */
public class RelativeHumidityService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_RELATIVE_HUMIDITY;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RelativeHumidityService.class, MyConstants.JOB_ID_RELATIVEHUMIDITY, work);
    }
}
