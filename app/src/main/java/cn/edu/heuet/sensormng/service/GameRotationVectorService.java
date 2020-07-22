package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.MyConstants;

/**
 * GameRotationVector
 */
public class GameRotationVectorService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_GAME_ROTATION_VECTOR;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GameRotationVectorService.class, MyConstants.JOB_ID_GAMEROTATIONVECTOR, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
