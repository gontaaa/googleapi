package com.example.hiroki.testgoogleapi;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

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
    private List<Marker> markerArray = new ArrayList<Marker>();
    //マーカー
    private Marker marker = null;

    //画面下の検索ボタン
    private Button buttonSearch;
    //画面下のクリアボタン
    private Button buttonClear;
    //画面上の検索ボタン
    private Button buttonSearch2;
    //画面上のエディトテキスト
    private EditText editText;

    //hotpepperAPIで検索した店のURLを一時的に格納する変数
    private String link;

    private PopupWindow popupWindow;

    //twitterの認証系
    private boolean twitterOauthOK = false;
    private static String consumerKey = null;
    private static String consumerSecret = null;
    private static String accessToken = null;
    private static String accessTokenSecret = null;

    //twitterにキーワード検索をかける際のキーワードを格納するための変数
    private static String keyword = "kobe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        //ボタンの設置
        //buttonSearch = (Button) findViewById(R.id.buttonSearch);
        //buttonSearch.setOnClickListener(this);

        buttonClear = (Button) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(this);

        buttonSearch2 = (Button) findViewById(R.id.buttonSearch2);
        buttonSearch2.setOnClickListener(this);


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

            //地図の中心地の位置を取得
            LatLng latLng = mMap.getCameraPosition().target;
            //地図の中心点の緯度・経度を取得して格納
            nowLat = latLng.latitude;
            nowLon = latLng.longitude;
            //表示
            Log.d("TestGoogleAPI", String.valueOf(nowLat) + '\n' + String.valueOf(nowLon));

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

                        //すべての店情報を取り出す
                        for (int i = 0; i < mListAdapter.getCount(); i++) {
                            //Log.d("mListAdapter", ((ApiGourmetResponse.Shop)mListAdapter.getItem(i)).getName());

                            //Object型の店情報をキャスト
                            ApiGourmetResponse.Shop tmpShop = ((ApiGourmetResponse.Shop) mListAdapter.getItem(i));

                            //マーカをつける、マーカーに情報の追加(店名、住所、開店時間、閉店時間)
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(tmpShop.getLat(), tmpShop.getLng()))
                                    .title(tmpShop.getName())
                                            // .snippet("address:" + tmpShop.getAddress() + '\n'
                                            //        + "open:" + tmpShop.getOpen() + '\n'
                                            //       + "close:" + tmpShop.getClose())
                                            //      // + "url:" + tmpShop.getUrl().getMobile())
                                    .draggable(false));

                            //店のホームページのURLを格納
                            link = "url:" + tmpShop.getUrl().getMobile();

                            markerArray.add(marker); // リストに格納（削除する為に必要）
                            //infoWindowを作成
                           // mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

                            //マーカにクリックリスナーをつける
                            mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    //twitter検索のために店名を取得しておく
                                    keyword = marker.getTitle();

                                    //TODO ここに処理を加える(画面下になんか出すとか)

                                    //タップ確認のためトーストを表示
                                    //Toast.makeText(getApplicationContext(), "マーカータップ", Toast.LENGTH_LONG).show();
                                    return false;
                                }
                            });

                            //クリックでポップアップウィンドウを表示
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    popupWindow = new PopupWindow(MapsActivity.this);

                                    View popupView
                                            //= (LinearLayout) getLayoutInflater().inflate(R.layout.info_window_main, null);
                                            = (LinearLayout) getLayoutInflater().inflate(R.layout.popup_window, null);

                                    //popupwindow内のyesボタンが押された時
                                    popupView.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // TODO リンク飛ばす処理
                                            Uri uri = Uri.parse(link);
                                            Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                            startActivity(i);
                                            //Toast.makeText(MapsActivity.this, "hoge", Toast.LENGTH_SHORT).show();
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

                                                //TODO twitter検索画面に遷移
                                                Intent intent = new Intent(MapsActivity.this, TwitterSearch.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });

                                    //popupwindow内のnoボタンが押された時
                                    popupView.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
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
                                    // TODO Auto-generated method stub
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
            for (int i = 0; i < markerArray.size(); i++) {
                markerArray.get(i).remove();
            }
            markerArray.clear();
            Toast.makeText(this, "マーカーを削除しました", Toast.LENGTH_LONG).show();
        }
        /*
        else if (v == buttonSearch) {
            //画面下の検索ボタンが押されたとき

            PlaceholderFragment f = new PlaceholderFragment();
            android.app.FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.map, f);
            //addToBackStackしておくこれでBackStackに登録される("戻る"が適応されるようになる)
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }*/
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

    //マーカーを動かす
    public void animateMarker(final Marker marker, final LatLng startPosition, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        //Point startPoint = proj.toScreenLocation(marker.getPosition());
        //final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5000;

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
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    //マーカがタップされたときに表示するinfowindowのクラス
    class CustomInfoWindowAdapter implements InfoWindowAdapter {
        private final View infoWindow;

        //画面の作成
        CustomInfoWindowAdapter() {
            infoWindow = getLayoutInflater().inflate(R.layout.info_window_main, null);
        }

        //infowindow内のビューの追加
        public View getInfoWindow(Marker marker) {
            String title = marker.getTitle();
            TextView textViewTitle = (TextView) infoWindow.findViewById(R.id.title);
            textViewTitle.setText(title);

            String snippet = marker.getSnippet();
            TextView textViewSnippet = (TextView) infoWindow.findViewById(R.id.snippet);
            textViewSnippet.setText(snippet);

            TextView textViewLink = (TextView) infoWindow.findViewById(R.id.link);
            textViewLink.setText(link);

            return infoWindow;
        }

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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Google Mapの Zoom値を指定
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    //twitter検索のためのクラス

    /**
     * A placeholder fragment containing a simple view.
     */
    /*
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Status>> {

        // Twitterオブジェクト
        private Twitter twitter = null;

        // ローディング表示用ダイアログ
        private ProgressDialog progressDialog = null;

        //認証設定とTwitterオブジェクトの初期化
        public PlaceholderFragment() {

            // OAuth認証用設定
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setOAuthConsumerKey(consumerKey);
            configurationBuilder.setOAuthConsumerSecret(consumerSecret);
            configurationBuilder.setOAuthAccessToken(accessToken);
            configurationBuilder.setOAuthAccessTokenSecret(accessTokenSecret);

            // Twitterオブジェクトの初期化
            this.twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
        }

        // ローディングダイアログの消去
        private void dialogDismiss() {
            if (this.progressDialog != null) {
                this.progressDialog.dismiss();
                this.progressDialog = null;
            }
        }

        //検索結果を表示するビューの作成
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup map,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, map,
                    false);
            return rootView;
        }


        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // 検索ボタンのイベントリスナーを設定する
            ((Button) getActivity().findViewById(R.id.button1))
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("keyword = " + keyword);
                            // ローダーの開始
                            getLoaderManager().restartLoader(0, null, PlaceholderFragment.this);
                        }
                    });
            setRetainInstance(true);
        }

        @Override
        public Loader<List<Status>> onCreateLoader(int id, Bundle args) {
            // ローディングダイアログの表示
            this.progressDialog = ProgressDialog.show(getActivity(), "Please wait", "Loading data...");

            SearchAsyncLoader loader = null;

            switch (id) {
                case 0:
                    // ローダーの初期化
                    loader = new SearchAsyncLoader(getActivity(), this.twitter, keyword);
                    loader.forceLoad();
                    break;
            }
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<Status>> loader, List<Status> data) {

            if (data != null) {

                // アダプターの初期化
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1);

                // アダプターにTweetをセットする
                for (Status tweet : data) {
                    adapter.add(tweet.getText());
                }

                // ListViewにアダプターをセットする
                ((ListView) getView().findViewById(R.id.listView1)).setAdapter(adapter);
            }
            // ローディングダイアログの消去
            dialogDismiss();
        }

        @Override
        public void onLoaderReset(Loader<List<Status>> loader) {
        }

        @Override
        public void onPause() {
            super.onPause();

            // ローディングダイアログの消去
            dialogDismiss();
        }
    }
*/

    /*
    private void replaceFragment(Fragment0 f) {
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.map, f);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/

    //androidの戻るが押された時の処理
    /*
    public void onBackPressed() {
        int backStackCnt =
                getFragmentManager().getBackStackEntryCount();
        //表示されているfragmentの数？(戻ることのできるfragmentの数？)
        System.out.println("backStackCnt = " + backStackCnt);
        if (backStackCnt != 0) {
            //stackされていたfragmentをpopする
            getFragmentManager().popBackStack();
        } else {
            //TODO 0のときはアプリの終了をさせたい
        }
    }
    */

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}