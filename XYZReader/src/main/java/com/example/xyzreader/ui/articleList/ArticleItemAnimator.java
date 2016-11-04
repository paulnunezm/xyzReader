package com.example.xyzreader.ui.articleList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulnunez on 10/2/16.
 */

public class ArticleItemAnimator extends DefaultItemAnimator {
  private static final String TAG = "ArticleItemAnimator";

  List<ArticlesAdapter.ViewHolder> mViewHolders = new ArrayList<ArticlesAdapter.ViewHolder>();

  private int lastAddAnimatedItem = -2;
  
  public ArticleItemAnimator(){
    Log.d(TAG, "ArticleItemAnimator: constructor");
  }

  @Override
  public void onAddStarting(RecyclerView.ViewHolder item) {
    Log.d(TAG, "onAddStarting: ");
    super.onAddStarting(item);
  }

  @Override
  public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
    Log.d(TAG, "animateAppearance: ");
    return true;
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

  @Override
  public void runPendingAnimations() {
    Log.d(TAG, "runPendingAnimations: ");
//    super.runPendingAnimations();
//    if (holder.getLayoutPosition() > lastAddAnimatedItem) {
//      Log.d(TAG, "animateAdd: "+holder.getLayoutPosition());
//      lastAddAnimatedItem++;
//      runEnterAnimation((ArticlesAdapter.ViewHolder) holder);
//      return false;
//    }
//
//    // Called when we finish animating the holder
//    dispatchAddFinished(holder);
    if (!mViewHolders.isEmpty()) {
      int         animationDuration = 300;
      AnimatorSet animator;
      View        target;
      for (final ArticlesAdapter.ViewHolder viewHolder : mViewHolders) {
        target = viewHolder.itemView;
        target.setPivotX(target.getMeasuredWidth() / 2);
        target.setPivotY(target.getMeasuredHeight() / 2);

        animator = new AnimatorSet();

        animator.playTogether(
            ObjectAnimator.ofFloat(target, "translationX", -target.getMeasuredWidth(), 0.0f),
            ObjectAnimator.ofFloat(target, "alpha", target.getAlpha(), 1.0f)
        );

        animator.setTarget(target);
        animator.setDuration(animationDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setStartDelay((animationDuration * viewHolder.getPosition()) / 10);
        animator.addListener(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animator) {

          }

          @Override
          public void onAnimationEnd(Animator animation) {
            mViewHolders.remove(viewHolder);
          }

          @Override
          public void onAnimationCancel(Animator animator) {

          }

          @Override
          public void onAnimationRepeat(Animator animator) {

          }
        });
        animator.start();
      }
    }
  }

  private void runEnterAnimation(final ArticlesAdapter.ViewHolder holder) {
    final int screenHeight = getScreenHeight(holder.itemView.getContext());
    holder.itemView.setTranslationY(screenHeight);
    holder.itemView.setAlpha(0);
    holder.itemView.animate()
        .translationY(0)
        .setInterpolator(new DecelerateInterpolator(3.f))
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

    return screenHeight;
  }

}
