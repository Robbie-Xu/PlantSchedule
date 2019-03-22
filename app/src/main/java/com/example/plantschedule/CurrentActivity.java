package com.example.plantschedule;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CurrentActivity extends AppCompatActivity {
    private static final String FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()+"/1.jpg";
    private static final int    REQUEST_STORAGE_PERMISSION=1;
    private TextView tvName;
    private TextView tvDescri;
    private TextView tvSpeci;
    private ImageView ivPic;
    private BaseAdapter adapter;
    private List<Plant> plantList = new ArrayList<Plant>();
    private ListView lvRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


        Intent intent = getIntent();
        String str = "";
        str = intent.getStringExtra("plantname");
        Log.w("getExtra",str);

        lvRecord = (ListView) findViewById(R.id.lv_record);

        for (int i = 0; i < 8; i++) {
            Plant pl = new Plant();
            pl.name = "fake";
            pl.descri = "test fake plant";
            pl.speci = "test fake property";
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
                tvName = (TextView) view.findViewById(R.id.tv_student_name);
                tvDescri = (TextView) view.findViewById(R.id.tv_date);
                tvSpeci = (TextView) view.findViewById(R.id.tv_student_id);
                tvName.setText(plantList.get(position).name);
                tvDescri.setText(plantList.get(position).descri);
                tvSpeci.setText(plantList.get(position).speci);
                return view;
            }
        };
        lvRecord.setAdapter(adapter);

        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = "";
                tvName = (TextView) view.findViewById(R.id.tv_student_name);//找到Textviewname
                str = tvName.getText().toString();//得到数据
                Log.w("onac","1");

                Intent it = new Intent(CurrentActivity.this, EditActivity.class); //
                Bundle b = new Bundle();
                b.putString("plantname", str);  //string
                it.putExtras(b);
                startActivity(it);
            }


        });
    }

    protected void onClickBtnCamera(View view){
        requestAllPower();
        Toast.makeText(CurrentActivity.this, "" + FILE_PATH, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();

        // 指定开启系统相机的Action
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        // 根据文件地址创建文件
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

        ImageView iv = findViewById(R.id.iv_current);
        if (requestCode == 0) {
            Bitmap bm = BitmapFactory.decodeFile(FILE_PATH);
            bm = zoomImg(bm,600,400);
            iv.setImageBitmap(bm);

            Toast.makeText(CurrentActivity.this, "" + "1"+FILE_PATH, Toast.LENGTH_SHORT).show();//显示数据
            Intent it = new Intent(CurrentActivity.this, EditActivity.class); //
            Bundle b = new Bundle();
            b.putString("path", FILE_PATH);  //string
            it.putExtras(b);
            startActivity(it);
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
    public Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


}
