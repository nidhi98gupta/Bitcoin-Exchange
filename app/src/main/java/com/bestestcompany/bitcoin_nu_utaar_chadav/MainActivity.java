package com.bestestcompany.bitcoin_nu_utaar_chadav;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity {

    public float inrToeur;
    public float EurRateBitstamp;
    public float profit;
    public float reqProfit;
    private ProgressBar prog;
    private int count;
    private EditText editProfitLimit;
    //spr.getStatus()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        count=0;
        prog =(ProgressBar)findViewById(R.id.progressBar);
       // prog.setEnabled(false);
        editProfitLimit=(EditText)findViewById(R.id.editProfitLimit);
        editProfitLimit.setText("10");
        AsyncTask spr= new SendPostRequest().execute();
        AsyncTask spr2=new SendPostRequest2().execute();



    }
    public void callRatesAgain() throws InterruptedException {

        AsyncTask spr= new SendPostRequest().execute();
        AsyncTask spr2=new SendPostRequest2().execute();
    }
    public void notify1(float profit) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Profit = "+profit)
                        .setContentText("Hello World!");
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
                reqProfit=Float.parseFloat(editProfitLimit.getText().toString());
        }
          catch (NumberFormatException ex){
              reqProfit=10;
          }
        if(profit>=reqProfit)
            mBuilder.setSound(soundUri);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }
   public class SendPostRequest extends AsyncTask<String, Void, String> {

       protected void onPreExecute(){

       }

       protected String doInBackground(String... arg0) {

           try{
               if(count!=0)
                Thread.sleep(10000);


               URL url=new URL("https://china-exchange.belfrics.com:443/gateway/public/lastTrades");
               JSONObject postDataParams = new JSONObject();

               postDataParams.put("market", "BTC-INR");
               postDataParams.put("count", 1);
               Log.e("params",postDataParams.toString());
               HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               conn.setReadTimeout(15000 /* milliseconds */);
               conn.setConnectTimeout(15000 /* milliseconds */);
               conn.setRequestMethod("POST");
               conn.setDoInput(true);
               conn.setDoOutput(true);

               OutputStream os = conn.getOutputStream();
               BufferedWriter writer = new BufferedWriter(
                       new OutputStreamWriter(os, "UTF-8"));
               writer.write(String.valueOf(postDataParams));

              // writer.flush();
               writer.close();
               os.close();

               int responseCode=conn.getResponseCode();


               if (responseCode == HttpsURLConnection.HTTP_OK) {

                   BufferedReader in=new BufferedReader(
                           new InputStreamReader(
                                   conn.getInputStream()));
                   StringBuffer sb = new StringBuffer("");
                   String line="";

                   while((line = in.readLine()) != null) {

                       sb.append(line);
                       break;
                   }

                   in.close();
                   return sb.toString();

               }
               else {
                   return new String("false : "+responseCode);
               }

           }
           catch(Exception e){
               return new String("Exception: " + e.getMessage());
           }
       }


       @Override
       protected void onPostExecute(String result) {
                    prog.setVisibility(View.VISIBLE);
          // Toast.makeText(getApplicationContext(), result,
            //       Toast.LENGTH_LONG).show();
           try {
               JSONObject json = new JSONObject(result);;
               JSONArray arr=json.getJSONArray("results");
               String rate=arr.getJSONObject(0).getString("rate");
               inrToeur=Float.parseFloat(rate)/75f;

               //Log.e("YO",bel);

           } catch (Exception e) {
           }

       }
   }

    public class SendPostRequest2 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url=new URL("https://www.bitstamp.net/api/v2/ticker/btceur/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));


                // writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();


                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }

            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }


        @Override
        protected void onPostExecute(String result) {

            //Toast.makeText(getApplicationContext(), result,
            //        Toast.LENGTH_LONG).show();
            try {

                JSONObject json = new JSONObject(result);
                String bid=json.getString("bid");
                Log.e("YO",bid);
                EurRateBitstamp=Float.parseFloat(bid);
                profit=((inrToeur*0.99f - EurRateBitstamp)/EurRateBitstamp)*100;
                TextView textBox=(TextView)(findViewById(R.id.InfoBox));
              //  prog.setEnabled(true);
                prog.setVisibility(View.INVISIBLE);
                prog.setEnabled(false);
                prog.setBackgroundColor(1);
                textBox.setText(" PROFIT ="+ profit);
                textBox.append("\n BUY ="+EurRateBitstamp );
                textBox.append("\n SELL = "+inrToeur);


                notify1(profit);
                //Thread.sleep(10000);
                callRatesAgain();
                count++;
                Log.e("YO","yo"+count);

            } catch (Exception e) {
            }

        }
    }

}
