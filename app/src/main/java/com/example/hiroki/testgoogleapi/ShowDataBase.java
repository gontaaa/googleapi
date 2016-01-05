package com.example.hiroki.testgoogleapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hiroki on 15/12/21.
 */
public class ShowDataBase extends Activity implements View.OnClickListener{
    //private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_database);

        //setContentView(R.layout.activity_main);
        final LinearLayout llayout = (LinearLayout) findViewById(R.id.llayout);

        //LinearLayout layout = new LinearLayout(this);
        llayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(llayout);

        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getReadableDatabase();

        MyOpenHelper helper2 = new MyOpenHelper(this);
        final SQLiteDatabase db2 = helper2.getWritableDatabase();

        // queryメソッドの実行例
        //query(テーブル名,column(nullなら全部),selectionArgs,groupby,having,orderby)
        //Cursor c = db.query("person", new String[]{"name", "link","latitude","longitude"}, null,
        //        null, null, null, null);
        /*
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
*/
        Cursor c2 = db.query("person", new String[]{"photo", "name"}, null,
                null, null, null, null);
        // カーソルから値を取り出す
        while (c2.moveToNext()) {
            // 表示用LinearLayout
            final LinearLayout linear = new LinearLayout(this);
            // idとファイル名を受け取りTextViewとして表示
            String str = c2.getString(c2.getColumnIndex("name"));
            TextView tv = new TextView(this);
            //TextView tv = (TextView) findViewById(R.id.title);
            tv.setId(0);
            tv.setText(str);
            tv.setTextSize(18.0f);
            // BLOBをbyte[]で受け取る.
            byte blob[] = c2.getBlob(c2.getColumnIndex("photo"));
            // byte[]をビットマップに変換しImageViewとして表示
            Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            ImageView iv = new ImageView(this);
            //ImageView iv = (ImageView) findViewById(R.id.thumbnail_image);
            iv.setImageBitmap(bmp);

            linear.addView(iv);
            linear.addView(tv);
            linear.setTag(tv);
            llayout.addView(linear);

            linear.setOnClickListener(this);
            linear.setOnTouchListener(new View.OnTouchListener() {
                // ボタンがタッチされた時のハンドラ
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // 指がタッチした時の処理を記述
                        v.setBackgroundColor(Color.parseColor("#808080"));
                        //Log.v("OnTouch", "Touch Down");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // タッチした指が離れた時の処理を記述
                        v.setBackgroundColor(0xffffffff);
                        //Log.v("OnTouch", "Touch Up");
                    }
                    return false;
                }
            });
            linear.setOnLongClickListener(new View.OnLongClickListener() {
                // ボタンが長押しクリックされた時のハンドラ
                @Override
                public boolean onLongClick(final View v) {
                    // 長押しクリックされた時の処理を記述
                    // 確認ダイアログの生成
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(ShowDataBase.this);
                    alertDlg.setTitle("確認");
                    alertDlg.setMessage("選択したデータを削除しますか？");
                    alertDlg.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //OK ボタンクリック処理(DB削除)
                                    db2.delete("person", "name = ?", new String[]{((TextView) v.findViewById(0)).getText().toString()});
                                    llayout.removeView(v);
                                    Toast.makeText(getApplicationContext(),"削除しました。", Toast.LENGTH_LONG).show();
                                }
                            });
                    alertDlg.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Cancel ボタンクリック処理
                                }
                            });
                    // 表示
                    alertDlg.create().show();
                    //Log.v("OnLongClick", "Button was clicked");
                    return false;
                }
            });
        }

        c2.close();
        db.close();
    }

    public void onClick(View v){
        Intent i = new Intent();
        i.putExtra("name",((TextView)v.findViewById(0)).getText().toString());
        setResult(1001,i);
        finish();
        //Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_LONG).show();
    }
/*
    public Object getTag(){
        return view.getTag();
    }
    */
}
