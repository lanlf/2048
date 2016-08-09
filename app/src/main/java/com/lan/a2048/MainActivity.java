package com.lan.a2048;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private int score = 0;
    private static MainActivity mainActivity;
    private Button bt;
    private TextView tv;
    private GameView gv;
    private AnimLayer animLayer = null;
    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        mainActivity = this;
        tv = (TextView) findViewById(R.id.tv_score);
        bt = (Button) findViewById(R.id.bt_restart);
        gv = (GameView) findViewById(R.id.gv_game);
        animLayer = (AnimLayer) findViewById(R.id.animLayer);
        bt.setOnClickListener(this);
        tv.setText("0");
    }

    /**
     *计算分数
     *  @param num
     */
    public void addScore(int num) {
        score += num;
        tv.setText(score +"");
    }

    /**
     *得到动画实例
     * 使gameview可以拿到动画实例，根据动作设置相应动画
     *  @return
     */
    public AnimLayer getAnimLayer() {
        return animLayer;
    }

    /**
     * 清零
     */
    public void clearScore() {
        tv.setText("0");
        setScore(0);
    }

    /**
     *重来按钮
     *  @param view
     */
    @Override
    public void onClick(View view) {
        gv.startGame();
        score = 0;
        tv.setText("0");
    }
}
