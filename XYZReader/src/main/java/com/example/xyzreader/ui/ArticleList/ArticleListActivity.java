package com.example.xyzreader.ui.articleList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.ui.ArticleDetailActivity;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
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

    mRecyclerView.setItemAnimator(new com.example.xyzreader.ui.articleList.ArticleItemAnimator());
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
        .setInterpolator(new DecelerateInterpolator(1.5f));

    mLogo.animate()
        .translationY(0)
        .alpha(1)
        .setStartDelay(900)
        .setDuration(500)
        .setInterpolator(new DecelerateInterpolator(1.5f));
  }

  @Override
  public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newAllArticlesInstance(this);
  }

  @Override
  public void onLoadFinished(android.support.v4.content.Loader loader, Cursor cursor) {

    ArticlesAdapter adapter = new ArticlesAdapter(cursor, this);
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



}
