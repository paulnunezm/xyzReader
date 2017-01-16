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
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.ui.articleDetails.ArticleDetailActivity;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
   android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
  private static final String TAG = "ArticleListActivity";

  private Toolbar            mToolbar;
  private View               mLogo;
  private AppBarLayout       mAppBarLayout;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private RecyclerView       mRecyclerView;
  private ArticlesAdapter    mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article_list);

    bindViews();
    getSupportLoaderManager().initLoader(0, null, this);

    if (savedInstanceState == null) {
//      refresh();
//      introAnimation();
    }
    introAnimation();
  }

  private void bindViews() {
    mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
    mLogo = findViewById(R.id.img_logo);
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

    // setting up the recyclerview
    mRecyclerView.setItemAnimator(new ArticleItemAnimator());
    mAdapter = new ArticlesAdapter(this);
    mAdapter.setHasStableIds(true);
    int columnCount = getResources().getInteger(R.integer.list_column_count);
    StaggeredGridLayoutManager sglm =
        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(sglm);
    mRecyclerView.setAdapter(mAdapter);

    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refresh();
      }
    });
  }


  private void refresh() {
    Log.d(TAG, "refresh: ");
    startService(new Intent(this, UpdaterService.class));
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "onStart: ");
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
      Log.d(TAG, "onReceive: ");
      if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
        mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
        updateRefreshingUI();
      }
    }
  };

  private void updateRefreshingUI() {
    Log.d(TAG, "updateRefreshingUI: ");
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
    Log.i(TAG, "onLoadFinished: ");

//    ArticlesAdapter adapter = new ArticlesAdapter(cursor, this);
//    adapter.setHasStableIds(true);
//    int columnCount = getResources().getInteger(R.integer.list_column_count);
//    StaggeredGridLayoutManager sglm =
//        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
//    mRecyclerView.setLayoutManager(sglm);
    mAdapter.setData(cursor);
    mAdapter.notifyDataSetChanged();

//    mRecyclerView.setAdapter(adapter);
//    adapter.notifyDataSetChanged();

  }

  @Override
  public void onLoaderReset(android.support.v4.content.Loader loader) {
    mRecyclerView.setAdapter(null);
  }



}
