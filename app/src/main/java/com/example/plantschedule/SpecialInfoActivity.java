package com.example.plantschedule;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plantschedule.data.PlantContract;
import com.example.plantschedule.data.PlantDbHelper;

import static com.example.plantschedule.Zoompic.zoomImg;

public class SpecialInfoActivity extends AppCompatActivity {
    String str[] = {""};
    private TextView tvName;
    private TextView tvSname;
    private TextView tvSpeci;
    private ImageView ivPic;
    private EditText edDes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_info);

        Intent intent = getIntent();
        str[0] = intent.getStringExtra("plantname");

        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                PlantContract.PlantEntry.COLUMN_PSNAME,
                PlantContract.PlantEntry.COLUMN_PSPECIES,
                PlantContract.PlantEntry.COLUMN_PPICPATH,
                PlantContract.PlantEntry.COLUMN_PDESCRI,
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

        tvName = (TextView) findViewById(R.id.tv_name_);
        tvSname = (TextView) findViewById(R.id.tv_sname_);
        tvSpeci = (TextView) findViewById(R.id.tv_species_);
        ivPic = (ImageView)findViewById(R.id.iv_speinfo_);
        edDes = (EditText)findViewById(R.id.ed_des_);
        tvName.setText(pl.name);
        tvSname.setText(pl.sname);
        tvSpeci.setText(pl.speci);
        edDes.setText(pl.descri);
        Bitmap bm = BitmapFactory.decodeFile(pl.path);
        bm = zoomImg(bm,600,400);
        ivPic.setImageBitmap(bm);

    }

    protected void onClickPlus(View view){
        PlantDbHelper dbHelper = new PlantDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PlantContract.PlantEntry.COLUMN_PISMY,1 );
        db.update(PlantContract.PlantEntry.TABLE_NAME,values,"name = ?",str);

        Intent it = new Intent(this, MyActivity.class);
        Bundle b = new Bundle();
        b.putString("plantname",str[0]);
        it.putExtras(b);
        startActivity(it);
        finish();
    }
}
