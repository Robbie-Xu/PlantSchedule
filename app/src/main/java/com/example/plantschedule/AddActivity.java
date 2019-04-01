package com.example.plantschedule;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

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
import static com.example.plantschedule.Zoompic.adjustImage;

public class AddActivity extends AppCompatActivity {
    private static String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/";
    private static String pname = "";
    private static String psname = "";
    private static String pspecies = "";
    private static String pdes = "";
    private static String loca = "";
    public static int flag = 0;
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    Bitmap bm;
    Location location;
    RecordInfo record = new RecordInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        requestAllPower();
    }

    public void BtnPlusCameraClick(View view) {

//        LinearLayout ll =(LinearLayout)this.findViewById(R.id.log_sel_qyport);
//        ll.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                ShowChoise();
//            }
//        });
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("pick one photo")
                .setMessage("shot with camera?")
                .setIcon(R.drawable.btn_camera)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pname = ((EditText) findViewById(R.id.ed_name)).getText().toString();

                        if (pname.equals("")) {
                            Toast.makeText(AddActivity.this, "Please add a name for this plant first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent();
                        // 指定开启系统相机的Action
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        // 根据文件地址创建文件
                        FILE_PATH = FILE_PATH + pname + ".jpg";
                        File file = new File(FILE_PATH);
                        if (file.exists()) {
                            file.delete();
                        }
                        // 把文件地址转换成Uri格式
                        Uri uri = Uri.fromFile(file);
                        // 设置系统相机拍摄照片完成后图片文件的存放地址
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, 0);

                    }
                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNeutralButton("pick from album", new DialogInterface.OnClickListener() {//添加普通按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 123);

                    }
                })
                .create();
        alertDialog2.show();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView iv = findViewById(R.id.iv_added);
        if (requestCode == 0) {
            Bitmap bm = null;
            bm = adjustImage(FILE_PATH, bm);
            iv.setImageBitmap(bm);
            flag = 1;
        } else if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            // 图片路径
            FILE_PATH = c.getString(columnIndex);
            Log.w("Imagepath", "1" + FILE_PATH);

            c.close();
            Bitmap bm = null;
            bm = adjustImage(FILE_PATH, bm);
            iv.setImageBitmap(bm);
            flag = 1;
        }
    }

    public void onClickBtnDone(View view) {
        if (flag <= 0) {
            Toast.makeText(this, "Please add a pic for this plant first", Toast.LENGTH_SHORT).show();
            return;
        }
        pname = ((EditText) findViewById(R.id.ed_name)).getText().toString();
        psname = ((EditText) findViewById(R.id.ed_sname)).getText().toString();
        pspecies = ((EditText) findViewById(R.id.ed_species)).getText().toString();
        pdes = ((EditText) findViewById(R.id.ed_des)).getText().toString();
        loca = ((EditText) findViewById(R.id.ed_loc)).getText().toString();
        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PlantContract.PlantEntry.COLUMN_PNAME, pname);
        values.put(PlantContract.PlantEntry.COLUMN_PSNAME, psname);
        values.put(PlantContract.PlantEntry.COLUMN_PSPECIES, pspecies);
        values.put(PlantContract.PlantEntry.COLUMN_PDESCRI, pdes);
        values.put(PlantContract.PlantEntry.COLUMN_PPICPATH, FILE_PATH);
        values.put(PlantContract.PlantEntry.COLUMN_PLOC,loca );
        db.insert(PlantContract.PlantEntry.TABLE_NAME, null, values);
        Intent intent = new Intent(this, SearchActivity.class);//显示intent
        flag = 0;
        startActivity(intent);
        finish();
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED) {                             //check the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,    //request the permission, give requestCode
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);   //feedback

        if (requestCode == 1) {                                                     //compare permission requested.
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (requestCode == BAIDU_READ_PHONE_STATE)                                   //compare permission requested.
            //如果用户取消，permissions可能为null.
            if (grantResults.length > 0) {  //有权限
                // 获取到权限，作相应处理
                Log.w("getLocation", "in permission" + grantResults.length + grantResults[0]);
                if (grantResults[0] == PERMISSION_GRANTED) {
                    Log.w("getLocation", "have permission");
                    getLocation();
                }
            } else {
                showGPSContacts();
                Log.w("getLocation", "in find permission");
            }

    }

    public void onClickLoc(View view) {
        showGPSContacts();
    }

    private class DownloadLocUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String stringUrl = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=" + record.lat + "," + record.lng + "&output=json&pois=0&latest_admin=1&ak=Xm1dkkU6dT8B1ply2kOBmKCOwx8BwQjg";
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
                if (StringUtils.isNotEmpty(wholeJson)) {
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
            if (!loc.equals("")) {
                EditText ed = (EditText) findViewById(R.id.ed_loc);
                Log.w("!!!!!!!",loc);
                ed.setText(loc);
            }
        }
    }

    /**
     * 检测GPS、位置权限是否开启
     */
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;//网络定位
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS定位
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public void showGPSContacts() {
        Log.w("getLocation", "in show");
        LocationManager lm;
        lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ok) {//开了定位服务
            if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PERMISSION_GRANTED) {// 没有权限，申请权限。
                    Log.w("getLocation", "ask for permission");
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
        Log.w("getLocation", "in get");
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
        Log.w("getLocation", "ready to update");
        new AddActivity.DownloadLocUpdate().execute();
    }

    /**
     * 获取到当前位置的经纬度
     *
     * @param location
     */
    private void updateLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            record.lat = latitude;
            record.lng = longitude;
            Toast.makeText(getApplicationContext(), "your location: lat=" + latitude + " lng=" + longitude, Toast.LENGTH_SHORT).show();
            Log.w("loc", "lat:" + latitude + "\nlng:" + longitude);
        } else {
            Log.w("loc", "can't get location");
        }
    }

}

