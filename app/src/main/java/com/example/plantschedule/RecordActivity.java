package com.example.plantschedule;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.icu.text.AlphabeticIndex;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static com.example.plantschedule.Zoompic.adjustImage;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Intent intent = getIntent();
        String str[] = {""};
        str[0] = intent.getStringExtra("plantname");
        Log.w("getExtra",str[0]);
        String str1[] = {""};
        str1[0] = intent.getStringExtra("path");
        int int2[] = {0};
        int2[0] = intent.getIntExtra("id",0);

        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlantContract.RecordEntry.COLUMN_PDATE,
                PlantContract.RecordEntry.COLUMN_PEVENTS_TITLE,
                PlantContract.RecordEntry.COLUMN_PISUSINGDRUG,
                PlantContract.RecordEntry.COLUMN_PISFERTILIZE,
                PlantContract.RecordEntry.COLUMN_PISWATER,
                PlantContract.RecordEntry.COLUMN_PWEATHER,
                PlantContract.RecordEntry.COLUMN_PLOC,
                PlantContract.RecordEntry.COLUMN_PEVENTS,
                PlantContract.RecordEntry.COLUMN_PPATH,
                PlantContract.RecordEntry.COLUMN_PNAME
        };

// Filter results WHERE "title" = 'My Title'
        String selection = PlantContract.RecordEntry._ID+"=?";
        String[] selectionArgs = {""};
        selectionArgs[0] = String.valueOf(int2[0]);
// How you want the results sorted in the resulting Cursor
        String sortOrder =
                PlantContract.RecordEntry.COLUMN_PNAME + " DESC";

        Cursor cursor = db.query(
                PlantContract.RecordEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        cursor.moveToPosition(0);
        RecordInfo re = new RecordInfo();
        re.date = cursor.getString(0);
        re.title = cursor.getString(1);
        re.isDrug = cursor.getInt(2);
        re.isFerti = cursor.getInt(3);
        re.isWater = cursor.getInt(4);
        re.weather = cursor.getString(5);
        re.loc = cursor.getString(6);
        re.event = cursor.getString(7);
        re.path = cursor.getString(8);
        re.plantName = cursor.getString(9);


        setContentView(R.layout.activity_record);
        TextView edTitle = (TextView)findViewById(R.id.tv_title);
        EditText edEvent = (EditText)findViewById(R.id.ed_event);
        EditText edDate = (EditText)findViewById(R.id.ed_date);
        TextView tvName = (TextView)findViewById(R.id.tv_rename);
        CheckBox cbW = (CheckBox)findViewById(R.id.cb_water);
        CheckBox cbF = (CheckBox)findViewById(R.id.cb_ferti);
        CheckBox cbD = (CheckBox)findViewById(R.id.cb_drug);
        EditText edLoc = (EditText)findViewById(R.id.ed_loc);
        EditText edWeather = (EditText)findViewById(R.id.ed_weather);
        ImageView ivpic = (ImageView)findViewById(R.id.iv_re);

        edTitle.setText(re.title);
        edDate.setText(re.date);
        edEvent.setText(re.event);
        edLoc.setText(re.loc);
        edWeather.setText(re.weather);
        tvName.setText(re.plantName);
        cbD.setChecked(i2b(re.isDrug));
        cbF.setChecked(i2b(re.isFerti));
        cbW.setChecked(i2b(re.isWater));
        Bitmap bm = null;
        bm = adjustImage(re.path,bm);
        ivpic.setImageBitmap(bm);
        cursor.close();
    }

    public boolean i2b (int i){
        if(i<=0){
            return false;
        }
        if(i>0)
            return true;
        return false;
    }


    /**
     * 截屏
     *
     * @param activity
     * @return
     */
    public static Bitmap activityShot(Activity activity) {
        /*获取windows中最顶层的view*/
        View view = activity.getWindow().getDecorView();
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        WindowManager windowManager = activity.getWindowManager();
        //获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        //去掉状态栏
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight, width, height - statusBarHeight);
        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void onClickShare(View view){
        Bitmap bm = activityShot(this);
        FileOutputStream mFileOutputStream = null;
        File mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()+"/shout.jpg");
        try {
            mFileOutputStream = new FileOutputStream(mFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.compress(Bitmap.CompressFormat.JPEG,100,mFileOutputStream);
        Uri uri = Uri.fromFile(mFile);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        this.startActivity(Intent.createChooser(shareIntent, "my plant record"));
    }

    public void onClickBack(View view){
        finish();
    }
}
