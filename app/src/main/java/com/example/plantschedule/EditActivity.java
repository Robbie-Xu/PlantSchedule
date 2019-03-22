package com.example.plantschedule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

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



}
