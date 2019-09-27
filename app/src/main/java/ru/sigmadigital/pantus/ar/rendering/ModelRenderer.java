

package ru.sigmadigital.pantus.ar.rendering;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.easyar.Matrix44F;
import cn.easyar.Vec2F;
import ru.sigmadigital.pantus.R;

//class ModelRenderer implements GLSurfaceView.Renderer {
public class ModelRenderer{

    public ModelRenderer(Context context, RenderModel model) {
    	mContext = context;
        mModel = model;
    }

    public ModelRenderer(Context context) {
        mContext = context;
    }

    public void setModel(RenderModel model){
        this.mModel = model;
    }
    
    public void onDrawFrame(GL10 glUnused, Matrix44F projectionMatrix, Matrix44F cameraview, Vec2F size) {

//        GLES20.glViewport(0, 0, 1080,2043);
//        float ratio = (float) 1080 / 2043;
//        Matrix.frustumM(mmatProjection, 0, -ratio, ratio, -1, 1, 1, 10);
//

        useShader(0);


        GLES20.glVertexAttribPointer(mrm_VertexHandle, 3, GLES20.GL_FLOAT, false,
        							 0, mModel.mVerticesBuffer);
        checkGlError("glVertexAttribPointer mrm_VertexHandle");
        GLES20.glEnableVertexAttribArray(mrm_VertexHandle);
        checkGlError("glEnableVertexAttribArray mrm_VertexHandle");
        GLES20.glVertexAttribPointer(mrm_NormalHandle, 3, GLES20.GL_FLOAT, false,
				 				     0, mModel.mNormalsBuffer);
        checkGlError("glVertexAttribPointer mrm_NormalHandle");
        GLES20.glEnableVertexAttribArray(mrm_NormalHandle);
        checkGlError("glEnableVertexAttribArray mrm_NormalHandle");
        GLES20.glVertexAttribPointer(mrm_TexCoord0Handle, 2, GLES20.GL_FLOAT, false,
				 					 0, mModel.mTexCoordsBuffer);
		checkGlError("glVertexAttribPointer mrm_TexCoord0Handle");
		GLES20.glEnableVertexAttribArray(mrm_TexCoord0Handle);
		checkGlError("glEnableVertexAttribArray mrm_TexCoord0Handle");


		GLES20.glUniform3f(mfvLightPositionHandle,
						   mfvLightPosition[0], mfvLightPosition[1], mfvLightPosition[2]);
		checkGlError("glUniform3f mfvLightPositionHandle");
		GLES20.glUniform3f(mfvEyePositionHandle,
				   mfvEyePosition[0], mfvEyePosition[1], mfvEyePosition[2]);
		checkGlError("glUniform3f mfvEyePositionHandle");


        Matrix.setRotateM(mmatModel, 0, -mAngleY, 1.0f, 0, 0);
        Matrix.rotateM(mmatModel, 0, mAngleX, 0, 1.0f, 0);
        Matrix.scaleM(mmatModel, 0, mScale/3, mScale/3, mScale/3);

        Matrix.multiplyMM(mmatModelView, 0, cameraview.data, 0, mmatModel, 0);
        Matrix.multiplyMM(mmatViewProjection, 0, projectionMatrix.data, 0, mmatModelView, 0);  // XXX - TODO - this is a lie, need to rename these
        Matrix.invertM(mmatViewProjectionInverse, 0, mmatViewProjection, 0);
        Matrix.transposeM(mmatViewProjectionInverseTranspose, 0, mmatViewProjectionInverse, 0);


        GLES20.glUniformMatrix4fv(mmatViewProjectionHandle, 1, false, mmatViewProjection, 0);
        checkGlError("glUniformMatrix4fv mmatViewProjectionHandle");
        GLES20.glUniformMatrix4fv(mmatViewProjectionInverseTransposeHandle, 1, false, mmatViewProjectionInverseTranspose, 0);
        checkGlError("glUniformMatrix4fv mmatViewProjectionInverseTransposeHandle");






        for (int i=0; i<mModel.mMaterials.length; i++) {
        	RenderMaterial material = mModel.mMaterials[i];


        	if (material.textureID != -1) {
                GLES20.glEnableVertexAttribArray(mrm_TexCoord0Handle);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, material.textureID);
        	}


        	GLES20.glUniform4f(mfvAmbientHandle,
        			material.matrix[0], material.matrix[1], material.matrix[2],  material.matrix[3]);
        	checkGlError("glUniform4f mfvAmbientHandle");
        	GLES20.glUniform4f(mfvDiffuseHandle,
        			material.matrix[4], material.matrix[5], material.matrix[6],  material.matrix[7]);
        	checkGlError("glUniform4f mfvDiffuseHandle");
        	GLES20.glUniform4f(mfvSpecularHandle,
        			material.matrix[8], material.matrix[9], material.matrix[10],  material.matrix[11]);
        	checkGlError("glUniform4f mfvSpecularHandle");
        	GLES20.glUniform1f(mfSpecularPowerHandle, material.matrix[12]);
        	checkGlError("glUniform1f mfSpecularPowerHandle");

        	//отрисовка стенок с материалом
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, material.vertexIndexStart, material.vertexIndexCount);
            checkGlError("glDrawArrays");
        }
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mmatProjection, 0, -ratio, ratio, -1, 1, 1, 10);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        mProgram = createProgram(R.raw.colormap_vert, R.raw.colormap_frag);
        if (mProgram == 0) {
            throw new RuntimeException("Error compiling the shader programs");
        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        checkGlError("glClearColor");
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        checkGlError("GL_CULL_FACE");
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        checkGlError("GL_DEPTH_TEST");

        mModel.loadTextures();
        mScale = (-mfvEyePosition[2] - 1.0f) * 1 / mModel.mLongestAxisLength;

        Matrix.setLookAtM(mmatView, 0, mfvEyePosition[0], mfvEyePosition[1], mfvEyePosition[2], 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    public void setAmbientLight(float intensity) {
    }

    private void useShader(int shader) {

        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        mrm_VertexHandle = GLES20.glGetAttribLocation(mProgram, "rm_Vertex");
        checkGlError("glGetAttribLocation rm_Vertex");
        if (mrm_VertexHandle == -1) {
            throw new RuntimeException("Could not get attrib location for rm_Vertex");
        }
        mrm_NormalHandle = GLES20.glGetAttribLocation(mProgram, "rm_Normal");
        checkGlError("glGetAttribLocation rm_Normal");
        if (mrm_NormalHandle == -1) {
            throw new RuntimeException("Could not get attrib location for rm_Normal");
        }
        mrm_TexCoord0Handle = GLES20.glGetAttribLocation(mProgram, "rm_TexCoord0");
        checkGlError("glGetAttribLocation rm_TexCoord0");
        if (mrm_TexCoord0Handle == -1) {
            throw new RuntimeException("Could not get attrib location for rm_TexCoord0");
        }

        mfvLightPositionHandle = GLES20.glGetUniformLocation(mProgram, "fvLightPosition");
        checkGlError("glGetUniformLocation fvLightPosition");
        if (mfvLightPositionHandle == -1) {
            throw new RuntimeException("Could not get uniform location for fvLightPosition");
        }
        mfvEyePositionHandle = GLES20.glGetUniformLocation(mProgram, "fvEyePosition");
        checkGlError("glGetUniformLocation fvEyePosition");
        if (mfvEyePositionHandle == -1) {
            throw new RuntimeException("Could not get uniform location for fvEyePosition");
        }

        mmatViewProjectionHandle = GLES20.glGetUniformLocation(mProgram, "matViewProjection");
        checkGlError("glGetUniformLocation matViewProjection");
        if (mmatViewProjectionHandle == -1) {
            throw new RuntimeException("Could not get uniform location for matViewProjection");
        }
        mmatViewProjectionInverseTransposeHandle = GLES20.glGetUniformLocation(mProgram, "matViewProjectionInverseTranspose");
        checkGlError("glGetUniformLocation matViewProjectionInverseTranspose");
        if (mmatViewProjectionInverseTransposeHandle == -1) {
            throw new RuntimeException("Could not get uniform location for matViewProjectionInverseTranspose");
        }

        mfvAmbientHandle = GLES20.glGetUniformLocation(mProgram, "fvAmbient");
        checkGlError("glGetUniformLocation fvAmbient");
        if (mfvAmbientHandle == -1) {
            throw new RuntimeException("Could not get uniform location for fvAmbient");
        }
        mfvDiffuseHandle = GLES20.glGetUniformLocation(mProgram, "fvDiffuse");
        checkGlError("glGetUniformLocation fvDiffuse");
        if (mfvDiffuseHandle == -1) {
            throw new RuntimeException("Could not get uniform location for fvDiffuse");
        }
        mfvSpecularHandle = GLES20.glGetUniformLocation(mProgram, "fvSpecular");
        checkGlError("glGetUniformLocation fvSpecular");
        if (mfvSpecularHandle == -1) {
            throw new RuntimeException("Could not get uniform location for fvSpecular");
        }
        mfSpecularPowerHandle = GLES20.glGetUniformLocation(mProgram, "fSpecularPower");
        checkGlError("glGetUniformLocation fSpecularPower");
        if (mfSpecularPowerHandle == -1) {
            throw new RuntimeException("Could not get uniform location for fSpecularPower");
        }
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader "+shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            checkGlError("glShaderSource "+source);
            GLES20.glCompileShader(shader);
            checkGlError("glCompileShader "+shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            checkGlError("glGetShaderiv "+shader);
            if (compiled[0] == 0) {
            	String err = GLES20.glGetShaderInfoLog(shader);
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, err);
                GLES20.glDeleteShader(shader);
                checkGlError("glDeleteShader "+shader);
                shader = 0;
            }
        }

        return shader;
    }

    private String readShader(int resId) {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(resId)));
    	String result = "";
    	String line;
    	try {
			while ((line = reader.readLine()) != null)
				result += line+"\n";
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return result;
    }
    private int createProgram(int vertexResId, int fragResId) {
    	mVertexShader = readShader(vertexResId);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShader);
        if (vertexShader == 0) {
            return 0;
        }

        mFragmentShader = readShader(fragResId);
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShader);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }


    private String mVertexShader = null;
    private String mFragmentShader = null;

    private float[] mmatModel = new float[16];
    private float[] mmatView = new float[16];
    private float[] mmatProjection = new float[16];
    private float[] mmatModelView = new float[16];
    private float[] mmatViewProjection = new float[16];
    private float[] mmatViewProjectionInverse = new float[16];
    private float[] mmatViewProjectionInverseTranspose = new float[16];
    private float[] mfvLightPosition = {0,0,1};
    private float[] mfvEyePosition = {0,0,-5};

    private int mProgram;

    private int mrm_VertexHandle;
    private int mrm_NormalHandle;
    private int mrm_TexCoord0Handle;
    private int mmatViewProjectionHandle;
    private int mmatViewProjectionInverseTransposeHandle;
    private int mfvLightPositionHandle;
    private int mfvEyePositionHandle;
  	private int mfvAmbientHandle;
  	private int mfvDiffuseHandle;
  	private int mfvSpecularHandle;
  	private int mfSpecularPowerHandle;

    private Context mContext;
    private RenderModel mModel;
    private static String TAG = "ModelRenderer";
    
    private float mScale = 1.0f;
    public float mAngleX = 0;
    public float mAngleY = 0;
}
