package ge.ee.eewh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ge.ee.eewh.SugaModels.LoginResult;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView userName=(TextView)findViewById(R.id.TextUserFullName);
        userName.setText(eewhapp.getProfile().getUser_ID());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Exit) {
            System.exit(0);
            return true;
        }

        if (id == R.id.action_relogin) {
            LoginResult.deleteAll(LoginResult.class);
            Intent orders=new Intent(this,LoginActivity.class);
            startActivity(orders);
            finish();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent setingact=new Intent(this,Settings_Activity.class);
            startActivity(setingact);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btn_click(View vi){


        String action="";
        String ActionName="";
        switch (vi.getId())
        {
            case R.id.btn_sale://გაყიდვა მაღაზია
                action="sale";
                break;
            case R.id.btn_purchase://შესყიდვა
                action="Purchase";
                break;
            case R.id.btn_move://გადაწერა
                action="move";
                break;
            case R.id.btn_sale_dealer://გაყიდვა დილერი
                action="sale_dealer";
                break;
            case R.id.btn_return_sale://გაყიდვის უკან დაბრუნება
                action="return_sale";
                break;
            case R.id.btn_PdaPurchase_return://შესყიდვის უკან დაბრუნება
                action="Purchase_return";
                break;

        }

        Button bu=(Button)vi;

        Intent orders=new Intent(this,OrdersActivity.class);
        orders.putExtra("action",action);
        orders.putExtra("actionName",bu.getText().toString());

        startActivity(orders);

    }


}
