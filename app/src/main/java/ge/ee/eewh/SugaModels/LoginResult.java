package ge.ee.eewh.SugaModels;

import com.orm.SugarRecord;

/**
 * Created by beka-work on 20.09.2017.
 */

public class LoginResult extends SugarRecord<LoginResult> {

    public String User_ID;

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }
}