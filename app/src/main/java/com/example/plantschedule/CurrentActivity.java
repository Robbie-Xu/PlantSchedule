package com.example.plantschedule;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.plantschedule.Zoompic.adjustImage;
import static com.example.plantschedule.Zoompic.adjustImage2;

public class CurrentActivity extends AppCompatActivity {
    private static String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();

    private static final int    REQUEST_STORAGE_PERMISSION=1;
    private EditText tvName;
    private EditText tvSname;
    private EditText tvSpeci;
    private TextView ldate;
    private TextView ltitle;
    private TextView levent;
    private TextView lid;
    private ImageView ivPic;
    private ImageView livPic;
    private BaseAdapter adapter;
    private List<Plant> plantList = new ArrayList<Plant>();
    private SwipeMenuListView lvRecord;
    private String strname;
    private String file_path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Intent intent = getIntent();
        String str[] = {""};
        str[0] = intent.getStringExtra("plantname");
        Log.w("getExtra",str[0]+" ");

        strname = str[0];
        lvRecord = (SwipeMenuListView) findViewById(R.id.lv_record);
        TextView tv = (TextView) findViewById(R.id.tv_current);
        tv.setText(str[0]);

        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlantContract.PlantEntry.COLUMN_PSNAME,
                PlantContract.PlantEntry.COLUMN_PSPECIES,
                PlantContract.PlantEntry.COLUMN_PPICPATH,
        };

// Filter results WHERE "title" = 'My Title'
        String selection = PlantContract.PlantEntry.COLUMN_PNAME+"=?";
        String[] selectionArgs = str;

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                PlantContract.PlantEntry.COLUMN_PNAME + " DESC";

        Cursor cursor = db.query(
                PlantContract.PlantEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        cursor.moveToPosition(0);
        Plant pl = new Plant();
        pl.name = str[0];
        pl.sname = cursor.getString(0);
        pl.speci = cursor.getString(1);
        pl.path = cursor.getString(2);

        tvName = (EditText) findViewById(R.id.ed_name);
        tvSname = (EditText) findViewById(R.id.ed_sname);
        tvSpeci = (EditText) findViewById(R.id.ed_species);
        ivPic = (ImageView)findViewById(R.id.iv_current);
        tvName.setText(pl.name);
        tvSname.setText(pl.sname);
        tvSpeci.setText(pl.speci);
        Bitmap bm = null;
        bm = adjustImage2(pl.path,bm);
        ivPic.setImageBitmap(bm);
        UpdateList(str);
        cursor.close();
        db.close();
    }

    public void onClickBtnCamera(View view){
        requestAllPower();
        Intent intent = new Intent();
        int idmax = 0;
        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select max("+ PlantContract.RecordEntry._ID+") from "+ PlantContract.RecordEntry.TABLE_NAME,null);
        cursor.moveToFirst();
        if(cursor.getInt(0) <= 0){
            idmax = 0;
        }else {
            idmax = cursor.getInt(0)+1;
        }

        file_path = FILE_PATH + "/"+String.valueOf(idmax)+".jpg";

        // 指定开启系统相机的Action
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        // 根据文件地址创建文件
        File file = new File(file_path);

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

        ImageView iv = findViewById(R.id.iv_current);
        if (requestCode == 0) {
            Bitmap bm =null;
            bm =adjustImage(file_path,bm);
            iv.setImageBitmap(bm);

            Toast.makeText(CurrentActivity.this, "" + "1"+file_path, Toast.LENGTH_SHORT).show();//显示数据
            Intent it = new Intent(CurrentActivity.this, EditActivity.class); //
            Bundle b = new Bundle();
            b.putString("path", file_path);  //string
            it.putExtras(b);
            Bundle c = new Bundle();
            c.putString("plantname", tvName.getText().toString());  //string
            it.putExtras(c);
            startActivity(it);
            finish();
        }
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


    public void UpdateList(String[] str){

        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlantContract.RecordEntry.COLUMN_PDATE,
                PlantContract.RecordEntry.COLUMN_PEVENTS_TITLE,
                PlantContract.RecordEntry.COLUMN_PEVENTS,
                PlantContract.RecordEntry.COLUMN_PPATH,
                PlantContract.RecordEntry._ID
        };

        final int id = 0;
// Filter results WHERE "title" = 'My Title'
        String selection = PlantContract.RecordEntry.COLUMN_PNAME+"=?";
        String[] selectionArgs = str;
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

        for (int i = 0; i < cursor.getCount(); i++) {

            cursor.moveToPosition(i);
            Plant pl = new Plant();
            pl.name = cursor.getString(0);
            pl.sname = cursor.getString(1);
            pl.speci = cursor.getString(2);
            pl.path = cursor.getString(3);
            pl.rid = cursor.getInt(4);
            plantList.add(pl);

        }
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return plantList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = CurrentActivity.this.getLayoutInflater();
                View view;

                if (convertView == null) {
                    view = inflater.inflate(R.layout.item_layout, null);
                } else {
                    view = convertView;
                    Log.i("info", "有缓存，不需要重新生成" + position);
                }
                ldate = (TextView) view.findViewById(R.id.tv_lineone);
                ltitle = (TextView) view.findViewById(R.id.tv_linthree);
                levent = (TextView) view.findViewById(R.id.tv_linetwo);
                livPic = (ImageView)view.findViewById(R.id.item_image);
                lid = (TextView)view.findViewById(R.id.tv_hideid);
                ldate.setText(plantList.get(position).name);
                ltitle.setText(plantList.get(position).speci);
                levent.setText(plantList.get(position).sname);
                lid.setText(plantList.get(position).rid+"");
                Bitmap bm = null;
                bm = adjustImage2(plantList.get(position).path,bm);

                livPic.setImageBitmap(bm);
                return view;
            }
        };
        lvRecord.setAdapter(adapter);


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(280);
                deleteItem.setTitle("delete");
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);

            }
        };

        lvRecord.setMenuCreator(creator);
        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = "";
                ldate = (TextView) view.findViewById(R.id.tv_lineone);//找到Textviewname
                str = ldate.getText().toString();//得到数据
                lid = (TextView) view.findViewById(R.id.tv_hideid);
                int rid1 = Integer.valueOf(lid.getText().toString());
                Toast.makeText(CurrentActivity.this, "" + str, Toast.LENGTH_SHORT).show();//显示数据

                Intent it = new Intent(CurrentActivity.this, RecordActivity.class); //
                Bundle b = new Bundle();
                Bundle c = new Bundle();
                Bundle d = new Bundle();
                d.putInt("id",rid1);  //string
                it.putExtras(d);
                c.putString("path", file_path);  //string
                it.putExtras(c);
                b.putString("plantname", str);  //string
                it.putExtras(b);
                startActivity(it);

            }


        });
        lvRecord.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Plant list = plantList.get(position);
                        PlantDbHelper dbHelper = new PlantDbHelper(CurrentActivity.this);
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        String[] sttr = {""};
                        sttr[0]= String.valueOf(list.rid);
                        db.delete(PlantContract.RecordEntry.TABLE_NAME, PlantContract.RecordEntry._ID+"=?",sttr);
                        Log.w("id" , "1"+list.rid);
                        plantList.remove(position);
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    public void onClickInfo(View view){

        Log.w("clickinfo",strname+"000000");
        Intent iit = new Intent(CurrentActivity.this, InfoActivity.class); //
        Bundle b = new Bundle();
        b.putString("plantname", strname);  //string
        iit.putExtras(b);
        startActivity(iit);
    }

}
