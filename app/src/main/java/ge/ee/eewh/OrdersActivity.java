package ge.ee.eewh;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ge.ee.eewh.Adapters.HeaderListAdapter;
import ge.ee.eewh.Common.MessageBox;
import ge.ee.eewh.Common.Web;
import ge.ee.eewh.SugaModels.HeaderResult;

public class OrdersActivity extends Activity {

    String _action="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Bundle extras=this.getIntent().getExtras();

        String action=extras.getString("action");
        _action=action;
        String actionName=extras.getString("actionName");
        //get textView
        TextView actionText=(TextView)findViewById(R.id.textAction);
        actionText.setText(actionName);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        new AsyncLoadHeader().execute(action);

        //                Type listType = new TypeToken<ArrayList<TaskResult>>(){}.getType();
        //http://10.0.0.153:8080/GetTaskList?mrReaderId=1
        //List<TaskResult> tasklist=Web.GetToObjectList("GetTaskList?mrReaderId="+ob.getID(),listType);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //// TODO: 01.10.2017 refresh here if some order was sent
    }

    public class AsyncLoadHeader extends AsyncTask<String,Void,List<HeaderResult>> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd=new ProgressDialog(OrdersActivity.this);
            pd.setMessage("მოითმინეთ...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected List<HeaderResult> doInBackground(String... params) {

            try {

                Type listType = new TypeToken<ArrayList<HeaderResult>>(){}.getType();
                //http://10.0.0.153:8080/GetTaskList?mrReaderId=1
                List<HeaderResult> tasklist= Web.GetToObjectList("GetHeader?typeid="+params[0],listType);
                return tasklist;
            }
            catch (RuntimeException ex){
                Log.v("eeWh",ex.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<HeaderResult> resultList) {
            super.onPostExecute(resultList);
            pd.dismiss();

            if(resultList == null){
                MessageBox.Show(OrdersActivity.this,"სიის ნახვა შეუძლებელია ცადეთ მოგვიანებით");
            }
            else{
                //Toast.makeText(OrdersActivity.this,String.valueOf(aVoid.size()),Toast.LENGTH_SHORT).show();

                ListView lst=(ListView)findViewById(R.id.list_customers);

                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        HeaderResult order=(HeaderResult)parent.getItemAtPosition(position);
                        Intent itemsActivity=new Intent(view.getContext(),ItemsActivity.class);
                        itemsActivity.putExtra("no",order.getNo_());
                        itemsActivity.putExtra("action",_action);
                        String json=new Gson().toJson(order);
                        itemsActivity.putExtra("item",json);
                        //startActivity(itemsActivity);
                        //Toast.makeText(view.getContext(),"click",Toast.LENGTH_LONG).show();
                        //
                        startActivityForResult(itemsActivity, 0);
                    }
                });

                // get data from the table by the ListAdapter
                final HeaderListAdapter customAdapter = new HeaderListAdapter(OrdersActivity.this, R.layout.list_order_layout,resultList);
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

        }

    }
}