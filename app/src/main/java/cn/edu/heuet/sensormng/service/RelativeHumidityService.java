package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * RelativeHumidity
 */
public class RelativeHumidityService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_RELATIVE_HUMIDITY;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RelativeHumidityService.class, ConstantUtils.JOB_ID_RELATIVEHUMIDITY, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
