package ge.ee.eewh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarApp;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ge.ee.eewh.Adapters.HeaderListAdapter;
import ge.ee.eewh.Adapters.LinesListAdapter;
import ge.ee.eewh.Common.MessageBox;
import ge.ee.eewh.Common.SugaQuery;
import ge.ee.eewh.Common.Web;
import ge.ee.eewh.SugaModels.BarcodesResult;
import ge.ee.eewh.SugaModels.HeaderResult;
import ge.ee.eewh.SugaModels.LinesResult;

public class ItemsActivity extends Activity {

    String _action="";
    List<LinesResult> _itemsList=new ArrayList<>();
    List<LinesResult> _localItemsList=new ArrayList<>();
    HeaderResult _item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        Bundle bundle=getIntent().getExtras();
        String json=bundle.getString("item");
        String action=bundle.getString("action");
        _action=action;
        //HeaderResult order=(HeaderResult)parent.getItemAtPosition(position);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        _item=new Gson().fromJson(json,HeaderResult.class);
        if(_item != null){

            //String[] columnnames= SugaQuery.ReadColumnNames("Header_Result");

            //Check if local copy exists
            List<HeaderResult> localHeader=HeaderResult.find(HeaderResult.class,"No='"+_item.getNo_()+"'");
            if(localHeader.size()>0){
                _item=localHeader.get(0);
                new AsyncLoadLocal().execute(new String[]{_action,_item.getNo_()});
                TextView headertextview=(TextView) findViewById(R.id.TextHeadStatus);
                headertextview.setText("დამუშავების პროცესში");
                int meterdisColor=getResources().getColor(R.color.colorKtgLogo);
                headertextview.setTextColor(meterdisColor);

                Button btn_send=(Button)findViewById(R.id.SendButton);
                btn_send.setEnabled(true);
            }
            else{
                new AsyncLoad().execute(new String[]{_action,_item.getNo_(),"0"});
            }
            HeaderResult p=_item;


            TextView TextOrderNumberAndDate = (TextView) findViewById(R.id.TextOrderNumberAndDate);
            TextView TextVendor = (TextView) findViewById(R.id.TextVendor);
            TextView textPostDateAndStatus = (TextView) findViewById(R.id.textPostDateAndStatus);
            TextView TextLocationAndUser = (TextView) findViewById(R.id.TextLocationAndUser);

            if (TextOrderNumberAndDate != null) {
                Timestamp stamp = new Timestamp(p.getOrder_Date()* 1000L);
                Date date = new Date(stamp.getTime());
                String dateAsText = new SimpleDateFormat("yyyy.MM.dd").format(date);
                TextOrderNumberAndDate.setText(p.getNo_() +" / "+dateAsText);
            }
            if (TextVendor != null) {
                TextVendor.setText(p.getBuy_from_Vendor_Name());
            }
            if (textPostDateAndStatus != null) {
                Timestamp stamp = new Timestamp(p.getOrder_Date()* 1000L);
                Date date = new Date(stamp.getTime());
                String dateAsText = new SimpleDateFormat("yyyy.MM.dd").format(date);
                textPostDateAndStatus.setText(dateAsText+" / "+p.getStatus());
            }
            if (TextLocationAndUser != null) {
                TextLocationAndUser.setText(p.getLocation_Code()+" - "+p.getUser_ID());
            }


        }

    }

    public void Upload_Click(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("დაადასტურეთ");
        builder.setMessage("აიტვირთება შედეგები და წაიშლება ლოკალური მონაცემები დარწმუნებული ხართ?");
        builder.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                new Async_Upload().execute(_item.getNo_());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("არა", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    public void DownloadClick(View view){
        if(_localItemsList.size()>0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("დაადასტურეთ");
            builder.setMessage("წაიშლება ლოკალური მონაცემები დარწმუნებული ხართ?");
            builder.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    new AsyncLoad().execute(new String[]{_action,_item.getNo_(),"1"});
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("არა", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else if(_itemsList.size()>0){

            new AsyncLoad().execute(new String[]{_action,_item.getNo_(),"1"});

        }
    }

    public void SetListItems(List<LinesResult> resultList){
        ListView lst=(ListView)findViewById(R.id.list_customers);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String headerJson=new Gson().toJson(_item);
                LinesResult line=(LinesResult)parent.getItemAtPosition(position);
                String lineJson=new Gson().toJson(line);

                Intent scanActivity=new Intent(view.getContext(),Scan_Activity.class);
                scanActivity.putExtra("item",headerJson);
                scanActivity.putExtra("line",lineJson);
                scanActivity.putExtra("action",_action);
                startActivityForResult(scanActivity, 0);
            }
        });

        // get data from the table by the ListAdapter
        final LinesListAdapter customAdapter = new LinesListAdapter(ItemsActivity.this, R.layout.list_item_layout,resultList);
        lst.setAdapter(customAdapter);


        EditText searchField=(EditText)findViewById(R.id.inputSearch);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public class Async_Upload extends AsyncTask<String,Void,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(ItemsActivity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                List<BarcodesResult> barcodes=new ArrayList<>();
                for (LinesResult line:_localItemsList)
                {
                    String query="ITEMNO='"+line.getNo_()+"' and  SOURCEID='"+line.getDocument_No_()+"'";
                    List<BarcodesResult> ItemBarcodes =BarcodesResult.find(BarcodesResult.class,query);
                    barcodes.addAll(ItemBarcodes);

                }

                String json_Data=new Gson().toJson(barcodes);
                HashMap<String, String> postdata=new HashMap<>();
                postdata.put("data",json_Data);
                String resp=Web.POST("UploadData",postdata);


                boolean tr=resp.equals("\"true\"");
                if(tr){
                    for (BarcodesResult bar:barcodes) {
                        bar.delete();
                    }
                }
                return resp;
            }
            catch (RuntimeException ex){
                Log.v("eeWh",ex.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String resultList) {

            boolean tr=resultList.equals("\"true\"");
            if(tr){

                Button btn_send=(Button)findViewById(R.id.SendButton);
                btn_send.setEnabled(false);
                _item.delete();

                for (LinesResult res:_localItemsList ) {
                    res.delete();
                }
                MessageBox.Show(ItemsActivity.this,"მოქმედება დასრულდა წარმატებით");
                finish();
            }
            pd.dismiss();

            super.onPostExecute(resultList);
        }

    }

public class AsyncLoadLocal extends AsyncTask<String,Void,List<LinesResult>> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(ItemsActivity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected List<LinesResult> doInBackground(String... params) {

            try {

                _localItemsList=LinesResult.find(LinesResult.class,"DOCUMENTNO='"+params[1]+"'");

                return _localItemsList;
            }
            catch (RuntimeException ex){
                Log.v("eeWh",ex.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<LinesResult> resultList) {
            super.onPostExecute(resultList);
            pd.dismiss();

            if (resultList == null) {
                MessageBox.Show(ItemsActivity.this, "სიის ნახვა შეუძლებელია ცადეთ მოგვიანებით");
            } else {
                SetListItems(resultList);
                //Toast.makeText(OrdersActivity.this,String.valueOf(aVoid.size()),Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class AsyncLoad extends AsyncTask<String,Void,List<LinesResult>> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(ItemsActivity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected List<LinesResult> doInBackground(String... params) {
            try {
                Type listType = new TypeToken<ArrayList<LinesResult>>(){}.getType();
                //http://10.0.0.153:8080/GetTaskList?mrReaderId=1
                List<LinesResult> tasklist= Web.GetToObjectList("GetLines?typeid="+params[0]+"&no="+params[1],listType);
                _itemsList=tasklist;
                if(_itemsList.size()>0 && params[2].equals("1")){
                    _item.save();
                    LinesResult.deleteAll(LinesResult.class,"DOCUMENTNO='"+_item.getNo_()+"'");
                    LinesResult.saveInTx(_itemsList);

                    Type barcodestype = new TypeToken<ArrayList<BarcodesResult>>(){}.getType();
                    //http://10.0.0.153:8080/GetTaskList?mrReaderId=1
                    List<BarcodesResult> barcodes= Web.GetToObjectList("GetBarcodesByHeaderNo?typeid="+params[0]+"&Source_ID="+params[1],barcodestype);
                    BarcodesResult.deleteAll(BarcodesResult.class,"SOURCEID='"+_item.getNo_()+"'");
                    if(barcodes != null && barcodes.size()>0){
                        BarcodesResult.saveInTx(barcodes);
                    }

                }
                return _itemsList;
            }
            catch (RuntimeException ex){
                Log.v("eeWh",ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<LinesResult> resultList) {
            super.onPostExecute(resultList);

            _localItemsList=LinesResult.find(LinesResult.class,"DOCUMENTNO='"+_item.getNo_()+"'");
            if(_localItemsList.size()>0){
                //check if local items exists
                TextView headertextview=(TextView) findViewById(R.id.TextHeadStatus);
                headertextview.setText("დამუშავების პროცესში");
                int meterdisColor=getResources().getColor(R.color.colorKtgLogo);
                headertextview.setTextColor(meterdisColor);

                Button btn_send=(Button)findViewById(R.id.SendButton);
                btn_send.setEnabled(true);
            }


            if(resultList == null){
                pd.dismiss();
                MessageBox.Show(ItemsActivity.this,"სიის ნახვა შეუძლებელია ცადეთ მოგვიანებით");
            }
            else{
                SetListItems(resultList);
                pd.dismiss();
            }
        }

    }

}
