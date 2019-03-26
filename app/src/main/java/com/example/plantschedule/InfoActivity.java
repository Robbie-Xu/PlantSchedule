package com.example.plantschedule;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

import static com.example.plantschedule.Zoompic.adjustImage;

public class InfoActivity extends AppCompatActivity {
    String str[] = {""};
    private EditText tvName;
    private EditText tvSname;
    private EditText tvSpeci;
    private EditText tvLoc;
    private ImageView ivPic;
    private EditText edDes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        str[0] = intent.getStringExtra("plantname");
        Log.w("chaxum",str[0]+"11111111111111111");
        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlantContract.PlantEntry.COLUMN_PSNAME,
                PlantContract.PlantEntry.COLUMN_PSPECIES,
                PlantContract.PlantEntry.COLUMN_PPICPATH,
                PlantContract.PlantEntry.COLUMN_PDESCRI,
                PlantContract.PlantEntry.COLUMN_PLOC,
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
        pl.descri = cursor.getString(3);
        pl.location = cursor.getString(4);

        tvName = (EditText) findViewById(R.id.tv_name_);
        tvSname = (EditText) findViewById(R.id.tv_sname_);
        tvSpeci = (EditText) findViewById(R.id.tv_species_);
        tvLoc = (EditText) findViewById(R.id.tv_loc_);
        ivPic = (ImageView)findViewById(R.id.iv_speinfo_);
        edDes = (EditText)findViewById(R.id.ed_des_);
        tvName.setText(pl.name);
        tvSname.setText(pl.sname);
        tvSpeci.setText(pl.speci);
        tvLoc.setText(pl.location);
        edDes.setText(pl.descri);
        Bitmap bm = null;
        bm = adjustImage(pl.path,bm);
        ivPic.setImageBitmap(bm);

    }

    protected void onClickBack(View view){

        finish();
    }
}
