package ge.ee.eewh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
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

import com.datalogic.decode.BarcodeManager;
import com.datalogic.decode.DecodeException;
import com.datalogic.decode.DecodeResult;
import com.datalogic.decode.PropertyID;
import com.datalogic.decode.ReadListener;
import com.datalogic.decode.configuration.ScannerProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ge.ee.eewh.Adapters.BarcodesListAdapter;
import ge.ee.eewh.Adapters.HeaderListAdapter;
import ge.ee.eewh.Adapters.LinesListAdapter;
import ge.ee.eewh.Adapters.SaleItemsAdapter;
import ge.ee.eewh.Common.MessageBox;
import ge.ee.eewh.Common.Web;
import ge.ee.eewh.SugaModels.BarcodesResult;
import ge.ee.eewh.SugaModels.HeaderResult;
import ge.ee.eewh.SugaModels.LinesResult;
import ge.ee.eewh.SugaModels.SaleTransferBarcodes;
import ge.ee.eewh.SugaModels.SaleTransferHeaderResult;

public class Items_Sale_Activity extends Activity {

    BarcodeManager decoder = null;
    TextView mBarcodeText;

    ReadListener _listener;
    ScannerProperties configuration = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_items);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            //turn on scanner
            if (decoder == null) {
                decoder = new BarcodeManager();
                configuration = ScannerProperties.edit(decoder);
                configuration.keyboardWedge.enable.set(false);
                configuration.store(decoder, true);
                //turn on ean checksum digit
                decoder.setPropertyInts(new int[]{PropertyID.EAN13_SEND_CHECK}, new int[]{1});
                decoder.setPropertyInts(new int[]{PropertyID.EAN8_SEND_CHECK}, new int[]{1});
//                    decoder.setPropertyInts(new int[]{PropertyID.LABEL_SUFFIX},new int[]{1});
//                    decoder.getPropertyInts(new int[]{PropertyID.LABEL_SUFFIX},new int[]{1})

                _listener = new ReadListener() {
                    @Override
                    public void onRead(DecodeResult decodeResult) {
                        //Toast.makeText(Scan_Activity.this, decodeResult.getText(), Toast.LENGTH_SHORT).show();
                        //mBarcodeText.setText(decodeResult.getText());
                        final String scannedBarcode = decodeResult.getText().replace("\r", "").replace("\n", "");

                        //check if scanned barcode exsits
                        List<SaleTransferBarcodes> existing=SaleTransferBarcodes.find(SaleTransferBarcodes.class,"scanned_barcode != ''");

                        if(existing.size()>0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Items_Sale_Activity.this);

                            builder.setTitle("დაადასტურეთ");
                            builder.setMessage("წაიშლება აუტვირთავი მონაცემები დარწმუნებული ხართ?");
                            builder.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing but close the dialog
                                    new AsyncGetSaleItems().execute(scannedBarcode);
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
                        else {
                            new AsyncGetSaleItems().execute(scannedBarcode);
                        }

                    }
                };
                //set listener
                decoder.addReadListener(_listener);

            }
        } catch (DecodeException e) {
            e.printStackTrace();
        }

