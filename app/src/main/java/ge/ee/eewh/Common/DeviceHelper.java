package ge.ee.eewh.Common;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by beka-work on 20.09.2017.
 */

public class DeviceHelper {
    public static String getDeviceImei(Activity activity){
        TelephonyManager mngr = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }


}
