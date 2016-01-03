package com.example.hiroki.testgoogleapi;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//import android.location.LocationProvider;
//import android.provider.Settings;
//import android.widget.ListView;
//import com.google.android.gms.maps.model.LatLng;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationManager;
//import android.location.LocationProvider;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import android.widget.Toast;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, OnClickListener {

    private GoogleMap mMap;
    //private RequestQueue mRequestQueue;
    //private LocationManager locationManager;

    private static final String TAG = "MapsActivity";

    //hotpepperのAPIを検索する際のキーワードを格納する変数
    private static String API_KEYWORD = null;// = "ラーメン";

    //hotpepperAPIの検索範囲
    private static final int API_RANGE = 5; // 3000m

    //現在地の緯度経度
    public double latitude = 0;
    public double longitude = 0;

    //地図の中心地の緯度経度
    public double nowLat = 0;
    public double nowLon = 0;

    //private ListView mListView;
    //hotpepperで検索したお店のデータを格納するクラスの変数
    private ShopListAdapter mListAdapter;

    //マーカーのリスト
    private List<Marker> arrayMarker = new ArrayList<Marker>();
    //markerごとのURLを保存
    private Map<Marker, String> markerArray = new HashMap<Marker, String>();
    //店名ごとの写真を保存
    private Map<String, Bitmap> photoArray = new HashMap<String, Bitmap>();
    //
    private List<Marker> markerArray2 = new ArrayList<Marker>();
    //ピンのリスト
    private List<Marker> pinArray = new ArrayList<Marker>();


    //マーカー
    private Marker marker = null;
    //ピン
    private Marker pin = null;

    //画面下の検索ボタン
    private Button buttonSearch;
    //画面下のクリアボタン
    private Button buttonClear;
    //画面上の検索ボタン
    private Button buttonSearch2;
    //画面上のエディトテキスト
    private EditText editText;
    //お気に入りしたデータを参照するボタン
    private Button referenceButton;

    //private SearchView mSearchView;
    //private Button smallbutton;

    //hotpepperAPIで検索した店のURLを一時的に格納する変数
    private String link;

    //
    private PopupWindow popupWindow;

    //twitterの認証系
    private boolean twitterOauthOK = false;
    private static String consumerKey = null;
    private static String consumerSecret = null;
    private static String accessToken = null;
    private static String accessTokenSecret = null;

    //twitterにキーワード検索をかける際のキーワードを格納するための変数
    private static String keyword = "kobe";

    //現在タップしているマーカーの位置情報を格納しておく変数
    private LatLng location;

    private int tmpCount = 0;

    //非同期処理を同期するための変数
    private final CountDownLatch mDone = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //TODO お気に入りリスト、検索履歴リスト

        /*
        //まずtwitterの認証を確認する
        twitterOauthOK = TwitterUtils.hasAccessToken(this);

        //未確認の時
        if (!twitterOauthOK) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            //確認済みのとき
            System.out.println("TwitterUtils.hasAccessToken(this)="
                    + TwitterUtils.hasAccessToken(this));

            //認証系の保存
            consumerKey = TwitterUtils.getConsumerKey(this);
            consumerSecret = TwitterUtils.getConsumerSecret(this);
            accessToken = TwitterUtils.loadAccessToken(this).getToken();
            accessTokenSecret = TwitterUtils.loadAccessToken(this).getTokenSecret();
        }
        */

        //mListView = (ListView) findViewById(R.id.list_view);
        mListAdapter = new ShopListAdapter(this);
        //mListView.setAdapter(mListAdapter);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragmet. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/

        //アイコンを追加する場合は、
        // BitmapDescriptor icon = BitmapDescriptorFactory.fromResourse(R.drawable.temple_pin);
        // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker")).setIcon(icon);
        //すればよい。
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //現在地ボタンの追加
        mMap.setMyLocationEnabled(true);

        //渋滞情報
        //mMap.setTrafficEnabled(true);

        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.search);

        mSearchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        Menu menu = toolbar.getMenu();
        MenuItem item = menu.add("検索");
        item.setIcon(R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setActionView(mSearchView);
        //mSearchView.setIconfied(false);
*/

        //ボタンの設置
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);

        buttonClear = (Button) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(this);

        buttonSearch2 = (Button) findViewById(R.id.buttonSearch2);
        buttonSearch2.setOnClickListener(this);

        referenceButton = (Button) findViewById(R.id.referenceButton);
        referenceButton.setOnClickListener(this);

        //searchView = (Button) findViewById(R.id.searchView);
        //searchView.setOnClickListener(this);

        //smallbutton = (Button) findViewById(R.id.smallbutton);

        // EditTextオブジェクトを取得
        editText = (EditText) findViewById(R.id.editText);

        //フォーカス操作
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // EditTextのフォーカスが外れた場合
                if (!hasFocus) {
                    // ソフトキーボードを非表示にする
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        //editText.clearFocus();

        if (mMap != null) {
            // タップ時のイベントハンドラ登録
            /*
            mMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    //Toast.makeText(getApplicationContext(),
                      //      "タップ位置\n緯度：" + point.latitude + "\n経度:" + point.longitude,
                        //    Toast.LENGTH_LONG).show();
                }
            });
            */
            // 長押し時のイベントハンドラ登録
            mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {
                    if (pinArray.size() != 0) {
                        // 既存のマーカーを消す処理
                        for (int i = 0; i < pinArray.size(); i++) {
                            pinArray.get(i).remove();
                        }
                        pinArray.clear();
                    }

                    // ピンを立てる
                    LatLng position = new LatLng(point.latitude, point.longitude);
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.focuspin);
                    pin = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(icon)
                                    //.title(tmpShop.getName
                            .draggable(true));
                    pinArray.add(pin);

                    Toast.makeText(getApplicationContext(),
                            "ピンの位置\n緯度：" + point.latitude + "\n経度:" + point.longitude,
                            Toast.LENGTH_LONG).show();

                    //TODO クリックできないようにする
                    // タップ時のイベントハンドラ登録
                    /*
                    mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Toast.makeText(getApplicationContext(), "マーカータップ", Toast.LENGTH_LONG).show();
                            return false;
                            }
                        });
                    */

                    //検索するピンの位置
                    nowLat = point.latitude;
                    nowLon = point.longitude;

                    // ドラッグ時のイベントハンドラ登録
                    mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
                        @Override
                        public void onMarkerDrag(Marker marker) {
                            // Toast.makeText(getApplicationContext(), "マーカードラッグ中", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            //検索するピンの位置
                            nowLat = marker.getPosition().latitude;
                            nowLon = marker.getPosition().longitude;
                            Toast.makeText(getApplicationContext(),
                                    "ピンの位置\n緯度：" + nowLat + "\n経度:" + nowLon,
                                    Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(), "マーカードラッグ終了", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            //Toast.makeText(getApplicationContext(), "マーカードラッグ開始", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            });

        }


        // 入力された文字を取得
        //String API_KEYWORD = editText.getText().toString();

        //システムサービスのLOCATION_SERVICEからLocationManager objectを取得
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //retrieve providerへcriteria objectを生成
        Criteria criteria = new Criteria();
        //Best providerの名前を取得
        String provider = locationManager.getBestProvider(criteria, true);
        //現在位置を取得
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        //現在位置からLatLng objectを生成
        LatLng latLng = new LatLng(latitude, longitude);
        //Google Mapに現在地を表示
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //Google Mapの Zoom値を指定
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
    //航空写真
    //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    //ピンの重なりを除去するには、Marker Clusterを利用

    //ボタンクリック時の動作
    public void onClick(View v) {
        //editTextのフォーカスをはずす
        editText.clearFocus();
        /*
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();*/

        //画面上の検索ボタン
        if (v == buttonSearch2) {
            //Toast.makeText(this, R.string.message , Toast.LENGTH_LONG).show();

            //入力された検索キーワードをセット(ない場合も大丈夫)
            API_KEYWORD = editText.getText().toString();

            //TODO 現在地に変える？
            if (pinArray.size() == 0) {
                //地図の中心地の位置を取得
                LatLng latLng = mMap.getCameraPosition().target;
                //地図の中心点の緯度・経度を取得して格納
                nowLat = latLng.latitude;
                nowLon = latLng.longitude;
                //表示
                //Log.d("TestGoogleAPI", String.valueOf(nowLat) + '\n' + String.valueOf(nowLon));
            }

            //APIのインターフェースを作成
            ApiInterface api = ApiClientManager.create(ApiInterface.class);
            //ホットペッパーAPIで検索をかける
            //api.gourmet(BuildConfig.API_KEY, API_KEYWORD,34.7196324,135.2441574, API_RANGE, new Callback<ApiGourmetResponse>() { //六甲道
            api.gourmet(BuildConfig.API_KEY, API_KEYWORD, nowLat, nowLon, API_RANGE, new Callback<ApiGourmetResponse>() {

                @Override //成功時
                public void success(final ApiGourmetResponse apiGourmetResponse, Response response) {
                    //mListAdapter.listClear();

                    //店情報があるとき
                    if (apiGourmetResponse.getResults().getShop().size() != 0) {

                        //店情報をリストにして保管
                        mListAdapter.setShop(apiGourmetResponse.getResults().getShop());
                        mListAdapter.notifyDataSetChanged();//更新?

                        //見つかった件数をトースト表示
                        String text = String.valueOf(mListAdapter.getCount()) + "件見つかりました。";
                        Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
                        //Log.d("ListSize", String.valueOf(mListAdapter.getCount()));

                        //インデックス
                        int j=0;
                        int tmpcount=-1;
                        //非同期処理の同期で画像の読み込みを行う
                        while(j<mListAdapter.getCount()){
                            //非同期処理の同期後に実行するためのif文
                            if(tmpcount!=j) {
                                //画像を読み込んで店名ごとにphotoArrayにbitmapとして格納
                                try {
                                    tmpcount=j;
                                    final ApiGourmetResponse.Shop tmpShop = ((ApiGourmetResponse.Shop) mListAdapter.getItem(j));
                                    String mainImageUrl = tmpShop.getPhoto().getPc().getS();
                                    System.out.println("mainImageUrl = " + mainImageUrl);
                                    //thumbnail_image1 = (ImageView) infoWindow.findViewById(R.id.thumbnail_image1);
                                    ImageLoader loader = ImageLoader.getInstance();
                                    loader.loadImage(mainImageUrl, new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            photoArray.put(tmpShop.getName(), loadedImage);
                                            System.out.println("arrayOK ");
                                            //System.out.println("arrayOK " + tmpShop.getPhoto().getPc().getS());
                                            //thumbnail_image.setImageBitmap(loadedImage);
                                        }
                                    });
                                } finally {
                                    //終了後にカウントダウン
                                    mDone.countDown();
                                }
                            }
                            //カウントダウンされたら次の読み込みをするためにインデックス変更
                            try {
                                mDone.await();
                                j++;
                            } catch(InterruptedException e) {
                            }
                        }

                        //すべての店情報を取り出す
                        for (int i = 0; i < mListAdapter.getCount(); i++) {
                            //Log.d("mListAdapter", ((ApiGourmetResponse.Shop)mListAdapter.getItem(i)).getName());

                            //Object型の店情報をキャスト
                            ApiGourmetResponse.Shop tmpShop = ((ApiGourmetResponse.Shop) mListAdapter.getItem(i));

                            //マーカをつける、マーカーに情報の追加(店名、(画像のURL))
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(tmpShop.getLat(), tmpShop.getLng()))
                                    .title(tmpShop.getName())
                                            // .snippet("address:" + tmpShop.getAddress() + '\n'
                                            //        + "open:" + tmpShop.getOpen() + '\n'
                                            //       + "close:" + tmpShop.getClose())
                                            //.snippet("url:" + tmpShop.getUrl().getPc())
                                    .snippet(tmpShop.getPhoto().getPc().getS())
                                    .draggable(false));

                            //店のホームページのURLを格納
                            //link = tmpShop.getUrl().getPc();
                            System.out.println("name = "+tmpShop.getName());
                            System.out.println("photoimage = " + tmpShop.getPhoto().getPc().getS());

                            arrayMarker.add(marker);// リストに格納（削除する為に必要）
                            markerArray.put(marker, tmpShop.getUrl().getPc()); //店のurlをマーカーごとに格納

                            /*
                            String mainImageUrl = marker.getSnippet();
                            System.out.println("mainImageUrl = "+mainImageUrl);
                            //thumbnail_image1 = (ImageView) infoWindow.findViewById(R.id.thumbnail_image1);
                            ImageLoader loader = ImageLoader.getInstance();
                            try {
                                loader.loadImage(mainImageUrl, new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        //photoArray.put(marker, loadedImage);
                                        System.out.println("arrayOK ");
                                        //System.out.println("arrayOK " + tmpShop.getPhoto().getPc().getS());
                                        //thumbnail_image.setImageBitmap(loadedImage);
                                    }
                                });
                            }finally{
                                mDone.countDown();
                            }

                            try {
                                mDone.await();
                            } catch(InterruptedException e) {
                            }
*/
                            //infoWindowを作成
                            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

                            //マーカにクリックリスナーをつける
                            mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    //twitter検索のために店名を取得しておく
                                    keyword = marker.getTitle();
                                    location = marker.getPosition();
                                    return false;
                                }
                            });

                            //クリックでポップアップウィンドウを表示
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(final Marker marker) {
                                    popupWindow = new PopupWindow(MapsActivity.this);
                                    View popupView
                                            = (LinearLayout) getLayoutInflater().inflate(R.layout.popup_window, null);

                                    //popupwindow内のyesボタンが押された時
                                    popupView.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //markerArrayから格納していたURLを取得
                                            link = markerArray.get(marker);
                                            //インテントを使いhomepageにアクセス
                                            try {
                                                Uri uri = Uri.parse(link);
                                                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(i);
                                                //Toast.makeText(MapsActivity.this, "hoge", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                //データを処理できるアプリがインストールされていません
                                            }
                                        }
                                    });

                                    //popupwindow内のtwitterボタンが押された時
                                    popupView.findViewById(R.id.twitter_button).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //TODO twiter検索

                                            //まずtwitterの認証を確認する
                                            twitterOauthOK = TwitterUtils.hasAccessToken(MapsActivity.this);
                                            //twitterOauthOK = false;
                                            //未確認の時
                                            if (!twitterOauthOK) {
                                                Intent intent = new Intent(MapsActivity.this, TwitterOAuthActivity.class);
                                                startActivity(intent);
                                                //finish();
                                            } else {
                                                //確認済みのとき
                                                System.out.println("TwitterUtils.hasAccessToken(this)="
                                                        + TwitterUtils.hasAccessToken(MapsActivity.this));

                                                //認証系の保存
                                                consumerKey = TwitterUtils.getConsumerKey(MapsActivity.this);
                                                consumerSecret = TwitterUtils.getConsumerSecret(MapsActivity.this);
                                                accessToken = TwitterUtils.loadAccessToken(MapsActivity.this).getToken();
                                                accessTokenSecret = TwitterUtils.loadAccessToken(MapsActivity.this).getTokenSecret();

                                                //検索画面に遷移
                                                Intent intent = new Intent(MapsActivity.this, TwitterSearch.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    //popupwindow内のfavボタンが押された時
                                    popupView.findViewById(R.id.fav_button).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //TODO fav
                                            //DBの設定
                                            MyOpenHelper helper = new MyOpenHelper(MapsActivity.this);
                                            final SQLiteDatabase db = helper.getWritableDatabase();

                                            //店名をnameに格納
                                            String name = marker.getTitle();
                                            tmpCount++;

                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            photoArray.get(marker.getTitle()).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                            byte[] bitmapdata = stream.toByteArray();

                                            //挿入項目を作成
                                            ContentValues insertValues = new ContentValues();
                                            insertValues.put("name", name);
                                            insertValues.put("link", markerArray.get(marker));
                                            insertValues.put("latitude", marker.getPosition().latitude);
                                            insertValues.put("longitude", marker.getPosition().longitude);
                                            insertValues.put("photo", bitmapdata);

                                            System.out.println("tmpCount = " + tmpCount);
                                            if (tmpCount % 3 != 0) {
                                                System.out.println("insertValues = " + name + markerArray.get(marker)
                                                        + marker.getPosition().latitude
                                                        + marker.getPosition().longitude
                                                        //+ marker.getSnippet()
                                                );
                                                //DBにデータを挿入
                                                long id = db.insert("person", null, insertValues);
                                            } else {
                                                //DBを削除
                                                db.delete("person", null, null);
                                            }
                                        }
                                    });

                                    //popupwindow内のnoボタンが押された時
                                    popupView.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //popupwindowを閉じる
                                            if (popupWindow.isShowing()) {
                                                popupWindow.dismiss();
                                            }
                                        }
                                    });

                                    //popupWindow.setWindowLayoutMode(
                                    //LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    //popupWindow.setWindowLayoutType(0);
                                    popupWindow.setContentView(popupView);

                                    // タップ時に他のViewでキャッチされないための設定
                                    popupWindow.setOutsideTouchable(true);
                                    popupWindow.setFocusable(true);

                                    float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                                    popupWindow.setWindowLayoutMode((int) width, WindowManager.LayoutParams.WRAP_CONTENT);
                                    popupWindow.setWidth((int) width);
                                    popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

                                    popupWindow.showAtLocation(findViewById(R.id.map), Gravity.CENTER, 0, 0);
                                    //Toast.makeText(getApplicationContext(), "インフォウィンドウクリック", Toast.LENGTH_LONG).show();

                                    //ウィンドウの中身が徐々に浮かび上がる
                                    animateAlpha(popupView);
                                }
                            });

                        }

                    } else {
                        //周辺に店が見つからなかった場合
                        //トースト表示
                        Toast.makeText(getBaseContext(), "この周辺に店舗は見当たりません。", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    //Toast.makeText(MainActivity.this, R.string.no_shop_available, Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Failed to API access.");
                }
            });
        } else if (v == buttonClear) {
            //クリアーボタンが押されたとき

            Log.d("buttonClear", "clear");
            // 既存のマーカーを消す処理
            for (int i = 0; i < arrayMarker.size(); i++) {
                    arrayMarker.get(i).remove();
            }
            arrayMarker.clear();

            //マーカが削除されたので座標情報も削除
            location = null;

            Toast.makeText(this, "マーカーを削除しました", Toast.LENGTH_LONG).show();
        } else if (v == buttonSearch) {

            if (location == null) {
                Toast.makeText(this, "検索したいお店のマーカーをタップして下さい。", Toast.LENGTH_LONG).show();
            } else {
                //画面下の検索ボタンが押されたとき
                LatLng startLocation;
                Marker marker;
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ruun);

                MarkerOptions options = new MarkerOptions();

                //第一象限
                for (int i = 0; i < 5; i++) {
                    startLocation = moveLatLng(location, 0.1 - i * 0.02, i * 0.02);
                    options.position(startLocation);
                    options.icon(icon);
                    marker = mMap.addMarker(options);
                    markerArray2.add(marker);
                    animateMarker(marker, startLocation, location, true);
                }

                //第四象限
                for (int i = 0; i < 5; i++) {
                    startLocation = moveLatLng(location, 0 - i * 0.02, 0.1 - i * 0.02);
                    options.position(startLocation);
                    options.icon(icon);
                    marker = mMap.addMarker(options);
                    markerArray2.add(marker);
                    animateMarker(marker, startLocation, location, true);
                }

                //第三象限
                for (int i = 0; i < 5; i++) {
                    startLocation = moveLatLng(location, -0.1 + i * 0.02, 0 - i * 0.02);
                    options.position(startLocation);
                    options.icon(icon);
                    marker = mMap.addMarker(options);
                    markerArray2.add(marker);
                    animateMarker(marker, startLocation, location, true);
                }

                //第二象限
                for (int i = 0; i < 5; i++) {
                    startLocation = moveLatLng(location, i * 0.02, -0.1 + i * 0.02);
                    options.position(startLocation);
                    options.icon(icon);
                    marker = mMap.addMarker(options);
                    markerArray2.add(marker);
                    animateMarker(marker, startLocation, location, true);
                }

            }
        } else if (v == referenceButton) {
            //fabしたDBを表示
            Intent dbIntent = new Intent(MapsActivity.this,
                    ShowDataBase.class);
            startActivity(dbIntent);

            /*
            ShowDataBase obj = new ShowDataBase();
            if(!obj.getTag().equals(null)) {
                System.out.println("obj = "+obj.getTag().toString());
            }else{
                System.out.println("null");
                System.out.println(obj.getTag().toString());
            }
            */
        }
    }

    /**
     * 3秒かけてターゲットを表示
     */
    private void animateAlpha( View view ) {

        // alphaプロパティを0fから1fに変化させます
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat( view, "alpha", 0f, 1f );

        // 3秒かけて実行させます
        objectAnimator.setDuration(1500);

        // アニメーションを開始します
        objectAnimator.start();
    }

    /**
     * X方向にターゲットを3秒かけて200移動する
     */
    private void animateTranslationX( Button button ) {

        // translationXプロパティを0fから200fに変化させます
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat( button, "translationX", 0f, 200f );

        // 3秒かけて実行させます
        objectAnimator.setDuration(3000);

        // アニメーションを開始します
        objectAnimator.start();
    }

    //マーカがタップされたときに表示するinfowindowのクラス
    class CustomInfoWindowAdapter implements InfoWindowAdapter {
        private final View infoWindow;
        ImageView thumbnail_image1 = new ImageView(MapsActivity.this);
        //AQuery aQuery;

        //画面の作成
        CustomInfoWindowAdapter() {
            infoWindow = getLayoutInflater().inflate(R.layout.info_window_main, null);
        }

        //infowindow内のビューの追加
        public View getInfoWindow(Marker marker) {
            //店名を表示
            String title = marker.getTitle();
            TextView textViewTitle = (TextView) infoWindow.findViewById(R.id.title);
            textViewTitle.setText(title);

            //保存しておいた店の画像表示
            thumbnail_image1 = (ImageView) infoWindow.findViewById(R.id.thumbnail_image1);
            thumbnail_image1.setImageBitmap(photoArray.get(marker.getTitle()));
            /*
            String mainImageUrl = marker.getSnippet();
            System.out.println("mainImageUrl = "+mainImageUrl);
            thumbnail_image1 = (ImageView) infoWindow.findViewById(R.id.thumbnail_image1);
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(mainImageUrl, thumbnail_image1);
            thumbnail_image1.setTag(mainImageUrl);
            thumbnail_image1.setImageResource(R.drawable.focuspin);
            */
/*
            thumbnail_image1 = (ImageView) infoWindow.findViewById(R.id.thumbnail_image1);
            aQuery = new AQuery(MapsActivity.this);
            aQuery.id(R.id.thumbnail_image1).image(marker.getSnippet());
*/

            //Drawable d = null;
            //thumbnail_image1 = (ImageView) infoWindow.findViewById(R.id.thumbnail_image1);
            //thumbnail_image1.setImageResource(R.drawable.focuspin);
            /*
            try {
                URL url = new URL(marker.getSnippet());
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.connect();
                InputStream in = http.getInputStream();
                d = Drawable.createFromStream(in, "");
                in.close();
                thumbnail_image1.setImageDrawable(m);
            }catch(Exception e){
            }
            */

            /*
            String mainImageUrl = marker.getSnippet();
            System.out.println("mainImageUrl = "+mainImageUrl);
            thumbnail_image1 = (ImageView) infoWindow.findViewById(R.id.thumbnail_image1);
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(mainImageUrl, thumbnail_image1);
            thumbnail_image1.setTag(mainImageUrl);
            */

            /*
            loader.loadImage(mainImageUrl, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    thumbnail_image.setImageBitmap(loadedImage);
                }
            });
            */
            //thumbnail_image.setImageDrawable(new ColorDrawable(0x00000000));
            //loader.displayImage(mainImageUrl, thumbnail_image1);
            //thumbnail_image1.setTag(mainImageUrl);
 /*
            TextView textViewSnippet = (TextView) infoWindow.findViewById(R.id.snippet);
            textViewSnippet.setText(snippet);

            TextView textViewLink = (TextView) infoWindow.findViewById(R.id.link);
            textViewLink.setText(link);
*/
            return infoWindow;
        }

        /*
        public Object fetch(String address) throws MalformedURLException,IOException {
            URL url = new URL(address);
            Object content = url.openConnection().getInputStream();
            return content;
        }
*/

        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void moveCamera2Target(boolean animation_effect, LatLng target, float zoom, float tilt, float bearing) {
        CameraPosition pos = new CameraPosition(target, zoom, tilt, bearing);
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(pos);


        if (animation_effect) {
            //
            mMap.animateCamera(camera);
        } else {
            //
            mMap.moveCamera(camera);
        }
    }


    //現在地が変化したときに現在地の緯度経度を取得する
    @Override
    public void onLocationChanged(Location location) {

        //現在位置の緯度を取得
        latitude = location.getLatitude();

        //現在位置の経度を取得
        longitude = location.getLongitude();

        //現在地が変わったことを通知
        Log.d("onLocationChanged", String.valueOf(location.getLatitude())
                + '\n' + String.valueOf(location.getLongitude()));
        //textview.setText(latitude + "," + longitude);
        //reverse_geocode(latitude,longitude);

        //現在位置からLatLng objectを生成
        LatLng latLng = new LatLng(latitude, longitude);

        //Google Mapに現在地を表示
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Google Mapの Zoom値を指定
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }


    //マーカーを動かす
    public void animateMarker(final Marker marker, final LatLng startPosition, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        //Projection proj = mMap.getProjection();
        //Point startPoint = proj.toScreenLocation(marker.getPosition());
        //final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 3000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startPosition.latitude;
                marker.setPosition(new LatLng(lat, lng));


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.remove();
                        setIcon(toPosition);
                    } else {
                        marker.setVisible(true);
                    }
                }

            }

        });
    }


    //緯度経度をずらす
    public LatLng moveLatLng(LatLng location, double x, double y) {
        LatLng newLocation = new LatLng(location.latitude + x, location.longitude + y);
        return newLocation;
    }

    public void setIcon(LatLng location) {
        LatLng location2 = moveLatLng(location, 0, 12.0d / (60 * 60));
        LatLng location3 = moveLatLng(location, 0, -12.0d / (60 * 60));

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.heart2);
        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.boo);

        // 貼り付設定
        GroundOverlayOptions overlayOptions = new GroundOverlayOptions();
        GroundOverlayOptions overlayOptions2 = new GroundOverlayOptions();
        overlayOptions.image(icon);
        overlayOptions2.image(icon2);

        //　public GroundOverlayOptions anchor (float u, float v)
        // (0,0):top-left, (0,1):bottom-left, (1,0):top-right, (1,1):bottom-right
        //中心に表示(?)
        overlayOptions.anchor(0.5f, 0.5f);
        overlayOptions2.anchor(0.75f, 0.5f);

        // 張り付け画像の大きさ メートル単位
        // public GroundOverlayOptions	position(LatLng location, float width, float height)
        overlayOptions.position(location2, 300f, 300f);
        overlayOptions2.position(location3, 300f, 300f);

        // マップに貼り付け・アルファを設定
        GroundOverlay overlay = mMap.addGroundOverlay(overlayOptions);
        GroundOverlay overlay2 = mMap.addGroundOverlay(overlayOptions2);
        // 透明度
        overlay.setTransparency(0.0F);
        overlay2.setTransparency(0.0F);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}