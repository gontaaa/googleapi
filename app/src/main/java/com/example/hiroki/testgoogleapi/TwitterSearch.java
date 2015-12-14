package com.example.hiroki.testgoogleapi;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by hiroki on 15/12/13.
 */
public class TwitterSearch extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<Status>> {

    // Twitterオブジェクト
    private Twitter twitter = null;

    // ローディング表示用ダイアログ
    private ProgressDialog progressDialog = null;

    private String keyword = "しゅはり";

    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        // OAuth認証用設定
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
        configurationBuilder.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));
        configurationBuilder.setOAuthAccessToken(TwitterUtils.loadAccessToken(this).getToken());
        configurationBuilder.setOAuthAccessTokenSecret(TwitterUtils.loadAccessToken(this).getTokenSecret());

        // Twitterオブジェクトの初期化
        this.twitter = new TwitterFactory(configurationBuilder.build()).getInstance();

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

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //editTextのフォーカスをはずす
                editText.clearFocus();
                keyword = editText.getText().toString();
                //System.out.println("keyword = " + keyword);
                // ローダーの開始
                getLoaderManager().restartLoader(0, null, TwitterSearch.this);
            }
        });



    }

    //認証設定とTwitterオブジェクトの初期
    /*
    public TwitterSearch() {

        // OAuth認証用設定
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(getString(R.string.twitter_consumer_key));
        configurationBuilder.setOAuthConsumerSecret(getString(R.string.twitter_consumer_secret));
        //configurationBuilder.setOAuthAccessToken(TwitterUtils.loadAccessToken(this).getToken());
        //configurationBuilder.setOAuthAccessTokenSecret(TwitterUtils.loadAccessToken(MapsActivity.this).getTokenSecret());

        // Twitterオブジェクトの初期化
        this.twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
    }
    */

    // ローディングダイアログの消去
    private void dialogDismiss() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }
    }


    //検索結果を表示するビューの作成
    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup map,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, map,
                false);
        return rootView;
    }

/*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 検索ボタンのイベントリスナーを設定する
        ((Button) getActivity().findViewById(R.id.button1))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //System.out.println("keyword = " + keyword);
                        // ローダーの開始
                        getLoaderManager().restartLoader(0, null, null);
                        //getLoaderManager().restartLoader(0, null, PlaceholderFragment.this);
                    }
                });
        setRetainInstance(true);
    }
*/
    @Override
    public Loader<List<Status>> onCreateLoader(int id, Bundle args) {
        // ローディングダイアログの表示
        this.progressDialog = ProgressDialog.show(this, "Please wait", "Loading data...");

        SearchAsyncLoader loader = null;

        switch (id) {
            case 0:
                // ローダーの初期化
                loader = new SearchAsyncLoader(this, this.twitter, keyword);
                //loader = new SearchAsyncLoader(getActivity(), this.twitter, keyword);
                loader.forceLoad();
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Status>> loader, List<Status> data) {

        if (data != null) {

            // アダプターの初期化
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1);

            // アダプターにTweetをセットする
            for (Status tweet : data) {
                adapter.add(tweet.getText());
            }

            // ListViewにアダプターをセットする
            //((ListView) getView().findViewById(R.id.listView1)).setAdapter(adapter);
            ((ListView) findViewById(R.id.listView1)).setAdapter(adapter);
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
