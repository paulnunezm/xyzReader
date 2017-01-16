package com.example.xyzreader.ui.articleList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulnunez on 10/2/16.
 */

public class ArticleItemAnimator extends DefaultItemAnimator {
  private static final String TAG = "ArticleItemAnimator";

  List<ArticlesAdapter.ViewHolder> mViewHolders = new ArrayList<ArticlesAdapter.ViewHolder>();

  private int lastAddAnimatedItem = 0;

  public ArticleItemAnimator(){
    Log.d(TAG, "ArticleItemAnimator: constructor");
  }



/**only used for content changing inside the viewholder**/
//  @Override
//  public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
//    return true;
//  }

  @Override
  public void onAddStarting(RecyclerView.ViewHolder item) {
    Log.d(TAG, "onAddStarting: ");
    super.onAddStarting(item);
  }

  @Override
  public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
//    Log.d(TAG, "animateAppearance: \nlastAnimItem:"+lastAddAnimatedItem+"\nlayoutPos:"+viewHolder.getLayoutPosition());
//    if (viewHolder.getLayoutPosition() >= lastAddAnimatedItem) {
//      lastAddAnimatedItem++;

//    viewHolder.itemView.setAlpha(0);
      runEnterAnimation((ArticlesAdapter.ViewHolder) viewHolder);
//      return true;
//    }
    return true;
  }

  @Override
  public boolean animateAdd(RecyclerView.ViewHolder holder) {
    Log.d(TAG, "animateAdd: ");
//    if (holder.getLayoutPosition() > lastAddAnimatedItem) {
      lastAddAnimatedItem++;
      runEnterAnimation((ArticlesAdapter.ViewHolder) holder);
//      return false;
//    }

    return false;
  }

  @Override
  public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
//    return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    Log.d(TAG, "animateChange: ");
    return true;
  }

  @Override
  public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
    Log.d(TAG, "animateChange: ");
    return true;
  }


  private void runEnterAnimation(final ArticlesAdapter.ViewHolder holder) {
    final int screenHeight = getScreenHeight(holder.itemView.getContext());
    holder.itemView.setTranslationY(screenHeight*2);
//    holder.itemView.setAlpha(0);
    holder.itemView.animate()
        .translationY(0)
        .setInterpolator(new DecelerateInterpolator(1f))
        .setStartDelay(26*holder.getLayoutPosition())
        .setDuration(700)
        .alpha(1)
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            dispatchAddFinished(holder);
          }
        })
        .start();
  }


  /**
   * grabbed from
   * {@url}https://github.com/frogermcs/InstaMaterial/blob/master/app/src/main/java/io/github/froger/instamaterial/Utils.java
   */
  public static int getScreenHeight(Context c) {
    int screenWidth = 0;
    int screenHeight = 0;

    if (screenHeight == 0) {
      WindowManager wm      = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
      Display       display = wm.getDefaultDisplay();
      Point         size    = new Point();
      display.getSize(size);
      screenHeight = size.y;
    }

    Log.i(TAG, "getScreenHeight: "+screenHeight);
    return screenHeight;
  }

}
