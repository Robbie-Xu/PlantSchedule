package com.example.plantschedule;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ListView;
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

import static com.example.plantschedule.Zoompic.adjustImage2;

public class SearchActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvDescri;
    private TextView tvSpeci;
    private ImageView ivPic;
    private BaseAdapter adapter;
    private BaseAdapter sadapter;
    private List<Plant> plantList = new ArrayList<Plant>();
    private SwipeMenuListView lvSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lvSearch = (SwipeMenuListView) findViewById(R.id.listSearchPlant);

        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlantContract.PlantEntry.COLUMN_PNAME,
                PlantContract.PlantEntry.COLUMN_PSNAME,
                PlantContract.PlantEntry.COLUMN_PSPECIES,
                PlantContract.PlantEntry.COLUMN_PPICPATH
        };

// Filter results WHERE "title" = 'My Title'
        String selection = null;
        String[] selectionArgs = null;

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

        for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToPosition(i);
                Plant pl = new Plant();
                pl.name = cursor.getString(0);
                pl.sname = cursor.getString(1);
                pl.speci = cursor.getString(2);
                pl.path = cursor.getString(3);
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
                LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
                View view;

                if (convertView == null) {
                    view = inflater.inflate(R.layout.item_layout, null);
                } else {
                    view = convertView;
                    Log.i("info", "有缓存，不需要重新生成" + position);
                }
                tvName = (TextView) view.findViewById(R.id.tv_lineone);
                tvDescri = (TextView) view.findViewById(R.id.tv_linthree);
                tvSpeci = (TextView) view.findViewById(R.id.tv_linetwo);
                ivPic = (ImageView)view.findViewById(R.id.item_image);
                tvName.setText(plantList.get(position).name);
                tvDescri.setText(plantList.get(position).sname);
                tvSpeci.setText(plantList.get(position).speci);
                Bitmap bm = null;
                bm = adjustImage2(plantList.get(position).path,bm);
                ivPic.setImageBitmap(bm);
                return view;
            }
        };
        lvSearch.setAdapter(adapter);

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

        // 设置 creato
        lvSearch.setMenuCreator(creator);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = "";
                tvName = (TextView) view.findViewById(R.id.tv_lineone);//找到Textviewname
                str = tvName.getText().toString();//得到数据
                Toast.makeText(SearchActivity.this, "" + str, Toast.LENGTH_SHORT).show();//显示数据

                Intent it = new Intent(SearchActivity.this, SpecialInfoActivity.class); //
                Bundle b = new Bundle();
                b.putString("plantname", str);  //string
                it.putExtras(b);
                startActivity(it);
            }


        });


        lvSearch.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Plant list = plantList.get(position);
                        PlantDbHelper dbHelper = new PlantDbHelper(SearchActivity.this);
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        String[] sttr = {""};
                        sttr[0] =list.name;
                        Log.w("name","1"+list.name);
                        db.delete(PlantContract.PlantEntry.TABLE_NAME, PlantContract.PlantEntry.COLUMN_PNAME+"=?",sttr);
                        File file = new File(list.path);
                        plantList.remove(position);
                        adapter.notifyDataSetChanged();
                        if (!file.exists()) {
                            Toast.makeText(getApplicationContext(), "file" + list.path + "does not exist", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            if (file.isFile())
                                return deleteSingleFile(list.path);
                        }

                        break;
                }
                return false;
            }
        });

        cursor.close();
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
            Toast.makeText(getApplicationContext(), filePath$Name + "does not exist", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void SearchClick(View view){

        String str = "";
        str =((EditText) findViewById(R.id.et_search)).getText().toString();
        lvSearch = (SwipeMenuListView) findViewById(R.id.listSearchPlant);

        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlantContract.PlantEntry.COLUMN_PNAME,
                PlantContract.PlantEntry.COLUMN_PSNAME,
                PlantContract.PlantEntry.COLUMN_PSPECIES,
                PlantContract.PlantEntry.COLUMN_PPICPATH
        };

// Filter results WHERE "title" = 'My Title'
        String selection = PlantContract.PlantEntry.COLUMN_PNAME+" like ?";
        String[] selectionArgs = {"%" + str+ "%"};


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

        if(cursor.getCount()==0){
            cursor.close();
            Toast.makeText(this,"No plants found!",Toast.LENGTH_SHORT).show();
            return;
        }
        plantList.clear();
        for (int i = 0; i < cursor.getCount(); i++) {

            cursor.moveToPosition(i);
            Plant pl = new Plant();
            pl.name = cursor.getString(0);
            pl.sname = cursor.getString(1);
            pl.speci = cursor.getString(2);
            pl.path = cursor.getString(3);
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
                LayoutInflater inflater = SearchActivity.this.getLayoutInflater();
                View view;

                if (convertView == null) {
                    view = inflater.inflate(R.layout.item_layout, null);
                } else {
                    view = convertView;
                    Log.i("info", "有缓存，不需要重新生成" + position);
                }
                tvName = (TextView) view.findViewById(R.id.tv_lineone);
                tvDescri = (TextView) view.findViewById(R.id.tv_linthree);
                tvSpeci = (TextView) view.findViewById(R.id.tv_linetwo);
                ivPic = (ImageView)view.findViewById(R.id.item_image);
                tvName.setText(plantList.get(position).name);
                tvDescri.setText(plantList.get(position).sname);
                tvSpeci.setText(plantList.get(position).speci);
                Bitmap bm = null;
                bm = adjustImage2(plantList.get(position).path,bm);
                ivPic.setImageBitmap(bm);
                return view;
            }
        };
        adapter.notifyDataSetChanged();
        lvSearch.setAdapter(adapter);


        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = "";
                tvName = (TextView) view.findViewById(R.id.tv_lineone);//找到Textviewname
                str = tvName.getText().toString();//得到数据
                Toast.makeText(SearchActivity.this, "" + str, Toast.LENGTH_SHORT).show();//显示数据

                Intent it = new Intent(SearchActivity.this, SpecialInfoActivity.class); //
                Bundle b = new Bundle();
                b.putString("plantname", str);  //string
                it.putExtras(b);
                startActivity(it);
            }


        });
        cursor.close();
    }

    public void BtnPlusClick(View view){
        Intent it = new Intent(SearchActivity.this, AddActivity.class);
        startActivity(it);
    }

}
