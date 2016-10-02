package com.example.xq.filter;

import android.opengl.GLES20;

import com.example.xq.mysolution.R;
import com.example.xq.utils.OpenGlUtil;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class MagicCrayonFilter extends GPUImageFilter {

    private int mSingleStepOffsetLocation;
	//1.0 - 5.0
	private int mStrengthLocation;
	
	public MagicCrayonFilter(){
		super(NO_FILTER_VERTEX_SHADER, OpenGlUtil.readShaderFromRawResource(R.raw.crayon));
	}

    public void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
        setFloat(mStrengthLocation, 2.0f);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onInitialized(){
        super.onInitialized();
        setFloat(mStrengthLocation, 0.5f);
    }

    private void setTexelSize(final float w, final float h) {
		setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
	}
	
	@Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
