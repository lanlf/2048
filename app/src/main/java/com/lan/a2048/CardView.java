package com.lan.a2048;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 每一个卡片对象
 * Created by lan on 2016/8/9.
 */
public class CardView extends FrameLayout {
    /**
     * 卡片上的数字
     */
    private int num = 0;

    /**
     * 承载数字的控件
     */
    private TextView tv;

    public CardView(Context context) {
        super(context);
        initView();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv = new TextView(getContext());
        tv.setText(num + "");
        tv.setTextSize(30);
        tv.setTextColor(getResources().getColor(R.color.btNormalColor));
        tv.setGravity(Gravity.CENTER);
        tv.setBackground(getResources().getDrawable(R.drawable.card_bg));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10,10,0,0);
        setColor();
        addView(tv, layoutParams);
    }


    /**
     * 根据卡片数字大小改变颜色
     */
    public void setColor(){
        if(getNum() < 9){
            tv.setBackgroundColor(getResources().getColor(R.color.lColor));
        }else if(getNum() > 9 && getNum() < 63){
            tv.setBackgroundColor(getResources().getColor(R.color.mColor));
        }else if(getNum() > 63){
            tv.setBackgroundColor(getResources().getColor(R.color.hColor));
        }
    }

    public int getNum() {
        return num;
    }

    /**
     *设置卡片上的数字
     * 如果是小于0，textview就设置为空
     *  @param num
     */
    public void setNum(int num) {
        this.num = num;
        if (num <= 0) {
            tv.setText("");
        } else {
            tv.setText(num+"");
        }
        setColor();
    }

    protected CardView clone(){
        CardView c= new CardView(getContext());
        c.setNum(getNum());
        return c;
    }

    public TextView getTv() {
        return tv;
    }

    /**
     *重写equals方法，判断两个卡片是否一致
     *  @param o
     * @return
     */
    public boolean equals(CardView o) {
        return getNum() == o.getNum();
    }
}
