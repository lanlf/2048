package com.lan.a2048;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lan on 2016/8/9.
 */
public class GameView extends GridLayout {
    /**
     * 设置行和列
     */
    private static final int LINE = 4;
    private static final int COLUMN = 4;

    /**
     * 子控件宽度
     */
    public static int CARD_WIDTH;
    /**
     * cardView数组用来存放所有的卡片
     */
    private CardView[][] cardMap = new CardView[LINE][COLUMN];

    /**
     * 用来存放所有空的卡片的坐标
     */
    private List<Point> emptyPoints = new ArrayList<Point>();

    public GameView(Context context) {
        super(context);
        initView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setColumnCount(4);
        /**
         *监听滑动事件
         */
        //System.out.println("AnimLayer");
        setOnTouchListener(new View.OnTouchListener() {


            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                swipeLeft();
                            } else if (offsetX > 5) {
                                swipeRight();
                            }
                        } else {
                            if (offsetY < -5) {
                                swipeUp();
                            } else if (offsetY > 5) {
                                swipeDown();
                            }
                        }

                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //System.out.println("onlayout");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        CARD_WIDTH = Math.min(w, h) / COLUMN;
        addCards(CARD_WIDTH, CARD_WIDTH);
        startGame();
        int height = Math.max(w,h);
        int offset =  (height - CARD_WIDTH *LINE+cardMap[0][0].getPaddingTop()*(LINE - 1))/2;
        setPadding(0,offset,0,offset);
        //System.out.print("onchange");
    }

    /**
     * 设置卡片的宽和高
     * 添加所有卡片
     *
     * @param cardWidth
     * @param cardHeight
     */
    private void addCards(int cardWidth, int cardHeight) {

        CardView c;

        for (int y = 0; y < LINE; y++) {
            for (int x = 0; x < COLUMN; x++) {
                c = new CardView(getContext());
                c.setNum(0);
                addView(c, cardWidth, cardHeight);
                cardMap[x][y] = c;
            }
        }
    }

