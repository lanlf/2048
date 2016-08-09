package com.lan.a2048;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 *动画
 *  Created by lan on 2016/8/9.
 */
public class AnimLayer extends FrameLayout{
    private List<CardView> cards = new ArrayList<CardView>();

    public AnimLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AnimLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimLayer(Context context) {
        super(context);
    }


    /**
     *移动卡片的动画
     * @param from
     * @param to
     * @param fromX
     * @param toX
     * @param fromY
     * @param toY
     */
    public void createMoveAnim(final CardView from,final CardView to,int fromX,int toX,int fromY,int toY){
        //生成一个卡片，专门用来完成动画效果
        final CardView c = getCard(from.getNum());

        LayoutParams lp = new LayoutParams(GameView.CARD_WIDTH, GameView.CARD_WIDTH);
        //计算需要移动的卡片的当前位置
        lp.leftMargin = fromX*GameView.CARD_WIDTH;
        lp.topMargin = fromY*GameView.CARD_WIDTH;
        c.setLayoutParams(lp);
        //如果目的卡片位置的数字是空，就让其消失
        if (to.getNum()<=0) {
            to.getTv().setVisibility(View.INVISIBLE);
        }
        //坐标差了一点也没有关系，动画完成后c会消失
        TranslateAnimation ta = new TranslateAnimation(0, GameView.CARD_WIDTH*(toX-fromX), 0, GameView.CARD_WIDTH*(toY-fromY));
        ta.setDuration(100);
        ta.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            /**
             *  @param animation
             *  动画结束后，让c消失，相当于在GameView上加了一个framelayout
             * c完成动画后消失，FrameLayout底下的GameView又显示出来
             * GameView没有动，在它上面加了一个布局专门完成动画
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                to.getTv().setVisibility(View.VISIBLE);
                recycleCard(c);
            }
        });
        c.startAnimation(ta);
    }

    /**
     * @param num
     * @return
     * 拿到卡片对象，复用list，使其不会一直增加
     */
    private CardView getCard(int num){
        CardView c;
        if (cards.size()>0) {
            c = cards.remove(0);
        }else{
            c = new CardView(getContext());
            addView(c);
        }
        c.setVisibility(View.VISIBLE);
        c.setNum(num);
        return c;
    }
    private void recycleCard(CardView c){
        c.setVisibility(View.INVISIBLE);
        c.setAnimation(null);
        cards.add(c);
    }

    /**
     *生成卡片的动画
     * 缩放动画
     *  @param target
     */
    public void createScaleTo1(CardView target){
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        target.setAnimation(null);
        target.getTv().startAnimation(sa);
    }

}
