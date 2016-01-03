package com.example.hiroki.testgoogleapi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hiroki on 15/12/21.
 */
public class MyOpenHelper extends SQLiteOpenHelper {
    //コンストラクタ
    public MyOpenHelper(Context context) {
        super(context, "NameShopDB", null, 3);
    }

    //SQL文実行
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table person("
                + " name text not null,"
                + "link text,"
                + "latitude real,"
                + "longitude real,"
                + "photo blob"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS NameShopDB");
        //onCreate(db);
        db.execSQL( "ALTER TABLE person add photo blob" );
    }
}
