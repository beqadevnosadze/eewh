package ge.ee.eewh.Common;

import com.google.gson.Gson;
import ge.ee.eewh.R;
import ge.ee.eewh.SugaModels.LoginResult;
import ge.ee.eewh.eewhapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by beka-work on 20.09.2017.
 */

public class Web {

    private static String _server= "http://213.131.45.78:54300/";//eewhapp.getAppContext().getString(R.string.service_address);

    public Web() {

    }

    public static String POST(String requestURL,
                              HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public static <T> T GetToObject(String url, Class<T> cls){
       String result= Web.GET(url);
       if(result.length()==0) return null;
       return new Gson().fromJson(result, cls);
    }

    public static <T> T GetToObjectList(String url, Type cls){
        String result= Web.GET(url);
        if(result.length()==0) return null;
        return new Gson().fromJson(result, cls);
    }

    public static String GET(String requestURL) {


        HashMap<String, String> postDataParams= new HashMap<>();

        URL url;
        String response = "";
        try {
            url = new URL(_server+requestURL);


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            //conn.setRequestMethod("GET");
            conn.setDoInput(true);

            LoginResult profile=eewhapp.getProfile();
            if(profile!=null){
//                conn.setRequestProperty("UserName",profile.getUSER_NAME());
//                conn.setRequestProperty("PassHash",profile.getPASSHASH());
//                conn.setRequestProperty("IMEI",profile.getIMEI());

            }

            //conn.setDoOutput(true);



//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(os, "UTF-8"));
//            writer.write(getPostDataString(postDataParams));
//
//            writer.flush();
//            writer.close();
//            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
