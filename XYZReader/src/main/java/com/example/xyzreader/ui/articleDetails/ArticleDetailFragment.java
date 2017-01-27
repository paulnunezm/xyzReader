package com.example.xyzreader.ui.articleDetails;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.ui.articleList.ArticleListActivity;
import com.example.xyzreader.ui.utils.DrawInsetsFrameLayout;
import com.example.xyzreader.ui.utils.DynamicHeightNetworkImageView;
import com.example.xyzreader.ui.utils.ImageLoaderHelper;
import com.example.xyzreader.ui.utils.ObservableScrollView;

import static android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends android.support.v4.app.Fragment implements
    LoaderManager.LoaderCallbacks<Cursor>, FragmentManager.OnBackStackChangedListener {
  private static final String TAG = "ArticleDetailFragment";

  public static final  String ARG_ITEM_ID     = "item_id";
  private static final float  PARALLAX_FACTOR = 1.25f;

  private Cursor mCursor;
  private long   mItemId;
  private View   mRootView;
  private int mMutedColor = 0xFF333333;
  private String                mTitle;
  private AppBarLayout          mAppBarLayout;
  private ObservableScrollView  mScrollView;
  private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
  private ColorDrawable         mStatusBarColorDrawable;

  private int                           mTopInset;
  private View                          mPhotoContainerView;
  private DynamicHeightNetworkImageView mPhotoView;
  private int                           mScrollY;
  private boolean mIsCard = false;
  private int mStatusBarFullOpacityBottom;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public ArticleDetailFragment() {
  }

  public static ArticleDetailFragment newInstance(long itemId) {
    Bundle arguments = new Bundle();
    arguments.putLong(ARG_ITEM_ID, itemId);
    ArticleDetailFragment fragment = new ArticleDetailFragment();
    fragment.setArguments(arguments);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments().containsKey(ARG_ITEM_ID)) {
      mItemId = getArguments().getLong(ARG_ITEM_ID);
    }

    mIsCard = getResources().getBoolean(R.bool.detail_is_card);
    mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
        R.dimen.detail_card_top_margin);
    setHasOptionsMenu(true);
  }

  public ArticleDetailActivity getActivityCast() {
    return (ArticleDetailActivity) getActivity();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
    // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
    // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
    // we do this in onActivityCreated.
//    getLoaderManager().initLoader(0, null, this);
    getLoaderManager().initLoader(0, null, this);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
//    mDrawInsetsFrameLayout = (DrawInsetsFrameLayout)
//        mRootView.findViewById(R.id.draw_insets_frame_layout);
//    mDrawInsetsFrameLayout.setOnInsetsCallback(new DrawInsetsFrameLayout.OnInsetsCallback() {
//      @Override
//      public void onInsetsChanged(Rect insets) {
//        mTopInset = insets.top;
//      }
//    });

//    mScrollView = (ObservableScrollView) mRootView.findViewById(R.id.scrollview);
//    mScrollView.setCallbacks(new ObservableScrollView.Callbacks() {
//      @Override
//      public void onScrollChanged() {
//        mScrollY = mScrollView.getScrollY();
//        getActivityCast().onUpButtonFloorChanged(mItemId, ArticleDetailFragment.this);
//        mPhotoContainerView.setTranslationY((int) (mScrollY - mScrollY / PARALLAX_FACTOR));
//        updateStatusBar();
//      }
//    });

    mPhotoView = (DynamicHeightNetworkImageView) mRootView.findViewById(R.id.photo);
//    mPhotoContainerView = mRootView.findViewById(R.id.photo_container);

    mStatusBarColorDrawable = new ColorDrawable(0);

//    mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
//            .setType("text/plain")
//            .setText("Some sample text")
//            .getIntent(), getString(R.string.action_share)));
//      }
//    });

    bindViews();
    updateStatusBar();
    return mRootView;
  }

  @TargetApi(21)
  private void updateStatusBar() {
    int color = 0;
    if (mPhotoView != null && mTopInset != 0 && mScrollY > 0) {
      float f = progress(mScrollY,
          mStatusBarFullOpacityBottom - mTopInset * 3,
          mStatusBarFullOpacityBottom - mTopInset);
      color = Color.argb((int) (255 * f),
          (int) (Color.red(mMutedColor) * 0.9),
          (int) (Color.green(mMutedColor) * 0.9),
          (int) (Color.blue(mMutedColor) * 0.9));
    }
//    mStatusBarColorDrawable.setColor(color);
    //    mDrawInsetsFrameLayout.setInsetBackground(mStatusBarColorDrawable);

//   from https://developer.android.com/reference/android/view/Window.html#setStatusBarColor(int)
    Window window = getActivity().getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
    window.setStatusBarColor(color);
  }

  static float progress(float v, float min, float max) {
    return constrain((v - min) / (max - min), 0, 1);
  }

  static float constrain(float val, float min, float max) {
    if (val < min) {
      return min;
    } else if (val > max) {
      return max;
    } else {
      return val;
    }
  }

  private void bindViews() {
    if (mRootView == null) {
      return;
    }

    Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
    ((ArticleDetailActivity) getActivity()).setSupportActionBar(toolbar);

    ActionBar actionBar = ((ArticleDetailActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setHomeButtonEnabled(true);

    getFragmentManager().addOnBackStackChangedListener(this); // listen to the backstack of the fragment manager

    final CollapsingToolbarLayout collapsingToolbar =
        (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_tool_bar);
    final TextView titleView      = (TextView) mRootView.findViewById(R.id.article_title);
    final View     titleContainer = mRootView.findViewById(R.id.title_container);
    final TextView bylineView     = (TextView) mRootView.findViewById(R.id.article_byline);
//    bylineView.setMovementMethod(new LinkMovementMethod());
    TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);
    mAppBarLayout = (AppBarLayout) mRootView.findViewById(R.id.app_bar_layout);
//    bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

    if (mCursor != null) {
      mRootView.setAlpha(0);
      mRootView.setVisibility(View.VISIBLE);
      mRootView.animate().alpha(1);
      mTitle = mCursor.getString(ArticleLoader.Query.TITLE);
      titleView.setText(mTitle);
//      titleView.setPivotY((titleView.getHeight()/8);
//      collapsingToolbar.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));

      collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
      collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

      bylineView.setText(Html.fromHtml(
          DateUtils.getRelativeTimeSpanString(
              mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
              System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
              DateUtils.FORMAT_ABBREV_ALL).toString()
              + " by <font color='#ffffff'>"
              + mCursor.getString(ArticleLoader.Query.AUTHOR)
              + "</font>"));
      bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));


      mPhotoView.setImageUrl(mCursor.getString(ArticleLoader.Query.PHOTO_URL),
          ImageLoaderHelper.getInstance(getActivity()).getImageLoader());
      mPhotoView.setAspectRatio(1.5f); // Set aspect radio

      ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
          .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {

              Bitmap bitmap = imageContainer.getBitmap();
              if (bitmap != null) {
                Palette p = Palette.generate(bitmap, 12);
                mMutedColor = p.getDarkMutedColor(0xFF333333);
                int collapsingToolBarScrimColor = p.getMutedColor(getResources()
                    .getColor(R.color.theme_primary));
//                mPhotoView.setImageBitmap(imageContainer.getBitmap());  // Doesn't work!, why ?

//                mRootView.findViewById(R.id.meta_bar)
//                    .setBackgroundColor(mMutedColor);

                // Create custom scrim using the muted color.
                int              alphaColor                     = Color.argb(170, Color.red(mMutedColor), Color.green(mMutedColor), Color.blue(mMutedColor));
                int[]            colors                         = new int[]{Color.parseColor("#00000000"), alphaColor};
                GradientDrawable gd                             = new GradientDrawable(TOP_BOTTOM, colors);
                ColorDrawable    collapsingToolBarScrimDrawable = new ColorDrawable(collapsingToolBarScrimColor);
                mRootView.findViewById(R.id.meta_scrim).setBackground(gd);
                collapsingToolbar.setContentScrim(collapsingToolBarScrimDrawable);

//                updateStatusBar();
              }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
          });

      mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//          From : https://androidcot.wordpress.com/2015/12/07/android-collapsingtoolbarlayout-animation-header-fade-inout/
          float percentage = ((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());
          float value      = Math.abs(1 - percentage);

          // Anumate views
          animateAppLayoutViewsFromOffset(titleContainer, value);

          // set title when collapsed
          String text = (percentage == 1) ? mTitle : " ";
          collapsingToolbar.setTitle(text);
        }
      });

    } else {
      mRootView.setVisibility(View.GONE);
      titleView.setText("N/A");
      bylineView.setText("N/A");
      bodyView.setText("N/A");
    }

    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
     addTransitionName();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        Log.d(TAG, "onOptionsItemSelected: ");
