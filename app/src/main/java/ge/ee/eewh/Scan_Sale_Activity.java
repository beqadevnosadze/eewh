package ge.ee.eewh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datalogic.decode.BarcodeManager;
import com.datalogic.decode.DecodeException;
import com.datalogic.decode.DecodeResult;
import com.datalogic.decode.PropertyID;
import com.datalogic.decode.ReadListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ge.ee.eewh.Adapters.BarcodesListAdapter;
import ge.ee.eewh.Adapters.SaleBarcodesListAdapter;
import ge.ee.eewh.Common.MessageBox;
import ge.ee.eewh.Common.Web;
import ge.ee.eewh.SugaModels.BarcodesResult;
import ge.ee.eewh.SugaModels.HeaderResult;
import ge.ee.eewh.SugaModels.LinesResult;
import ge.ee.eewh.SugaModels.SaleTransferBarcodes;
import ge.ee.eewh.SugaModels.SaleTransferHeaderResult;

public class Scan_Sale_Activity extends Activity {

    BarcodeManager decoder = null;
    TextView mBarcodeText;
    SaleTransferHeaderResult _item;
    ReadListener _listener;
    List<SaleTransferBarcodes> _localitems=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_);

        Bundle intent=getIntent().getExtras();
        _item=new Gson().fromJson(intent.getString("item"),SaleTransferHeaderResult.class);


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        TextView headertextview=(TextView) findViewById(R.id.TextHeadStatus);
        headertextview.setText("დამუშავების პროცესში");
        int color=getResources().getColor(R.color.colorKtgLogo);
        headertextview.setTextColor(color);

        try {
            //turn on scanner
            if (decoder==null){
                decoder = new BarcodeManager();
                //turn on ean checksum digit
                decoder.setPropertyInts(new int[]{PropertyID.EAN13_SEND_CHECK},new int[]{1});
                decoder.setPropertyInts(new int[]{PropertyID.EAN8_SEND_CHECK},new int[]{1});
//                    decoder.setPropertyInts(new int[]{PropertyID.LABEL_SUFFIX},new int[]{1});
//                    decoder.getPropertyInts(new int[]{PropertyID.LABEL_SUFFIX},new int[]{1})

                _listener=new ReadListener() {
                    @Override
                    public void onRead(DecodeResult decodeResult) {
                        String scannedBarcode=decodeResult.getText().replace("\r","").replace("\n","");
                        SaleTransferBarcodes foundItem=null;

                        //if all bar codes are scanned show toast that space is full
                        if(Integer.valueOf(_item.getQuantity())==_item.getSannedQuantity()){
                            Toast.makeText(Scan_Sale_Activity.this, "ყველა შტრიხკოდი უკვე დასკანერებულია", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        //find if barcode Scanned locally
                        for (SaleTransferBarcodes bar:_localitems  ) {
                            if(bar.getScannedBarcode()==null ) continue;
                            if(bar.getScannedBarcode().equals(scannedBarcode)){
                                Toast.makeText(Scan_Sale_Activity.this,"შტრიხკოდი უკვე დასკანერებულია",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        //find if barcode Scanned in another item
                        //ScannedBarcode
                        String query="  scanned_barcode='"+scannedBarcode+"'";
                        List<SaleTransferBarcodes> fundInItem =SaleTransferBarcodes.find(SaleTransferBarcodes.class,query);
                        if(fundInItem.size()>0){
                            Toast.makeText(Scan_Sale_Activity.this,"შტრიხკოდი უკვე დასკანერებულია \r\n"+
                                    "ნივთის ნომერია:"+fundInItem.get(0).getItem_No_(),Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //find if barcode exists in local list
                        for (SaleTransferBarcodes bar:_localitems  ) {
                            if(bar.getScannedBarcode()!=null && !bar.getScannedBarcode().equals("")) continue;
                            //if(bar.getSerial_No_()==null ) continue;

                            if(bar.getSerial_No_().equals(scannedBarcode)){
                                foundItem=bar;
                                foundItem.setScannedBarcode(scannedBarcode);
                                foundItem.setChangedById(eewhapp.getProfile().getUser_ID());
                                foundItem.save();
                                break;
                            }
                        }
                        if(foundItem==null){
                            //if not found any change first non same non scanned
                            for (SaleTransferBarcodes bar:_localitems  ) {
                                if(bar.getScannedBarcode()!=null && !bar.getScannedBarcode().equals("") ) continue;
//                                if(bar.getSerial_No_()==null ) continue;

                                foundItem=bar;
                                foundItem.setScannedBarcode(scannedBarcode);
                                foundItem.setChangedById(eewhapp.getProfile().getUser_ID());
                                foundItem.save();

                                break;
                            }
                        }

                        SetListItems(_localitems);
                    }
                };
                //set listener
                decoder.addReadListener(_listener);

            }
        } catch (DecodeException e) {
            e.printStackTrace();
        }

        SetLineValues();

        new AsyncLoadLocal().execute("");



    }

    private void SetLineValues(){

        SaleTransferHeaderResult p=_item;
        //set header values
        TextView TextItemAndNo = (TextView) findViewById(R.id.TextItemAndNo);
        TextView TextDescription = (TextView) findViewById(R.id.TextDescription);
        TextView textManufacturerAndShortDesc = (TextView) findViewById(R.id.textManufacturerAndShortDesc);
        TextView TextLocation = (TextView) findViewById(R.id.TextLocation);
        TextView TextQuantity = (TextView) findViewById(R.id.TextScannedQuantity2);

        if (TextItemAndNo != null) {
            TextItemAndNo.setText(p.getItem_No_());
        }
        if (TextDescription != null) {
            TextDescription.setText(p.getManufacturer_Code());
        }
        if (textManufacturerAndShortDesc != null) {
            textManufacturerAndShortDesc.setText(p.getDescription());
        }
        if (TextLocation != null) {
            TextLocation.setText(p.getLocation_Code());
        }

//        if (TextQuantity != null) {
//            TextQuantity.setText(String.valueOf(p.getQuantity()));
//        }
        if (TextQuantity != null) {

            TextQuantity.setText(String.valueOf(p.getSannedQuantity())+"/"+p.getQuantity());
        }

        ListView lst=(ListView)findViewById(R.id.list_customers);
        View header = (View)getLayoutInflater().inflate(R.layout.list_barcodes_header_layout,null);
        lst.addHeaderView(header);
    }

    @Override
    protected void onDestroy() {
        if(decoder!=null){
            decoder.removeReadListener(_listener);
        }
        super.onDestroy();
    }

    public void RefreshScannedQuantity(){
        try {
            int scanquant=0;

            for (SaleTransferBarcodes bar :_localitems) {
                String scbarcode=bar.getScannedBarcode();
                if(scbarcode == null || scbarcode.equals("")) continue;
                scanquant++;
            }

            _item.setSannedQuantity(scanquant);
            _item.save();

            TextView TextQuantity = (TextView) findViewById(R.id.TextScannedQuantity2);
            if(_item.getSannedQuantity()==Integer.valueOf(_item.getQuantity())){
                int color=getResources().getColor(R.color.colorKtgLogo);
                TextQuantity.setTextColor(color);
            }
            else{
                int color=getResources().getColor(R.color.colorAccent);
                TextQuantity.setTextColor(color);
            }

            TextQuantity .setText(String.valueOf(_item.getSannedQuantity())+"/"+_item.getQuantity());
        }
        catch (Exception ex){
            Log.v("eewh",ex.getMessage());
        }

    }

    TextWatcher _filterFielWatcher;
    public void SetListItems(List<SaleTransferBarcodes> resultList){
        ListView lst=(ListView)findViewById(R.id.list_customers);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //if header clicked
                if(position==0) return;

                final SaleTransferBarcodes barcode=(SaleTransferBarcodes)parent.getItemAtPosition(position);


                AlertDialog.Builder builder = new AlertDialog.Builder(Scan_Sale_Activity.this);

                builder.setTitle("დაადასტურეთ");
                builder.setMessage("დარწმუნებული ხართ რომ გსურთ წაშლა??");
                builder.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        barcode.setScannedBarcode(null);
                        barcode.save();

                        SetListItems(_localitems);
                        // Do nothing but close the dialog
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
        });
        final SaleBarcodesListAdapter customAdapter = new SaleBarcodesListAdapter(Scan_Sale_Activity.this, R.layout.list_barcodes_layout,resultList);
        //set custom adapter to list view
        lst.setAdapter(customAdapter);

        EditText searchField=(EditText)findViewById(R.id.inputSearch);
        //add text change listener to search field
        if(_filterFielWatcher==null){
            _filterFielWatcher=new TextWatcher() {
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
            searchField.addTextChangedListener(_filterFielWatcher);
        }

        RefreshScannedQuantity();
    }


    public class AsyncLoadLocal extends AsyncTask<String,Void,List<SaleTransferBarcodes>> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(Scan_Sale_Activity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected List<SaleTransferBarcodes> doInBackground(String... params) {

            try {


                //List<SaleTransferBarcodes> a=SaleTransferBarcodes.listAll(SaleTransferBarcodes.class);

                    String query="ITEMNO='"+_item.getItem_No_()+"'";
                    _localitems =SaleTransferBarcodes.find(SaleTransferBarcodes.class,query);
                    return _localitems;
            }
            catch (RuntimeException ex){
                Log.v("eeWh",ex.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<SaleTransferBarcodes> resultList) {
            super.onPostExecute(resultList);
            pd.dismiss();

            if (resultList == null) {
                MessageBox.Show(Scan_Sale_Activity.this, "სიის ნახვა შეუძლებელია ცადეთ მოგვიანებით");
            } else {
                SetListItems(resultList);

                //Toast.makeText(OrdersActivity.this,String.valueOf(aVoid.size()),Toast.LENGTH_SHORT).show();
            }
        }

    }
}
