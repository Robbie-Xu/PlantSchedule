package com.example.plantschedule;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class ResultActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvDescri;
    private TextView tvSpeci;
    private ImageView ivPic;
    private BaseAdapter adapter;
    private List<Plant> plantList = new ArrayList<Plant>();
    private ListView lvSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String str = "";
        str = intent.getStringExtra("searchname");
        Log.w("getExtra",str);

        lvSearch = (ListView) findViewById(R.id.listSearchPlant);

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
                LayoutInflater inflater = ResultActivity.this.getLayoutInflater();
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
        lvSearch.setAdapter(adapter);

        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str1 = "";
                tvName = (TextView) view.findViewById(R.id.tv_student_name);//找到Textviewname
                str1 = tvName.getText().toString();//得到数据
                Toast.makeText(ResultActivity.this, "" + str1, Toast.LENGTH_SHORT).show();//显示数据


                Intent it = new Intent(ResultActivity.this, MyActivity.class); //
                Bundle b = new Bundle();
                b.putString("we", str1);  //string
                it.putExtras(b);
                startActivity(it);
            }


        });
    }
    protected void BtnSearchClick(){
        Intent it = new Intent(ResultActivity.this, ResultActivity.class); //
        Bundle b = new Bundle();
        String str = "";
        str =((EditText) findViewById(R.id.et_search)).getText().toString();
        Log.w("BtnSearchClick",str);
        b.putString("searchname", str);  //string
        it.putExtras(b);
        startActivity(it);
        finish();
    }
}
