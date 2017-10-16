package ge.ee.eewh;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;


import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import  ge.ee.eewh.Common.*;
import ge.ee.eewh.SugaModels.LoginResult;


import com.google.gson.reflect.TypeToken;


import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestEmeiPermissions();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //set password action listener
        EditText passField=(EditText)findViewById(R.id.etPass);

        passField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    DoAuth();
                    return true;
                }
                return false;
            }
        });


        new AsyncLocalDb().execute();
        new AsyncTryUpdate().execute();

    }

    public class AsyncTryUpdate extends AsyncTask<Void,Void,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(LoginActivity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                int versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                if(versionName !=0){
                    UpdateModel upd=Web.GetToObject("CheckUpdate",UpdateModel.class);
                    if(upd != null)
                    {
                        if(versionName < Integer.valueOf(upd.version)){
                            //download update here
                            String updateapkUrl=Web.get_server()+"apk/"+upd.version+".apk";

                            String path= Environment.getExternalStorageDirectory() + "/download/";
                            String fileName=upd.version+".apk";

                            if(Web.DownloadFile(updateapkUrl,path,fileName)){
                                //start install
                                return path+fileName;
                            }
                        }
                    }
                }

            }
            catch (Exception ex){
                Log.v("eewh",ex.getMessage());
            }
//            return null;
            return null;
        }

        @Override
        protected void onPostExecute(String apk_path) {
            super.onPostExecute(apk_path);
            pd.dismiss();

            if(apk_path != null){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(apk_path)), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

    }

    public class AsyncLocalDb extends AsyncTask<Void,Void,Void> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(LoginActivity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //find if user all ready logged in download new tasks
                List<LoginResult> list=LoginResult.listAll(LoginResult.class);
                if(!list.isEmpty()){
                    eewhapp.setProfile(list.get(0));
                    StartMainActivity();

                }
            }
            catch (RuntimeException ex){
                Log.v("eewh",ex.getMessage());
            }
//            return null;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            pd.dismiss();
        }

    }

    public void StartMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        finish();
    }

    public void BtnClosClick(View view){
        finish();
    }

    public void BtnOkClick(View view){
        DoAuth();
    }

//authenticating user
    private void DoAuth(){
        //action/username/passhash/imei

        EditText userName=(EditText)LoginActivity.this.findViewById(R.id.etUserName);
        EditText userPass=(EditText)LoginActivity.this.findViewById(R.id.etPass);
        String passHash= userPass.getText().toString();//CryptoUtils.SHA1(userPass.getText().toString());
        //user=1&passhash=2&imei=ეეე
        String imei=DeviceHelper.getDeviceImei(LoginActivity.this);
        String getUrl="login?user="+userName.getText()+"&passhash="+passHash+"&imei="+ imei;
        String[] params=new String[]{getUrl,passHash,imei};
        new AsyncAuth().execute(params);
//        Intent intent = new Intent(this, MainActivity.class);
//        //intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
        //finish();
        //Toast.makeText(this,"ავტორიზაცია",Toast.LENGTH_LONG).show();
    }


    private void requestEmeiPermissions() {
//        // Assume thisActivity is the current activity
//        int permissionCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_PHONE_STATE);
//        if(permissionCheck== PackageManager.PERMISSION_GRANTED) return;
//        Activity.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
    }


    //<params,progress,result>
    public class AsyncAuth extends AsyncTask<String,Void,LoginResult> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("მოითმინეთ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected LoginResult doInBackground(String... params) {
            LoginResult ret=new LoginResult();
            ret.setUser_ID("");
            LoginResult ob=Web.GetToObject(params[0], LoginResult.class);
            if(ob!=null){
                eewhapp.setProfile(ob);
                //get task list


//                Type listType = new TypeToken<ArrayList<TaskResult>>(){}.getType();
                //http://10.0.0.153:8080/GetTaskList?mrReaderId=1
                //List<TaskResult> tasklist=Web.GetToObjectList("GetTaskList?mrReaderId="+ob.getID(),listType);
                //TaskResult.findAll(TaskResult.class);
                
                //// TODO: 22.09.2017 if size > 0?  new task load logic 
                //TaskResult.deleteAll(TaskResult.class);
                //TaskResult.saveInTx(tasklist);
                //// TODO: 22.09.2017 same logic applays here 
                //get meters


                return ob;
            }

            return ret;
        }


        @Override
        protected void onPostExecute(LoginResult loginResult) {
            pDialog.dismiss();
            super.onPostExecute(loginResult);

            if(loginResult.getUser_ID()==null || loginResult.getUser_ID().length()<=0){
                MessageBox.Show(LoginActivity.this,"მომხმარებლის სახლი ან პაროლი არასწორია");
            }
            else{
                loginResult.save();
                StartMainActivity();
            }
        }
    }
}