//        NavUtils.navigateUpFromSameTask(getActivity());
        ((AppCompatActivity) getActivity()).onSupportNavigateUp();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
  }

  @Override
  public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
    if (!isAdded()) {
      if (cursor != null) {
        cursor.close();
      }
      return;
    }

    mCursor = cursor;
    if (mCursor != null && !mCursor.moveToFirst()) {
      Log.e(TAG, "Error reading item detail cursor");
      mCursor.close();
      mCursor = null;
    }

    bindViews();
     ActivityCompat.startPostponedEnterTransition(getActivity());
  }

  @Override
  public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    mCursor = null;
    bindViews();
  }


//  @Override
//  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//    if (!isAdded()) {
//      if (cursor != null) {
//        cursor.close();
//      }
//      return;
//    }
//
//    mCursor = cursor;
//    if (mCursor != null && !mCursor.moveToFirst()) {
//      Log.e(TAG, "Error reading item detail cursor");
//      mCursor.close();
//      mCursor = null;
//    }
//
//    bindViews();
//  }

//  @Override
//  public void onLoaderReset(Loader<Cursor> cursorLoader) {
//    mCursor = null;
//    bindViews();
//  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public void addTransitionName(){
    mPhotoView.setTransitionName(getActivity().getResources().getString(R.string.item_transition)+ " "+mItemId);
//    getActivity().enter
    Log.d(TAG, "addTransitionName: id -> "+mItemId);
  }

  public void animateAppLayoutViewsFromOffset(View view, float value) {
    float scale = (value <= 0.8) ? 0.8f : value;
    float alpha = (value > 0.7) ? value : (float) (value * 0.8);

    view.setScaleX(scale);
    view.setScaleY(scale);
    view.setAlpha(alpha);
  }

  public int getUpButtonFloor() {
    if (mPhotoContainerView == null || mPhotoView.getHeight() == 0) {
      return Integer.MAX_VALUE;
    }

    // account for parallax
    return mIsCard
        ? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() - mScrollY
        : mPhotoView.getHeight() - mScrollY;
  }

  @Override
  public void onBackStackChanged() {
    Log.d(TAG, "onBackStackChanged: ");
    ((AppCompatActivity) getActivity()).onSupportNavigateUp();
  }
}