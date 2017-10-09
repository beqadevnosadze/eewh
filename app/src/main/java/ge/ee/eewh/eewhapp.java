package ge.ee.eewh;

import android.content.Context;

import ge.ee.eewh.SugaModels.LoginResult;
import com.orm.Database;
import com.orm.SugarApp;

/**
 * Created by beka-work on 20.09.2017.
 */

public class eewhapp extends SugarApp {


    public SugarApp sugarApp;
    private Database database;
    public static eewhapp app;
    @Override
    public void onCreate() {

        super.onCreate();
        database=super.getDatabase();
        app=this;
    }

    public Database GetSugaDb(){
        return database;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    public static LoginResult Profile;

    public static LoginResult getProfile() {
        return Profile;
    }

    public static void setProfile(LoginResult profile) {
        Profile = profile;
    }
}