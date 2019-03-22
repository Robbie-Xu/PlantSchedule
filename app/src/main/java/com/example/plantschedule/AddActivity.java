package com.example.plantschedule;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

import java.io.File;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AddActivity extends AppCompatActivity {
    private static String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()+"/";
    private static String pname = "";
    private static String psname = "";
    private static String pspecies = "";
    private static String pdes = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        requestAllPower();
    }

    public void BtnPlusCameraClick(View view){

        pname = ((EditText)findViewById(R.id.ed_name)).getText().toString();

        if(pname.equals("")){
            Toast.makeText(this, "Please add a name for this plant first" , Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView iv = findViewById(R.id.iv_added);
        if (requestCode == 0) {
            Bitmap bm = BitmapFactory.decodeFile(FILE_PATH);
            bm = Zoompic.zoomImg(bm,600,400);
            iv.setImageBitmap(bm);

        }
    }
    protected void onClickBtnDone(View view){
        psname = ((EditText)findViewById(R.id.ed_sname)).getText().toString();
        pspecies = ((EditText)findViewById(R.id.ed_species)).getText().toString();
        pdes = ((EditText)findViewById(R.id.ed_des)).getText().toString();
        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PlantContract.PlantEntry.COLUMN_PNAME, pname);
        values.put(PlantContract.PlantEntry.COLUMN_PSNAME, psname);
        values.put(PlantContract.PlantEntry.COLUMN_PSPECIES, pspecies);
        values.put(PlantContract.PlantEntry.COLUMN_PDESCRI,pdes);
        values.put(PlantContract.PlantEntry.COLUMN_PPICPATH,FILE_PATH);
        db.insert(PlantContract.PlantEntry.TABLE_NAME, null, values);
        Intent intent = new Intent(this, SearchActivity.class);//显示intent
        startActivity(intent);
        finish();
    }
    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
