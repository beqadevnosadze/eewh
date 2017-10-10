package ge.ee.eewh;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import ge.ee.eewh.Common.Web;
import ge.ee.eewh.SugaModels.SettingsModel;

public class Settings_Activity extends Activity {

    SettingsModel _model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if(_model==null){
            List<SettingsModel> dbresult=SettingsModel.listAll(SettingsModel.class);
            if(dbresult.size()>0){
                _model=dbresult.get(0);
            }
            else{
                SettingsModel ob=new SettingsModel();
                ob.setServerUrl("http://213.131.45.78:54300/");
                ob.save();
                _model=ob;
            }
        }

        EditText tv=(EditText)findViewById(R.id.textServerAddress);
        tv.setText(_model.getServerUrl());
    }

    public void btn_save_click(View vi){
        EditText tv=(EditText)findViewById(R.id.textServerAddress);
        _model.setServerUrl(tv.getText().toString());
        _model.save();
        Web.ResetSetting();
        finish();
    }

    public void btn_close_click(View vi){
        finish();
    }

}
