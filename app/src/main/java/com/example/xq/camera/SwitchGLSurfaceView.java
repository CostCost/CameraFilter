package com.example.xq.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by xq on 16/9/26.
 */

public class SwitchGLSurfaceView extends GLSurfaceView{

    public static final String TAG = "TAG";
    public Switch switchFilter;
    float start_x = 0;
    float end_x = 0;

    public SwitchGLSurfaceView(Context context) {
        super(context);
    }

    public SwitchGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        int height_mode = MeasureSpec.getMode(heightMeasureSpec);
//        if(height_mode==MeasureSpec.AT_MOST){
//            height = width;
//        }
//        setMeasuredDimension(width, height);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                 start_x = event.getRawX();
                 end_x = start_x;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                 end_x = event.getRawX();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        if(end_x-start_x>0&&end_x-start_x>150){//切换滤镜
            this.getSwitchFilter().left();
        }
        if(start_x-end_x>0&&start_x-end_x>150){
            this.getSwitchFilter().right();
        }
        return true;
    }


    public interface Switch{
        void left();
        void right();
    }

    public Switch getSwitchFilter() {
        return switchFilter;
    }

    public void setSwitchFilter(Switch switchFilter) {
        this.switchFilter = switchFilter;
    }
}
