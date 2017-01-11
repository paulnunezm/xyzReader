package com.example.xyzreader.ui.articleList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.utils.DynamicHeightNetworkImageView;
import com.example.xyzreader.ui.utils.ImageLoaderHelper;

import static android.content.ContentValues.TAG;

/**
 * Created by paulnunez on 10/2/16.
 */

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {
  private Cursor  mCursor;
  private Context mContext;

  public ArticlesAdapter(Cursor cursor, Context context) {
    mCursor = cursor;
    mContext = context;
  }

  @Override
  public long getItemId(int position) {
    mCursor.moveToPosition(position);
    return mCursor.getLong(ArticleLoader.Query._ID);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View             view = LayoutInflater.from(mContext).inflate(R.layout.list_item_article, parent, false);
    final ViewHolder vh   = new ViewHolder(view);
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
//            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));

        Bundle bundle = ActivityOptionsCompat
               .makeSceneTransitionAnimation(
                   (Activity) mContext,
                   vh.thumbnailView,
                   vh.thumbnailView.getTransitionName()
               ).toBundle();

        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
            ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))), bundle);
//        Log.d(TAG, "onClick: id -> "+vh.getAdapterPosition()+"\n "+getItemId(vh.getAdapterPosition()));
      }
    });
    return vh;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    mCursor.moveToPosition(position);
    holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
    holder.subtitleView.setText(
        DateUtils.getRelativeTimeSpanString(
            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_ALL).toString()
            + " by "
            + mCursor.getString(ArticleLoader.Query.AUTHOR));

    holder.thumbnailView.setImageUrl(
        mCursor.getString(ArticleLoader.Query.THUMB_URL),
        ImageLoaderHelper.getInstance(mContext).getImageLoader());
    holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      setTransitionName(position, holder);
    }

  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void setTransitionName(int position, ViewHolder holder) {
    Log.i(TAG, "setTransitionName: "+ mContext.getString(R.string.item_transition) + " " + getItemId(holder.getAdapterPosition()));

    holder.thumbnailView.setTransitionName(
        mContext.getString(R.string.item_transition) + " " + getItemId(holder.getAdapterPosition()));
  }

  @Override
  public int getItemCount() {
    return mCursor.getCount();
  }


  public static class ViewHolder extends RecyclerView.ViewHolder {
    DynamicHeightNetworkImageView thumbnailView;
    TextView                      titleView;
    TextView                      subtitleView;

    public ViewHolder(View view) {
      super(view);
      thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
      titleView = (TextView) view.findViewById(R.id.article_title);
      subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
    }
  }
}

