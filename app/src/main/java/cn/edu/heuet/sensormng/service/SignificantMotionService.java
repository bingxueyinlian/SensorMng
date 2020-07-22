package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * SignificantMotion
 */
public class SignificantMotionService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_SIGNIFICANT_MOTION;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SignificantMotionService.class, ConstantUtils.JOB_ID_SIGNIFICANTMOTION, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
