package com.bestestcompany.bitcoin_nu_utaar_chadav;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public Double inrToeur;
    public Double EurRateBitstamp;
    public Double profit;
    public Double reqProfit;
    private ProgressBar prog;
    private int count;
    private EditText editProfitLimit;
    private Double quantity;
    private Double BuyPrice;
    private double exchangeRate=0.012;
    private List<Pair<Double,Double>> BidsList ;
    private List<Pair<Double,Double>> AsksList;
    private List<Pair<Double,Double>> Profit;
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
        AsyncTask spr= new SendBuyBtcPostRequest().execute();
        AsyncTask spr2=new GetSellPriceRequest().execute();

        
    }
    public void callRatesAgain() throws InterruptedException {

        AsyncTask spr= new SendBuyBtcPostRequest().execute();
        AsyncTask spr2=new GetSellPriceRequest().execute();
    }
    public void notify1(Double profit) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Profit = "+profit)
                        .setContentText("Hello World!");
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
                reqProfit=Double.parseDouble(editProfitLimit.getText().toString());
        }
          catch (NumberFormatException ex){
              reqProfit=10d;
          }
        if(profit>=reqProfit)
            mBuilder.setSound(soundUri);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }
   public class SendBuyBtcPostRequest extends AsyncTask<String, Void, String> {

       protected void onPreExecute(){

       }

       protected String doInBackground(String... arg0) {

           try{
               if(count!=0)
                Thread.sleep(10000);


               URL url=new URL("https://api.coinsecure.in/v1/exchange/bid/orders");
               /*JSONObject postDataParams = new JSONObject();

               postDataParams.put("market", "BTC-INR");
               postDataParams.put("count", 1);
               Log.e("params",postDataParams.toString());
               */
               HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               conn.setReadTimeout(15000 /* milliseconds */);
               conn.setConnectTimeout(15000 /* milliseconds */);
               conn.setRequestMethod("GET");
               conn.setDoInput(true);
               conn.setDoOutput(false);
                /*
               OutputStream os = conn.getOutputStream();
               BufferedWriter writer = new BufferedWriter(
                       new OutputStreamWriter(os, "UTF-8"));
               //writer.write(String.valueOf(postDataParams));

              // writer.flush();
               writer.close();
               os.close();
                */
               int responseCode=conn.getResponseCode();

              //  Log.e("yoo",responseCode+"i");
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
           Log.e("result",result);
           try {
               JSONObject json = new JSONObject(result);;
               JSONArray arr=json.getJSONArray("message");
               BidsList=new ArrayList<Pair<Double,Double>>();
               for(int i=0;i<arr.length();i++) {
                   Pair<Double, Double> pair = new Pair<Double, Double>(arr.getJSONObject(i).getDouble("rate") * exchangeRate / 100, arr.getJSONObject(i).getDouble("vol") / 100000000.0);
                   BidsList.add(i, pair);
               }

           } catch (Exception e) {
           }

       }
   }

    public class GetSellPriceRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                //URL url=new URL("https://www.bitstamp.net/api/v2/ticker/btceur/");
                URL url=new URL("https://webapi.coinfloor.co.uk:8090/bist/XBT/GBP/order_book/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(false);
                /*
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));


                // writer.flush();
                writer.close();
                os.close();
                */
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
            Log.e("result2",result);
            try {

                JSONObject json = new JSONObject(result);
                JSONArray jsonArray=json.getJSONArray("asks");
                AsksList=new ArrayList<Pair<Double,Double>>() ;
                for(int i=0;i<jsonArray.length();i++){
                    JSONArray jo=jsonArray.getJSONArray(i);
                    Pair<Double,Double> pair=new Pair<>(jo.getDouble(0),jo.getDouble(1));
                    AsksList.add(i,pair);
                }
                Log.e("BidsList",BidsList.toString());
                Log.e("AsksList",AsksList.toString());
                CalculateProfit(BidsList,AsksList);
                /*String bid=json.getString("bid");
                Log.e("YO",bid);
                EurRateBitstamp=Double.parseDouble(bid);
                profit=((inrToeur*0.99f - EurRateBitstamp)/EurRateBitstamp)*100;
                */
                TextView textBox=(TextView)(findViewById(R.id.InfoBox));

                prog.setEnabled(true);
                prog.setVisibility(View.INVISIBLE);
                prog.setEnabled(false);
                prog.setBackgroundColor(1);
                textBox.setText("Bitcoins Transaction       Profit Percentage");
                for(int i=0;i<10;i++){
                    String bitcoinsToString = String.format("%.3f", Profit.get(i).first);
                    String profitToString = String.format("%.2f", Profit.get(i).second);
                    textBox.append("\n  "+bitcoinsToString+"                                "+profitToString);
                }
               /* textBox.setText(" PROFIT ="+ profit);
                textBox.append("\n BUY ="+EurRateBitstamp );
                textBox.append("\n SELL = "+inrToeur);
                */

                notify1(Profit.get(0).second);
                //Thread.sleep(10000);
                callRatesAgain();
                count++;
                Log.e("YO","yo"+count);

            } catch (Exception e) {
            }

        }
    }



    public void CalculateProfit(List<Pair<Double,Double>> BidsList,List<Pair<Double,Double>> AsksList)
    {
        Profit=new ArrayList<Pair<Double, Double>>();
        Pair<Double,Double> profitPair;
         double bitcoins;
        int m=0,n=0;
       for(int i=0;i<10;i++){
            if(BidsList.get(m).second>AsksList.get(n).second){
                bitcoins= AsksList.get(n).second;
                profit=((BidsList.get(m).first-AsksList.get(n).first)*100)/AsksList.get(n).first ;
                Pair<Double,Double> pair=new Pair<>(BidsList.get(m).first,BidsList.get(m).second-AsksList.get(n).second);
                BidsList.set(m,pair);
                n++;

            }
            else
            {
                bitcoins= BidsList.get(m).second;
                profit=((BidsList.get(m).first-AsksList.get(n).first)*100)/AsksList.get(n).first ;
                Pair<Double,Double> pair=new Pair<>(AsksList.get(n).first,AsksList.get(n).second-BidsList.get(m).second);
                AsksList.set(n,pair);
                m++;

            }


              profitPair =new Pair<>(bitcoins,profit);
              Profit.add(i,profitPair);

       }
       Log.e("Profit",Profit.toString()) ;
    }

}
