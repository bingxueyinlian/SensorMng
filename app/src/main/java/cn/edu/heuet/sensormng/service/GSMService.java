package cn.edu.heuet.sensormng.service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.heuet.sensormng.FileUtils;
import cn.edu.heuet.sensormng.ConstantUtils;

/**
 * GSM
 */
public class GSMService extends JobIntentService {
    private String TAG = "GSMService";
    private final String dirName = "GSM";
    private String fileName = "gsm";
    private FileUtils fileUtils = null;
    private TelephonyManager telMng = null;
    private Timer timer = null;
    private int lastSignal = 0;
    private MyPhoneStateListener mPhoneStateListener;
    private String delay;
    private String period;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GSMService.class, ConstantUtils.JOB_ID_GSM, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileUtils = new FileUtils(dirName, fileName);
        telMng = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPhoneStateListener = new MyPhoneStateListener();

        // delay:1,period:5
        String config = fileUtils.getConfigInfo("gsm", "delay,period");
        String[] tempArr = config.split(",");
        delay = tempArr[0];
        period = tempArr[1];
        if (delay == null || delay.length() == 0) {
            delay = "1";
        }
        if (period == null || period.length() == 0) {
            period = "5";
        }
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        telMng.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
        //重新启动
        enqueueWork(this, new Intent());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        telMng.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    getLocationData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Integer.parseInt(delay) * 1000, Integer.parseInt(period) * 1000);

        //防止服务退出
        long count = 0;
        while (count < Long.MAX_VALUE - 1) {
            try {
                Thread.sleep(10000);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getLocationData() {
        // MCC，Mobile Country Code，移动国家代码（中国的为460）
        // MNC，Mobile Network Code，移动网络号码（中国移动为00，中国联通为01）
        // LAC，Location Area Code，位置区域码
        // CID，Cell Identity，基站编号，是个16位的数据（范围是0到65535）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        CellLocation cellLoc = telMng.getCellLocation();
        if (cellLoc instanceof GsmCellLocation) {
            String networkOperator = telMng.getNetworkOperator();
            GsmCell gsmCell = new GsmCell();
            if (networkOperator != null && networkOperator.length() >= 5) {
                String mcc = networkOperator.substring(0, 3);
                String mnc = networkOperator.substring(3, 5);
                gsmCell.setMcc(mcc);
                gsmCell.setMnc(mnc);
                GsmCellLocation gsmCellLoc = (GsmCellLocation) cellLoc;
                int cid = gsmCellLoc.getCid();// Cell id
                int lac = gsmCellLoc.getLac();// location area code
                gsmCell.setCid(cid);
                gsmCell.setLac(lac);
                gsmCell.setSignal(lastSignal);
                fileUtils.appendLine(gsmCell.toString());

                Log.i(TAG, gsmCell.toString());

            } else {
                Log.i(TAG, "==>networkOperator is invalid:" + networkOperator);
            }

        } else {
            Log.i(TAG, "==>CellLocation is invalid:" + cellLoc);

        }
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            if (signalStrength.isGsm()) {
                int gsmSignalStrength = signalStrength.getGsmSignalStrength();
                if (gsmSignalStrength != 99) {
                    lastSignal = gsmSignalStrength * 2 - 113;
                    Log.i(TAG, "GSMService==>gsmSignalStrength:" + lastSignal);
                }
            }
        }
    }

    static class GsmCell {
        private int cid;
        private int lac;
        private String mcc;
        private String mnc;
        private int signal;

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public int getLac() {
            return lac;
        }

        public void setLac(int lac) {
            this.lac = lac;
        }

        public String getMcc() {
            return mcc;
        }

        public void setMcc(String mcc) {
            this.mcc = mcc;
        }

        public String getMnc() {
            return mnc;
        }

        public void setMnc(String mnc) {
            this.mnc = mnc;
        }

        public int getSignal() {
            return signal;
        }

        public void setSignal(int signal) {
            this.signal = signal;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + cid;
            result = prime * result + lac;
            result = prime * result + ((mcc == null) ? 0 : mcc.hashCode());
            result = prime * result + ((mnc == null) ? 0 : mnc.hashCode());
            result = prime * result + signal;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            GsmCell other = (GsmCell) obj;
            if (cid != other.cid)
                return false;
            if (lac != other.lac)
                return false;
            if (mcc == null) {
                if (other.mcc != null)
                    return false;
            } else if (!mcc.equals(other.mcc))
                return false;
            if (mnc == null) {
                if (other.mnc != null)
                    return false;
            } else if (!mnc.equals(other.mnc))
                return false;
            if (signal != other.signal)
                return false;
            return true;
        }

        @NonNull
        @Override
        public String toString() {
            return cid + "," + lac + "," + mcc + "," + mnc + "," + signal;
        }
    }

}
