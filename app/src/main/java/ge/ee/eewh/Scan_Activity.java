package ge.ee.eewh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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
import com.orm.SugarRecord;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ge.ee.eewh.Adapters.BarcodesListAdapter;
import ge.ee.eewh.Adapters.LinesListAdapter;
import ge.ee.eewh.Common.MessageBox;
import ge.ee.eewh.Common.Web;
import ge.ee.eewh.SugaModels.BarcodesResult;
import ge.ee.eewh.SugaModels.HeaderResult;
import ge.ee.eewh.SugaModels.LinesResult;

public class Scan_Activity extends Activity {

    BarcodeManager decoder = null;
    TextView mBarcodeText;
    HeaderResult _item;
    LinesResult _line;
    String _action="";
    ReadListener _listener;
    List<BarcodesResult> _localitems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_);

        Bundle intent=getIntent().getExtras();
        _item=new Gson().fromJson(intent.getString("item"),HeaderResult.class);
        _line=new Gson().fromJson(intent.getString("line"),LinesResult.class);
        _action=intent.getString("action");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if(_line.getId()!=null){
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
                            //Toast.makeText(Scan_Activity.this, decodeResult.getText(), Toast.LENGTH_SHORT).show();
                            //mBarcodeText.setText(decodeResult.getText());
                            String scannedBarcode=decodeResult.getText().replace("\r","").replace("\n","");
                            BarcodesResult foundItem=null;
                            //find if barcode Scanned locally
                            for (BarcodesResult bar:_localitems  ) {
                                if(bar.getScannedBarcode()==null ) continue;
                                if(bar.getScannedBarcode().equals(scannedBarcode)){
                                    Toast.makeText(Scan_Activity.this,"შტრიხკოდი უკვე დასკანერებულია",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            //find if barcode Scanned in another item
                            String query="SOURCEID='"+_item.getNo_()+"' and  scanned_barcode='"+scannedBarcode+"'";
                            List<BarcodesResult> fundInItem =BarcodesResult.find(BarcodesResult.class,query);
                            if(fundInItem.size()>0){
                                Toast.makeText(Scan_Activity.this,"შტრიხკოდი უკვე დასკანერებულია \r\n"+
                                        "ნივთის ნომერია:"+fundInItem.get(0).getItem_No_(),Toast.LENGTH_SHORT).show();
                                return;
                            }


                            //find if barcode exists in local list
                            for (BarcodesResult bar:_localitems  ) {
                                if(bar.getScannedBarcode()!=null ) continue;
                                if(bar.getSerial_No_()==null ) continue;

                                if(bar.getSerial_No_().equals(scannedBarcode)){
                                    foundItem=bar;
                                    foundItem.setScannedBarcode(scannedBarcode);
                                    foundItem.setChangedBy(eewhapp.getProfile().getUser_ID());
                                    foundItem.save();
                                    break;
                                }
                            }
                            if(foundItem==null){
                                //if not found any change first non same non scanned
                                for (BarcodesResult bar:_localitems  ) {
                                    if(bar.getScannedBarcode()!=null ) continue;
                                    if(bar.getSerial_No_()==null ) continue;

                                    foundItem=bar;
                                    foundItem.setScannedBarcode(scannedBarcode);
                                    foundItem.setChangedBy(eewhapp.getProfile().getUser_ID());
                                    foundItem.save();

                                    break;
                                }
                            }
                            //if no space add line
                            if(foundItem==null) {
                                BarcodesResult res=new BarcodesResult();
                                res.setSource_ID(_line.getDocument_No_());
                                res.setSource_Ref__No_(_line.getLine_No_());
                                res.setItem_No_(_line.getNo_());
                                res.setQuantity(1);
                                res.setLocation_Code(_line.getLocation_Code());
                                res.setScannedBarcode(scannedBarcode);
                                res.setChangedBy(eewhapp.getProfile().getUser_ID());
                                //todo: set created by here
                                res.save();
                                _localitems.add(res);
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
        }
        SetLineValues();

        new AsyncLoadLocal().execute("");

        ListView lst=(ListView)findViewById(R.id.list_customers);
        View header = (View)getLayoutInflater().inflate(R.layout.list_barcodes_header_layout,null);
        lst.addHeaderView(header);

    }

    private void SetLineValues(){

        LinesResult p=_line;
        //set header values
        TextView TextItemAndNo = (TextView) findViewById(R.id.TextItemAndNo);
        TextView TextDescription = (TextView) findViewById(R.id.TextDescription);
        TextView textManufacturerAndShortDesc = (TextView) findViewById(R.id.textManufacturerAndShortDesc);
        TextView TextLocation = (TextView) findViewById(R.id.TextLocation);
        TextView TextQuantity = (TextView) findViewById(R.id.TextScannedQuantity2);

        if (TextItemAndNo != null) {
            TextItemAndNo.setText(_item.getNo_()+" / "+p.getLine_No_() +" / "+p.getNo_());
        }
        if (TextDescription != null) {
            TextDescription.setText(p.getFull_Description());
        }
        if (textManufacturerAndShortDesc != null) {
            textManufacturerAndShortDesc.setText(p.getManufacturer_Code()+" / "+p.getDescription());
        }
        if (TextLocation != null) {
            TextLocation.setText(p.getLocation_Code()+" - "+_item.getUser_ID());
        }

        if (TextQuantity != null) {
            String quantity=String.format("%.0f",_line.getQuantity());
            TextQuantity.setText(String.valueOf(p.getScannedQuantity())+"/"+quantity);
        }
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

            for (BarcodesResult bar :_localitems) {
                String scbarcode=bar.getScannedBarcode();
                if(scbarcode == null ) continue;
                scanquant++;
            }
            //Toast.makeText(Scan_Activity.this,String.valueOf(scanquant), Toast.LENGTH_SHORT);
            _line.setScannedQuantity(scanquant);
            _line.save();

            TextView TextQuantity = (TextView) findViewById(R.id.TextScannedQuantity2);
            if(_line.getScannedQuantity()==_line.getQuantity()){
                int color=getResources().getColor(R.color.colorKtgLogo);
                TextQuantity.setTextColor(color);
            }
            else{
                int color=getResources().getColor(R.color.colorAccent);
                TextQuantity.setTextColor(color);
            }
            String quantity=String.format("%.0f",_line.getQuantity());
            TextQuantity .setText(String.valueOf(_line.getScannedQuantity())+"/"+quantity);
        }
        catch (Exception ex){
            Log.v("eewh",ex.getMessage());
        }

    }

    public void SetListItems(List<BarcodesResult> resultList){
        ListView lst=(ListView)findViewById(R.id.list_customers);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if(_line.getId()==null || position==0) return;

                final BarcodesResult barcode=(BarcodesResult)parent.getItemAtPosition(position);


                AlertDialog.Builder builder = new AlertDialog.Builder(Scan_Activity.this);

                builder.setTitle("დაადასტურეთ");
                builder.setMessage("დარწმუნებული ხართ რომ გსურთ წაშლა??");
                builder.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(barcode.getSerial_No_()==null || barcode.getSerial_No_().length()==0){

                            _localitems.remove(barcode);
                            //barcode.delete();
                            if(barcode.getId() != null && barcode.getId()>0){
                               // BarcodesResult.de(BarcodesResult.class,"ID="+String.valueOf(barcode.getId()));
                                //String query="delete from Barcodes_Result where Id="+String.valueOf(barcode.getId());
                                //BarcodesResult.executeQuery(query);
                                //Log.v("SQLiteQuery",query);
                                barcode.delete();
                            }
                        }
                        else{
                            barcode.setScannedBarcode(null);
                            barcode.save();
                        }

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

        // get data from the table by the ListAdapter
        final BarcodesListAdapter customAdapter = new BarcodesListAdapter(Scan_Activity.this, R.layout.list_barcodes_layout,resultList);
//        View header = (View)getLayoutInflater().inflate(R.layout.list_barcodes_header_layout,null);
//        lst.addHeaderView(header);
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

        RefreshScannedQuantity();
    }

    public class AsyncLoadLocal extends AsyncTask<String,Void,List<BarcodesResult>> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(Scan_Activity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected List<BarcodesResult> doInBackground(String... params) {

            try {
                //download from server
                if(_line.getId() ==null){
                    Type listType = new TypeToken<ArrayList<BarcodesResult>>(){}.getType();
                    String requestURL="GetBarcodes?typeid="+_action+"&Item_No="+_line.getNo_()+"&Source_ID="+_line.getDocument_No_();
                    List<BarcodesResult> barcodeList=
                            Web.GetToObjectList(requestURL,listType);
                    return barcodeList;
                }
                else{

                    String query="ITEMNO='"+_line.getNo_()+"' and  SOURCEID='"+_line.getDocument_No_()+"'";
                    _localitems =BarcodesResult.find(BarcodesResult.class,query);
                    return _localitems;
                }
            }
            catch (RuntimeException ex){
                Log.v("eeWh",ex.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<BarcodesResult> resultList) {
            super.onPostExecute(resultList);
            pd.dismiss();

            if (resultList == null) {
                MessageBox.Show(Scan_Activity.this, "სიის ნახვა შეუძლებელია ცადეთ მოგვიანებით");
            } else {
                SetListItems(resultList);

                //Toast.makeText(OrdersActivity.this,String.valueOf(aVoid.size()),Toast.LENGTH_SHORT).show();
            }
        }

    }
}
