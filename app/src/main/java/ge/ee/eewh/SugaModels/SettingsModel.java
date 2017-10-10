package ge.ee.eewh.SugaModels;

import com.orm.SugarRecord;

/**
 * Created by beka-work on 10.10.2017.
 */

public class SettingsModel extends SugarRecord<SettingsModel> {
        public String ServerUrl="http://213.131.45.78:54300/";

    public String getServerUrl() {
        return ServerUrl;
    }

    public void setServerUrl(String serverUrl) {
        ServerUrl = serverUrl;
    }
}