//        new AsyncGetLocal().execute("");
    }


    @Override
    protected void onStop() {
        if(decoder != null && _listener!=null){
            decoder.removeReadListener(_listener);
        }
        super.onStop();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if(decoder != null && _listener!=null){
            if(configuration!=null){
                configuration.keyboardWedge.enable.set(false);
                configuration.store(decoder, true);
            }
            decoder.removeReadListener(_listener);
            decoder.addReadListener(_listener);
        }
        CheckIfUploadPossiable();
        new AsyncGetLocal().execute("");
    }

    @Override
    protected void onDestroy() {
        if(decoder!=null){
            if(configuration!=null){
                configuration.keyboardWedge.enable.set(true);
                configuration.store(decoder, true);
            }
            decoder.removeReadListener(_listener);
        }
        super.onDestroy();
    }

    TextWatcher _wacher;

    public void SetListItems(List<SaleTransferHeaderResult> imtes){


        ListView lst=(ListView)findViewById(R.id.list_customers);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String headerJson=new Gson().toJson(_item);
                SaleTransferHeaderResult line=(SaleTransferHeaderResult)parent.getItemAtPosition(position);
                String lineJson=new Gson().toJson(line);

                Intent scanActivity=new Intent(view.getContext(),Scan_Sale_Activity.class);
                scanActivity.putExtra("item",lineJson);
//                scanActivity.putExtra("line",lineJson);
//                scanActivity.putExtra("action",_action);
                startActivityForResult(scanActivity, 0);
            }
        });
        final SaleItemsAdapter customAdapter = new SaleItemsAdapter(Items_Sale_Activity.this, R.layout.list_item_layout,imtes);
        lst.setAdapter(customAdapter);

        EditText searchField=(EditText)findViewById(R.id.inputSearch);
        if(_wacher == null){
            _wacher=new TextWatcher() {
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
            };
            searchField.addTextChangedListener(_wacher);
        }

    }

    public void SaveButton_click(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("დაადასტურეთ");
        builder.setMessage("აიტვირთება შედეგები და წაიშლება ლოკალური მონაცემები დარწმუნებული ხართ?");
        builder.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                new Async_Upload().execute("");
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

    public class Async_Upload extends AsyncTask<String,Void,String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(Items_Sale_Activity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                List<SaleTransferBarcodes> barcodes=SaleTransferBarcodes.listAll(SaleTransferBarcodes.class);
                String json_Data=new Gson().toJson(barcodes);
                HashMap<String, String> postdata=new HashMap<>();
                postdata.put("data",json_Data);
                String resp=Web.POST("UploadSaleData",postdata);

                boolean tr=resp.equals("\"true\"");
                if(tr){
                    SaleTransferHeaderResult.deleteAll(SaleTransferHeaderResult.class);
                    SaleTransferBarcodes.deleteAll(SaleTransferBarcodes.class);
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
                MessageBox.Show(Items_Sale_Activity.this,"მოქმედება დასრულდა წარმატებით");
                finish();
            }
            else{
                MessageBox.Show(Items_Sale_Activity.this,"ატვირთვა შეუძლებელია");
            }

            pd.dismiss();
            super.onPostExecute(resultList);
        }

    }
    public class AsyncGetLocal extends AsyncTask<String,Void,List<SaleTransferHeaderResult>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(Items_Sale_Activity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected List<SaleTransferHeaderResult> doInBackground(String... params) {
            try {
                List<SaleTransferHeaderResult> results=SaleTransferHeaderResult.listAll(SaleTransferHeaderResult.class);
                return results;
            }
            catch (RuntimeException ex){
                Log.v("eeWh",ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<SaleTransferHeaderResult> resultList) {
            super.onPostExecute(resultList);
            if(resultList==null){
                pd.dismiss();
                return;
            }

            TextView headerText=(TextView)findViewById(R.id.TextHeadStatus);
            if(resultList.size()==0){
                headerText.setText("დაასკანერეთ შტრიხკოდი");
            }
            else{
                headerText.setText(resultList.get(0).getReceipt_No_());
            }


            SetListItems(resultList);

            pd.dismiss();
        }
    }



    public class AsyncGetSaleItems extends AsyncTask<String,Void,List<SaleTransferHeaderResult>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(Items_Sale_Activity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected List<SaleTransferHeaderResult> doInBackground(String... params) {
            try {
                //get from web
                Type tp=new TypeToken<ArrayList<SaleTransferHeaderResult>>(){}.getType();
                List<SaleTransferHeaderResult> results=Web.GetToObjectList("GetSale?recno="+params[0],tp);

                Type tp2=new TypeToken<ArrayList<SaleTransferBarcodes>>(){}.getType();
                List<SaleTransferBarcodes> itemsRes=Web.GetToObjectList("GetSaleItems?recno="+params[0],tp2);

                if(results.size()>0){
                    SaleTransferHeaderResult.deleteAll(SaleTransferHeaderResult.class);
                    SaleTransferBarcodes.deleteAll(SaleTransferBarcodes.class);

                    SaleTransferHeaderResult.saveInTx(results);
                    SaleTransferBarcodes.saveInTx(itemsRes);


                }
                return results;
            }
            catch (RuntimeException ex){
                String message=ex.getMessage();
                Log.v("eeWh",message);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<SaleTransferHeaderResult> resultList) {
            super.onPostExecute(resultList);


            if(resultList==null){
                pd.dismiss();
                return;
            }

            TextView headerText=(TextView)findViewById(R.id.TextHeadStatus);
            if(resultList.size()==0){
                headerText.setText("დაასკანერეთ შტრიხკოდი");
                SaleTransferBarcodes.deleteAll(SaleTransferBarcodes.class);
                SaleTransferHeaderResult.deleteAll(SaleTransferHeaderResult.class);
                MessageBox.Show(Items_Sale_Activity.this,"არაფერი არ მოიძებნა");
            }
            else{
                headerText.setText(resultList.get(0).getReceipt_No_());
            }

            SetListItems(resultList);
            CheckIfUploadPossiable();
            pd.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CheckIfUploadPossiable();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void CheckIfUploadPossiable(){
        List<SaleTransferHeaderResult> h_items=SaleTransferHeaderResult.listAll(SaleTransferHeaderResult.class);

        boolean scannedAll=true;
        for (SaleTransferHeaderResult item:h_items){
            if(Integer.valueOf(item.getQuantity()) != item.getSannedQuantity()){
                scannedAll=false;
            }
        }
        if(h_items.size()==0){
            scannedAll=false;
        }
        Button btn=(Button)findViewById(R.id.SendButton);

        btn.setEnabled(scannedAll);

    }


}