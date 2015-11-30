package com.example.hiroki.testgoogleapi;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

//twitterAPIでキーワード検索を行うクラス

/**
 * Created by hiroki on 15/11/22.
 */
public class SearchAsyncLoader extends AsyncTaskLoader<List<Status>> {
    private Twitter twitter;
    private String keyword;

    public SearchAsyncLoader(Context context, Twitter _twitter, String key) {
        super(context);
        this.twitter = _twitter;
        this.keyword = key;
    }

    @Override
    public List<Status> loadInBackground() {

        try {
            // 大阪のWOEID
            //int osaka = 15015370;

            // トレンドを取得する
            //Trend[] trend = this.twitter.getPlaceTrends(osaka).getTrends();

            // 取得したトレンドから、ランダムで１つを選択する
            //Random rnd = new Random();
            //String q = trend[rnd.nextInt(trend.length)].getQuery();

            // 検索文字列を設定する
            Query query = new Query(keyword);
            query.setLocale("ja");    // 日本語のtweetに限定する
            query.setCount(20);        // 最大20tweetにする（デフォルトは15）

            // 検索の実行
            QueryResult result = this.twitter.search(query);

            return result.getTweets();

        } catch (TwitterException e) {
            Log.d("twitter", e.getMessage());
        }

        return null;
    }

}
