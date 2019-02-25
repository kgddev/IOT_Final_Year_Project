package com.example.nikita.mytemphumidapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nikita on 24-Nov-18.
 */

public class fetchData extends AsyncTask<Void,Void,Void> {
    String data="";
    String parsedData = "";
    String parsedSingle = "";
    int hr,min,sec;
    String time="";
    String date="";
    //background thread
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://api.thingspeak.com/channels/414819/feeds.json?api_key=QSSI85KIN4SSKEF8&results=2");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while (line!= null){
                line = bufferedReader.readLine();
                data = data + line;
            }
            JSONObject JO = (JSONObject) new JSONTokener(data).nextValue();
            JSONArray JA = (JSONArray) JO.get("feeds");
            for (int i =0;i<JA.length();i++){
                JSONObject JO1 = (JSONObject) JA.get(i);
                time=JO1.getString("created_at");
                date=time.substring(0,10);
                hr=Integer.parseInt(time.substring(11,13));
                min=Integer.parseInt(time.substring(14,16));
                sec=Integer.parseInt(time.substring(17,19));

                min = min+30;
                if(min>60){
                    min=min-60;
                    hr++;
                }
                hr=hr+5;
                parsedSingle = "Collected on : "+ date +" , "+hr+":"+min+":"+sec+" hrs\n"+
                        "\t\tTemperature: " + JO1.getString("field1").toString() + " °C" +
                        "\n\t\tHumidity:" + JO1.getString("field2").toString() +" g/cubic m" + "\n\n";
                parsedData = parsedData + parsedSingle;
            }

        } catch (MalformedURLException e) {
            MainActivity.data.setText("error1");
            e.printStackTrace();
        } catch (IOException e) {
            MainActivity.data.setText("error2");
            e.printStackTrace();
        } catch (JSONException e) {
            MainActivity.data.setText("error3");
            e.printStackTrace();
        }
        return null;
    }
//ui thread
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        MainActivity.data.setText(this.parsedData);
    }
}
