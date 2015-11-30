package com.example.hiroki.testgoogleapi;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collections;
import java.util.List;

//店情報のリスト

/**
 * Created by hiroki on 15/11/15.
 */
public class ShopListAdapter extends BaseAdapter {

    private List<ApiGourmetResponse.Shop> mShop = Collections.emptyList();

    private Context mContext;

    private LayoutInflater mInflater;

    private ImageLoader mImageLoader = ImageLoader.getInstance();

    public ShopListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setShop(List<ApiGourmetResponse.Shop> shop) {
        mShop = shop;
        if (mShop == null) {
            mShop = Collections.emptyList();
        }
    }

    @Override
    public int getCount() {
        return mShop.size();
    }

    @Override
    public Object getItem(int position) {
        return mShop.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

/*
    public void listClear(){
        mShop.clear();
    }
    */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            //convertView = mInflater.inflate(R.layout.shop_list_view, null);
            holder = new ViewHolder();
            // holder.mThumbnailView = (ImageView) convertView.findViewById(R.id.thumbnail_image);
            //holder.mTitleView = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ApiGourmetResponse.Shop shop = mShop.get(position);

        // イメージを表示する
        String mainImageUrl = shop.getLogoImage();
        if (TextUtils.isEmpty(mainImageUrl)) {
            // URLがない場合はnoimageを表示する
            holder.mThumbnailView.setImageDrawable(new ColorDrawable(0x00000000));
        } else {
            // 表示している内容と表示しようとしている内容が異なる場合はイメージを非同期で取得し表示する。
            // 表示している内容と表示しようとしている内容が同じ場合には何もしない。（画像表示部ちらつき防止の為）
            if (!mainImageUrl.equals(holder.mThumbnailView.getTag())) {
                // リストを上下すると一瞬前の画像が表示されるため、明示的にno_imageを入れる
                holder.mThumbnailView.setImageDrawable(new ColorDrawable(0x00000000));
                mImageLoader.displayImage(mainImageUrl, holder.mThumbnailView);
            }
        }
        holder.mThumbnailView.setTag(mainImageUrl); // ImageViewのタグにURLを入れる

        holder.mTitleView.setText(shop.getName());

        return convertView;
    }

    private static class ViewHolder {
        /*package*/ ImageView mThumbnailView;
        /*package*/ TextView mTitleView;
    }

}
