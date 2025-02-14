package com.zhiziyun.mywuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class WuziqiPanel extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE=10;
    private int MAX_COUNT_IN_LINE=5;

    private Paint mPaint=new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratioPieceOfLineHeight=3*1.0f/4;

    //白棋先手；轮到白棋
    private boolean mIsWhite=false;
    private ArrayList<Point> mWhiteArray=new ArrayList<>();
    private ArrayList<Point> mBlackArray=new ArrayList<>();

    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;


    public WuziqiPanel(Context context) {
        this(context,null);
    }

    public WuziqiPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece= BitmapFactory.decodeResource(getResources(),R.drawable.stone_white);
        mBlackPiece=BitmapFactory.decodeResource(getResources(),R.drawable.stone_black);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);

        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getSize(heightMeasureSpec);

        int width=Math.min(widthSize,heightSize);

        if(widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if(heightSize==MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=mPanelWidth*1.0f/MAX_LINE;

        int pieceWidth= (int) (mLineHeight*ratioPieceOfLineHeight);
        mWhitePiece=Bitmap.createScaledBitmap(mWhitePiece,pieceWidth,pieceWidth,false);
        mBlackPiece=Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin=checkFiveLine(mWhiteArray);
        boolean blackWin=checkFiveLine(mBlackArray);

        if(whiteWin || blackWin){
            mIsGameOver=true;
            mIsWhiteWinner=whiteWin;
            String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveLine(List<Point> points) {
        for(Point p:points){
            int x=p.x;
            int y=p.y;
            if(checkHorizhontal(x,y,points)
                || checkVertical(x,y,points)
                || checkLeftDiagonal(x,y,points)
                || checkRightDiagonal(x,y,points)){
                return true;
            }
        }
        return false;
    }
    private boolean checkHorizhontal(int x,int y,List<Point>points){
        int count=1;
        //Left
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point((x-i),y))){
                count++;
            }else {
                break;
            }
        }
        //Right
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point((x+i),y))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE)
            return true;
        return false;
    }

    private boolean checkVertical(int x,int y,List<Point>points){
        int count=1;
        //top
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }
        //bottom
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE)
            return true;
        return false;
    }
    private boolean checkLeftDiagonal(int x,int y,List<Point>points){
        int count=1;
        //left&top
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        //bottom
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE)
            return true;
        return false;
    }
    private boolean checkRightDiagonal(int x,int y,List<Point>points){
        int count=1;
        //right&top
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }
        //left&bottom
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE)
            return true;
        return false;
    }

    private void drawPieces(Canvas canvas) {
        Log.d("msgd",mWhiteArray.size()+"");
        for(int i = 0,n=mWhiteArray.size() ;i < n;i++){
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    null);
        }
        for(int i = 0,n=mBlackArray.size() ;i < n;i++){
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int w=mPanelWidth;
        float lineHeight=mLineHeight;
        for(int i=0;i<MAX_LINE;i++){
            int startx= (int) (lineHeight/2);
            int endx= (int) (w-lineHeight/2);
            int y= (int) ((0.5+i)*lineHeight);
            canvas.drawLine(startx,y,endx,y,mPaint);
            canvas.drawLine(y,startx,y,endx,mPaint);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mIsGameOver){
            return false;
        }

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                Point p=getValidPoint(x,y);
                if(mWhiteArray.contains(p) || mBlackArray.contains(p)){
                    return false;
                }
                if(mIsWhite){
                    mWhiteArray.add(p);
                }else {
                    mBlackArray.add(p);
                }
                invalidate();
                mIsWhite=!mIsWhite;
                Log.d("msgd",mIsWhite+"");
                return true;

                default:
                    return false;
        }

    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
    }

    private static final  String INSTANCE="instance";
    private static final String IS_WHITE="is_white";
    private static final String INSTANCE_GAME_OVER="instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY="instance_black_array";

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(IS_WHITE,mIsWhite);
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle=(Bundle)state;
            mIsWhite=bundle.getBoolean(IS_WHITE);
            mIsGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
