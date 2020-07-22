package cn.edu.heuet.sensormng.service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;

import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * GeoMagneticRotationVector
 */
public class GeoMagneticRotationVectorService extends AbstractSensorService {

    @Override
    int getSensorType() {
        return Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GeoMagneticRotationVectorService.class, ConstantUtils.JOB_ID_GEOMAGNETICROTATIONVECTOR, work);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }
}
