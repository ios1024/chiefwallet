package me.spark.mvvm.ui.dialog.anim;

import android.animation.ObjectAnimator;
import android.view.View;

import me.spark.mvvm.ui.dialog.base.BaseAnimatorSet;


public class BounceExit extends BaseAnimatorSet {

    public BounceExit() {
        duration = 300;
    }

    @Override
    public void setAnimation(View view) {

        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "alpha", 1, 1, 1, 0), //
                ObjectAnimator.ofFloat(view, "scaleX", 1, 0.95f, 1.05f, 0.5f),//
                ObjectAnimator.ofFloat(view, "scaleY", 1, 0.95f, 1.05f, 0.5f));
        /**
         * <pre>
         * 另一种弹性实现:依据sweet-alert-dialog布局文件实现
         * ObjectAnimator oa_alpha = ObjectAnimator.ofFloat(view, "alpha", 0.2f, 1).setDuration(90);
         *
         * AnimatorSet as1 = new AnimatorSet();
         * as1.playTogether(oa_alpha, ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1.05f).setDuration(135),//
         * 		ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1.05f).setDuration(135));
         *
         * AnimatorSet as2 = new AnimatorSet();
         * as2.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 0.95f).setDuration(105), //
         * 		ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 0.95f).setDuration(105));
         *
         * AnimatorSet as3 = new AnimatorSet();
         * as3.playTogether(ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f).setDuration(60),//
         * 		ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f).setDuration(60));
         *
         * animatorSet.playSequentially(as1, as2, as3);
         * </pre>
         *
         */
    }
}
