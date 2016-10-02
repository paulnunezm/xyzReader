package com.example.xyzreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends ActionBarActivity implements
   android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

  private Toolbar            mToolbar;
  private View               mLogo;
  private AppBarLayout       mAppBarLayout;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private RecyclerView       mRecyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article_list);

    mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
    mLogo = findViewById(R.id.img_logo);
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

    getSupportLoaderManager().initLoader(0, null, this);

    if (savedInstanceState == null) {
      refresh();
//      introAnimation();
    }
    introAnimation();
  }

  private void refresh() {
    startService(new Intent(this, UpdaterService.class));
  }

  @Override
  protected void onStart() {
    super.onStart();
    registerReceiver(mRefreshingReceiver,
        new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
  }

  @Override
  protected void onStop() {
    super.onStop();
    unregisterReceiver(mRefreshingReceiver);
  }

  private boolean mIsRefreshing = false;

  private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
        mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
        updateRefreshingUI();
      }
    }
  };

  private void updateRefreshingUI() {
    mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
  }

  private void introAnimation(){
    Resources res = getResources();
    mAppBarLayout.setTranslationY(-res.getDimensionPixelSize(R.dimen.list_app_bar_layout_height));
    mLogo.setTranslationY(-res.getDimension(R.dimen.list_logo_y_translation));

    mAppBarLayout.animate()
        .translationY(0)
        .setStartDelay(300)
        .setDuration(600)
        .setInterpolator(new DecelerateInterpolator());
    mLogo.animate()
        .translationY(0)
        .alpha(1)
        .setStartDelay(900)
        .setDuration(500)
        .setInterpolator(new DecelerateInterpolator());

  }

  @Override
  public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newAllArticlesInstance(this);
  }

  @Override
  public void onLoadFinished(android.support.v4.content.Loader loader, Cursor cursor) {

    Adapter adapter = new Adapter(cursor);
    adapter.setHasStableIds(true);
    mRecyclerView.setAdapter(adapter);
    int columnCount = getResources().getInteger(R.integer.list_column_count);
    StaggeredGridLayoutManager sglm =
        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(sglm);
  }

  @Override
  public void onLoaderReset(android.support.v4.content.Loader loader) {
    mRecyclerView.setAdapter(null);
  }


  private class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private Cursor mCursor;

    public Adapter(Cursor cursor) {
      mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
      mCursor.moveToPosition(position);
      return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View             view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
      final ViewHolder vh   = new ViewHolder(view);
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          startActivity(new Intent(Intent.ACTION_VIEW,
              ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
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
          ImageLoaderHelper.getInstance(ArticleListActivity.this).getImageLoader());
      holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
    }

    @Override
    public int getItemCount() {
      return mCursor.getCount();
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public DynamicHeightNetworkImageView thumbnailView;
    public TextView                      titleView;
    public TextView                      subtitleView;

    public ViewHolder(View view) {
      super(view);
      thumbnailView = (DynamicHeightNetworkImageView) view.findViewById(R.id.thumbnail);
      titleView = (TextView) view.findViewById(R.id.article_title);
      subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
    }
  }
}
