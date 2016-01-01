package com.example.hiroki.testgoogleapi;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by hiroki on 15/12/21.
 */
public class ShowDataBase extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_database);
        //setContentView(R.layout.activity_main);


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        // queryメソッドの実行例
        //query(テーブル名,column(nullなら全部),selectionArgs,groupby,having,orderby)
        //Cursor c = db.query("person", new String[]{"name", "link","latitude","longitude"}, null,
        //        null, null, null, null);
        Cursor c = db.query("person", null, null,
                null, null, null, null);

        boolean mov = c.moveToFirst();
        //データの表示
        while (mov) {
            TextView textView = new TextView(this);
            //textView.setText(String.format("%s : %s (%.10f,%.10f)", c.getString(0),
            //        c.getString(1), c.getDouble(2), c.getDouble(3)));
            textView.setText(String.format("%s : %s ", c.getString(0),
                    c.getString(1)));
            mov = c.moveToNext();
            layout.addView(textView);
        }
        c.close();
        db.close();
    }
}