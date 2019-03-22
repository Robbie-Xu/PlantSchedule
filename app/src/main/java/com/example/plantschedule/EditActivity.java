package com.example.plantschedule;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ImageView iv = (ImageView)findViewById(R.id.iv_re);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Intent intent = getIntent();
        String str = "";
        String str1 = "";
        str = intent.getStringExtra("path");
        Log.w("path",str);
        if(str!=null) {

            Bitmap bm = BitmapFactory.decodeFile(str);

            iv.setImageBitmap(bm);
        }
            str1 = intent.getStringExtra("plantname");
        if(str1!=null)
            Log.w("getExtra1",str1);
    }

    protected void onClickWeather(View view){
        new DownloadUpdate().execute();
    }
    private class WeatherInfo{
        public String date;
        public String weather;
        public String temp;
        public String weather1;
        public String weather2;
        public String weather3;
        public String weather4;
        public String d0;
        public String d0_l;
        public String d0_h;
        public String d1;
        public String d1_l;
        public String d1_h;
        public String d2;
        public String d2_l;
        public String d2_h;
        public String d3;
        public String d3_l;
        public String d3_h;
        public String d4;
        public String d4_l;
        public String d4_h;
        WeatherInfo(){

        }

    }
    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://api.shujuzhihui.cn/api/weather/dailyweather?appKey=efcdee809802446f9e3c5f291195052f&city=Chongqing";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonInfo) {
            //Update the temperature displayed
            JSONObject dataJson = JSONObject.fromObject(jsonInfo);
            WeatherInfo weatherInfo = new WeatherInfo();
            String result = dataJson.getString("RESULT");
            dataJson = JSONObject.fromObject(result);
            String str_now = dataJson.getString("weather_now");
            JSONObject now = JSONObject.fromObject(str_now);
            weatherInfo.weather = now.getString("weather");

            weatherInfo.temp = now.getString("temp");

            JSONArray next = dataJson.getJSONArray("weather_next");
            JSONObject nextdata = next.getJSONObject(0);
            weatherInfo.date = nextdata.getString("fi");
            weatherInfo.d0_l = nextdata.getString("fd");
            weatherInfo.d0_h = nextdata.getString("fc");
            nextdata = next.getJSONObject(1);
            weatherInfo.weather1 = chooseWeather(nextdata.getString("fa"));
            weatherInfo.d0 = nextdata.getString("fj");


            ApplicationInfo appInfo = getApplicationInfo();
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(weatherInfo.temp);
            ((TextView) findViewById(R.id.tv_location)).setText("重庆");
            ((TextView) findViewById(R.id.t0)).setText(weatherInfo.d0_l+" ~ "+weatherInfo.d0_h+" °C");

            ((TextView) findViewById(R.id.tv_date)).setText("2019/"+weatherInfo.date);
            ((TextView) findViewById(R.id.tv_date0)).setText(weatherInfo.d0);

            Toast.makeText(getApplicationContext(),"Weather forecast has been updated",Toast.LENGTH_LONG).show();
        }
    }

}
