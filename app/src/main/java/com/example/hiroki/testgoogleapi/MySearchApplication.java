package com.example.hiroki.testgoogleapi;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by hiroki on 16/01/01.
 */
public class MySearchApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // ImageLoaderの初期化
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .displayer(new FadeInBitmapDisplayer(getApplicationContext().getResources().getInteger(R.integer.image_loader_fade_in_duration)))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
        // グローバル設定の生成と初期化を行う
        /*
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                // ImageLoaderConfigurationの設定をメソッドチェインで繋いでいく
        .build();
        ImageLoader.getInstance().init(config);
        */
    }
}
