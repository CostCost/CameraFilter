package com.example.xq.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import javax.microedition.khronos.opengles.GL10;
import jp.co.cyberagent.android.gpuimage.OpenGlUtils;

public class OpenGlUtil extends OpenGlUtils{
	public static final int NO_TEXTURE = -1;
	public static final int NOT_INIT = -1;	
	public static final int ON_DRAWN = 1;

	public static int loadTexture(final Buffer data, final int width,final int height, final int usedTexId) {
		if(data == null)
			return NO_TEXTURE;
	    int textures[] = new int[1];
	    if (usedTexId == NO_TEXTURE) {
	        GLES20.glGenTextures(1, textures, 0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
	                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
	    } else {
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
	        GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width,
	                height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
	        textures[0] = usedTexId;
	    }
	    return textures[0];
    }
    
	public static int loadTexture(final Buffer data, final int width,final int height, final int usedTexId,final int type) {
		if(data == null)
			return NO_TEXTURE;
	    int textures[] = new int[1];
	    if (usedTexId == NO_TEXTURE) {
	        GLES20.glGenTextures(1, textures, 0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
	                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
	                0, GLES20.GL_RGBA, type, data);
	    } else {
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
	        GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width,
	                height, GLES20.GL_RGBA, type, data);
	        textures[0] = usedTexId;
	    }
	    return textures[0];
    }
    
    public static int loadTexture(final Context context, final String name){
		final int[] textureHandle = new int[1];
		
		GLES20.glGenTextures(1, textureHandle, 0);
		
		if (textureHandle[0] != 0){

			// Read in the resource
			final Bitmap bitmap = getImageFromAssetsFile(context,name);
						
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();						
		}
		
		if (textureHandle[0] == 0){
			throw new RuntimeException("Error loading texture.");
		}
		
		return textureHandle[0];
	}
	
	private static Bitmap getImageFromAssetsFile(Context context,String fileName){  
		Bitmap image = null;  
	    AssetManager am = context.getResources().getAssets();
	    try{  
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
          	}catch (IOException e){  
	          e.printStackTrace();  
	      }  	  
	      return image;  	  
	}


	public static int getExternalOESTextureID(){		
		int[] texture = new int[1];
		GLES20.glGenTextures(1, texture, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);        
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		return texture[0];
	}
	
	public static String readShaderFromRawResource(final int resourceId){
		final InputStream inputStream = MagicParams.context.getResources().openRawResource(
				resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream);
		final BufferedReader bufferedReader = new BufferedReader(
				inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try{
			while ((nextLine = bufferedReader.readLine()) != null){
				body.append(nextLine);
				body.append('\n');
			}
		}
		catch (IOException e){
			return null;
		}
		return body.toString();
	}

	/**
	 * Checks to see if a GLES error has been raised.
	 */
	public static void checkGlError(String op) {
		int error = GLES20.glGetError();
		if (error != GLES20.GL_NO_ERROR) {
			String msg = op + ": glError 0x" + Integer.toHexString(error);
			Log.e("OpenGlUtils", msg);
			throw new RuntimeException(msg);
		}
	}
}