    public void startGame() {

        MainActivity.getMainActivity().clearScore();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                cardMap[x][y].setNum(0);
            }
        }

        addRandomNum();
        addRandomNum();
    }

    /**
     * 添加随机位置的卡片
     */
    private void addRandomNum() {

        emptyPoints.clear();

        for (int y = 0; y < LINE; y++) {
            for (int x = 0; x < COLUMN; x++) {
                if (cardMap[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }

        Point p = emptyPoints.remove((int) (Math.random() * emptyPoints.size()));
        cardMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);

        MainActivity.getMainActivity().getAnimLayer().createScaleTo1(cardMap[p.x][p.y]);
    }

    /**
     * 向左滑动动作
     */
    private void swipeLeft() {
        //表示是否发生了变化
        boolean merge = false;

        for (int y = 0; y < LINE; y++) {
            for (int x = 0; x < COLUMN; x++) {
                /*
                *从左往右查找
                 */
                for (int x1 = x + 1; x1 < COLUMN; x1++) {
                    if (cardMap[x1][y].getNum() > 0) {

                        if (cardMap[x][y].getNum() <= 0) {
                             /*
                               *如果右边的卡片有数字，当前卡片没数字，那么右边的卡片就移动到左边
                               */
                            MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x1][y],cardMap[x][y], x1, x, y, y);
                            cardMap[x][y].setNum(cardMap[x1][y].getNum());
                            cardMap[x1][y].setNum(0);
                            /*
                             *移动后，还要再比较一次当前位置，因为当前位置上的数字有了变化
                             */
                            x--;
                            merge = true;
                        } else if (cardMap[x][y].equals(cardMap[x1][y])) {
                            /*
                               *如果右边的卡片有数字，当前卡片有数字，两者一样就合并，不一样则不变
                               */
                            MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);
                            cardMap[x][y].setNum(cardMap[x][y].getNum() * 2);
                            cardMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }
        //每次布局发生变化了都有添加一张卡片，并检查游戏是否结束
        if (merge) {
            addRandomNum();
           // checkColor();
            checkComplete();
        }
    }

    /**
     * 向右
     */
    private void swipeRight() {

        boolean merge = false;

        for (int y = 0; y < LINE; y++) {
            for (int x = 3; x >= 0; x--) {

                for (int x1 = x - 1; x1 >= 0; x1--) {
                    if (cardMap[x1][y].getNum() > 0) {

                        if (cardMap[x][y].getNum() <= 0) {
                            MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);
                            cardMap[x][y].setNum(cardMap[x1][y].getNum());
                            cardMap[x1][y].setNum(0);
                            x++;
                            merge = true;
                        } else if (cardMap[x][y].equals(cardMap[x1][y])) {
                            MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x1][y], cardMap[x][y],x1, x, y, y);
                            cardMap[x][y].setNum(cardMap[x][y].getNum() * 2);
                            cardMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            //checkColor();
            checkComplete();
        }
    }

    /**
     * 向上
     */
    private void swipeUp() {

        boolean merge = false;

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {

                for (int y1 = y + 1; y1 < 4; y1++) {
                    if (cardMap[x][y1].getNum() > 0) {

                        if (cardMap[x][y].getNum() <= 0) {
                            MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
                            cardMap[x][y].setNum(cardMap[x][y1].getNum());
                            cardMap[x][y1].setNum(0);
                            y--;
                            merge = true;
                        } else if (cardMap[x][y].equals(cardMap[x][y1])) {
                            MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
                            cardMap[x][y].setNum(cardMap[x][y].getNum() * 2);
                            cardMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMap[x][y].getNum());
                            merge = true;
                        }

                        break;

                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            //checkColor();
            checkComplete();
        }
    }

    /**
     * 向下
     */
    private void swipeDown() {

        boolean merge = false;

        for (int x = 0; x < 4; x++) {
            for (int y = 3; y >= 0; y--) {

                for (int y1 = y - 1; y1 >= 0; y1--) {
                    if (cardMap[x][y1].getNum() > 0) {

                        if (cardMap[x][y].getNum() <= 0) {
                            MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
                            cardMap[x][y].setNum(cardMap[x][y1].getNum());
                            cardMap[x][y1].setNum(0);

                            y++;
                            merge = true;
                        } else if (cardMap[x][y].equals(cardMap[x][y1])) {
                            MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardMap[x][y1],cardMap[x][y], x, x, y1, y);
                            cardMap[x][y].setNum(cardMap[x][y].getNum() * 2);
                            cardMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            //checkColor();
            checkComplete();
        }
    }

    /**
     * 根据数字大小设置不同颜色
     */
 /*   private void checkColor() {
        for (int x = 0; x < COLUMN; x++) {
            for (int y = 0; y < LINE; y++) {
                cardMap[x][y].setColor();
            }
        }
    }
*/
    /**
     * 检查游戏是否结束
     * 如果所有卡片上的数字都不为空，并且所有卡片数字与其相邻卡片的数字都不一样。那么判断游戏结束
     */
    private void checkComplete() {

        boolean complete = true;

        ALL:
        for (int y = 0; y < COLUMN; y++) {
            for (int x = 0; x < LINE; x++) {
                if (cardMap[x][y].getNum() == 0 ||
                        (x > 0 && cardMap[x][y].equals(cardMap[x - 1][y])) ||
                        (x < 3 && cardMap[x][y].equals(cardMap[x + 1][y])) ||
                        (y > 0 && cardMap[x][y].equals(cardMap[x][y - 1])) ||
                        (y < 3 && cardMap[x][y].equals(cardMap[x][y + 1]))) {

                    complete = false;
                    break ALL;
                }
            }
        }

        if (complete) {
            new AlertDialog.Builder(getContext()).setTitle("GAME OVER~").setMessage("得分："+MainActivity.getMainActivity().getScore()).setCancelable(false).setPositiveButton("重来", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startGame();
                }
            }).show();
        }
    }
}
