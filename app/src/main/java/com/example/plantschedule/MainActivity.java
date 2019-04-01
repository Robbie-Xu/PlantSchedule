package com.example.plantschedule;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDatabase();
    }

    public void BtnSearchClick(View view){
         //startActivity(new Intent("com.AndroidTest.SecondActivity"));//隐式intent
            Intent intent = new Intent(this, SearchActivity.class);//显示intent
            startActivity(intent);
        }

    public void BtnMyClick(View view){

            //startActivity(new Intent("com.AndroidTest.SecondActivity"));//隐式intent
            Intent intent = new Intent(this, MyActivity.class);//显示intent
            startActivity(intent);
    }
    private void createDatabase() {
        PlantDbHelper mDbHelper = new PlantDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

    }
}
