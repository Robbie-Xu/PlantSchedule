package com.example.plantschedule;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class EditActivity extends AppCompatActivity {
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    Bitmap bm;
    Location location;
    RecordInfo record = new RecordInfo();
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

            bm = Zoompic.adjustImage(str,bm);
            iv.setImageBitmap(bm);
        }
            str1 = intent.getStringExtra("plantname");
        if(str1!=null)
            ((TextView)findViewById(R.id.tv_rename)).setText(str1);
            Log.w("getExtra1"," "+str1);
        record.plantName = str1;
        record.path = str;
    }


    protected void onClickWeather(View view){
        new DownloadUpdate().execute();
    }
    protected void onClickLoc(View view){
        showGPSContacts();

    }


    private class WeatherInfo{
        public String weather;
        public String d0_l;
        public String d0_h;

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

            JSONArray next = dataJson.getJSONArray("weather_next");
            JSONObject nextdata = next.getJSONObject(0);
            weatherInfo.d0_l = nextdata.getString("fd");
            weatherInfo.d0_h = nextdata.getString("fc");

            ((EditText) findViewById(R.id.ed_weather)).setText(  weatherInfo.weather+" "+weatherInfo.d0_l+" ~ "+weatherInfo.d0_h+" °C");
            record.weather =  ((EditText) findViewById(R.id.ed_weather)).getText().toString();

            Toast.makeText(getApplicationContext(),"Weather forecast has been updated",Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadLocUpdate extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String stringUrl = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location="+record.lat+","+record.lng+"&output=json&pois=0&latest_admin=1&ak=Xm1dkkU6dT8B1ply2kOBmKCOwx8BwQjg";
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
                String wholeJson = buffer.toString();
                if(StringUtils.isNotEmpty(wholeJson)) {
                    int locStart = wholeJson.indexOf("address\":\"");
                    int locEnd = wholeJson.indexOf("\",\"business");
                    if (locStart > 0 && locEnd > 0) {
                        String loc = wholeJson.substring(locStart + 10, locEnd);
                        record.loc = loc;
                        return loc;
                    }
                }

                return null;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String loc) {
            if(!loc.equals("")){
                EditText ed = (EditText)findViewById(R.id.ed_loc);
                ed.setText(loc);
            }
        }
    }
    /**
     * 检测GPS、位置权限是否开启
     */
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;//网络定位
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS定位
        }else{
            Toast.makeText(this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    public void showGPSContacts() {
        Log.w("getLocation","in show");
        LocationManager lm;
        lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        boolean ok = lm.isProviderEnabled( LocationManager.GPS_PROVIDER);

        if (ok) {//开了定位服务
            if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PERMISSION_GRANTED) {// 没有权限，申请权限。
                    Log.w("getLocation","ask for permission");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            BAIDU_READ_PHONE_STATE);
                } else {
                    getLocation();//getLocation为定位方法
                }
            } else {
                getLocation();//getLocation为定位方法
            }
        } else {
            Toast.makeText(this, "Please turn on GPS first.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, PRIVATE_CODE);
        }
    }

    /**
     * 获取具体位置的经纬度
     */
    private void getLocation() {
        // 获取位置管理服务
        Log.w("getLocation","in get");
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        // 查找到服务信息
        String provider = judgeProvider(locationManager); // 获取GPS信息

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        updateLocation(location);
        Log.w("getLocation","ready to update");
        new DownloadLocUpdate().execute();
    }

    /**
     * 获取到当前位置的经纬度
     * @param location
     */
    private void updateLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            record.lat = latitude;
            record.lng = longitude;
            Toast.makeText(getApplicationContext(), "your location: lat="+latitude+" lng="+longitude, Toast.LENGTH_SHORT).show();
            Log.w("loc","lat:" + latitude + "\nlng:" + longitude);
        } else {
            Log.w("loc","can't get location");
        }
    }
    /**
     * Android6.0申请权限的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                //如果用户取消，permissions可能为null.
                if ( grantResults.length > 0) {  //有权限
                    // 获取到权限，作相应处理
                    Log.w("getLocation","in permission"+grantResults.length+grantResults[0]);
                    if(grantResults[0] == PERMISSION_GRANTED) {
                        Log.w("getLocation","have permission");
                        getLocation();
                    }
                } else {
                    showGPSContacts();
                    Log.w("getLocation","in find permission");
                }
                break;
            default:
                break;
        }
    }
    protected void onClickGou(View view){
        bm.recycle();
        Log.w("boom", "recycle");
        EditText edTitle = (EditText)findViewById(R.id.ed_title);
        EditText edEvent = (EditText)findViewById(R.id.ed_event);
        EditText edDate = (EditText)findViewById(R.id.ed_date);
        TextView tvName = (TextView)findViewById(R.id.tv_rename);
        CheckBox cbW = (CheckBox)findViewById(R.id.cb_water);
        CheckBox cbF = (CheckBox)findViewById(R.id.cb_ferti);
        CheckBox cbD = (CheckBox)findViewById(R.id.cb_drug);
        record.title = edTitle.getText().toString();
        record.event = edEvent.getText().toString();
        record.date = edDate.getText().toString();
        record.plantName = tvName.getText().toString();
        if(cbW.isChecked()){
            record.isWater =1;
        }
        if(cbD.isChecked()){
            record.isDrug =1;
        }
        if(cbF.isChecked()){
            record.isFerti =1;
        }
        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PlantContract.RecordEntry.COLUMN_PNAME, record.plantName);
        values.put(PlantContract.RecordEntry.COLUMN_PDATE, record.date);
        values.put(PlantContract.RecordEntry.COLUMN_PISWATER, record.isWater);
        values.put(PlantContract.RecordEntry.COLUMN_PISFERTILIZE, record.isFerti);
        values.put(PlantContract.RecordEntry.COLUMN_PISUSINGDRUG, record.isDrug);
        values.put(PlantContract.RecordEntry.COLUMN_PWEATHER, record.weather);
        values.put(PlantContract.RecordEntry.COLUMN_PLOC_LON, record.lng);
        values.put(PlantContract.RecordEntry.COLUMN_PLOC_LAT, record.lat);
        values.put(PlantContract.RecordEntry.COLUMN_PLOC, record.loc);
        values.put(PlantContract.RecordEntry.COLUMN_PEVENTS_TITLE, record.title);
        values.put(PlantContract.RecordEntry.COLUMN_PEVENTS, record.event);
        values.put(PlantContract.RecordEntry.COLUMN_PPATH, record.path);
        db.insert(PlantContract.RecordEntry.TABLE_NAME, null, values);

        Intent it = new Intent(EditActivity.this, CurrentActivity.class); //
        Bundle b = new Bundle();
        b.putString("plantname", record.plantName);  //string
        it.putExtras(b);
        startActivity(it);

        finish();


}
    protected boolean onClickCha(View view){
        File file = new File(record.path);
        if (!file.exists()) {
            Toast.makeText(getApplicationContext(), "file" + record.path + "does not exist", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(record.path);
        }
        return false;
    }


    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);

        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile:" + filePath$Name + "success");
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "delete" + filePath$Name + "fail", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(),   filePath$Name + "does not exist", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}
