package com.held.customview;


import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

public class SlideInUpAnimator extends BaseItemAnimator {

//    @Override protected void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
//        ViewCompat.setPivotX(holder.itemView, 0);
//    }
//    @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
//        ViewCompat.animate(holder.itemView)
//                .scaleX(0)
//                .scaleY(0)
//                .setDuration(getRemoveDuration())
//                .setListener(new DefaultRemoveVpaListener(holder))
//                .start();
//    }
//    @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
//        ViewCompat.setPivotX(holder.itemView, 0);
//        ViewCompat.setScaleX(holder.itemView, 0);
//        ViewCompat.setScaleY(holder.itemView, 0);
//    }
//    @Override protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
//        ViewCompat.animate(holder.itemView)
//                .scaleX(1)
//                .scaleY(1)
//                .setDuration(getAddDuration())
//                .setListener(new DefaultAddVpaListener(holder))
//                .start();
//    }

    @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationY(holder.itemView.getHeight())
                .alpha(0)
                .setDuration(getRemoveDuration())
                .setListener(new DefaultRemoveVpaListener(holder))
                .start();
    }

    @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationY(holder.itemView, holder.itemView.getHeight());
        ViewCompat.setAlpha(holder.itemView, 0);
    }

    @Override protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .translationY(0)
                .alpha(1)
                .setDuration(getAddDuration())
                .setListener(new DefaultAddVpaListener(holder))
                .start();
    }
}