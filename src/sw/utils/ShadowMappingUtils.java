package sw.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBDepthTexture;
import org.lwjgl.opengl.ARBShadow;
import org.lwjgl.util.vector.Matrix4f;
import sun.reflect.generics.tree.VoidDescriptor;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 15.06.11
 * Time: 10:44
 */
public class ShadowMappingUtils {
    private static Matrix4f biasMatrix = new Matrix4f();

    static {
        FloatBuffer biasBuff = Utils.bufferFromArray(new float[]{
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f
        });
        biasBuff.rewind();
        biasMatrix.load(biasBuff);
    }

    public static void setMatrices(
            int width, int height, float near, float far,
            float[] pos, float[] at, float[] up) {
        Utils.initPerspective(width, height, near, far);
        lookAt(pos, at, up);
    }

    public static void lookAt(float[] pos, float[] at, float[] up) {
        gluLookAt(pos[0], pos[1], pos[2], at[0], at[1], at[2], up[0], up[1], up[2]);
    }

    public static void saveMatrices(FloatBuffer projection, FloatBuffer modelView) {
        projection.rewind();
        modelView.rewind();
        glGetFloat(GL_PROJECTION_MATRIX, projection);
        glGetFloat(GL_MODELVIEW_MATRIX, modelView);
    }

    public static void loadMatrices(FloatBuffer projection, FloatBuffer modelView) {
        projection.rewind();
        modelView.rewind();
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projection);
        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(modelView);
    }

    public static int createShadowMapTexture(int size) {
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
                size, size, 0, GL_DEPTH_COMPONENT,
                GL_UNSIGNED_BYTE, (ByteBuffer) null);
        setShadowMapTextureParams();
        return texture;
    }

    public static void setShadowMapTextureParams() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
    }

    public static void setTextureGenerationVectors(FloatBuffer lightProjection, FloatBuffer lightModelView) {
        Matrix4f lightPro = new Matrix4f();
        lightPro.load(lightProjection);
        Matrix4f lightView = new Matrix4f();
        lightView.load(lightModelView);
        setTextureGenerationVectors(lightPro, lightView);
    }

    /**
     * Only needed if light position changes (?).
     */
    public static void setTextureGenerationVectors(Matrix4f lightProjection, Matrix4f lightModelView) {
        FloatBuffer[] textureMatrixRows = {
                BufferUtils.createFloatBuffer(4),
                BufferUtils.createFloatBuffer(4),
                BufferUtils.createFloatBuffer(4),
                BufferUtils.createFloatBuffer(4)
        };

        Matrix4f textureMatrix = new Matrix4f();
        Matrix4f.mul(Matrix4f.mul(biasMatrix, lightProjection, null), lightModelView, textureMatrix);

        FloatBuffer texMatBuff = BufferUtils.createFloatBuffer(16);
        texMatBuff.rewind();
        textureMatrix.storeTranspose(texMatBuff);
        texMatBuff.rewind();
        for (int row = 0; row < 4; ++row) {
            textureMatrixRows[row].rewind();
            for (int col = 0; col < 4; ++col) {
                textureMatrixRows[row].put(texMatBuff.get());
            }
            textureMatrixRows[row].rewind();
        }

        glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGen(GL_S, GL_EYE_PLANE, textureMatrixRows[0]);

        glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGen(GL_T, GL_EYE_PLANE, textureMatrixRows[1]);

        glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGen(GL_R, GL_EYE_PLANE, textureMatrixRows[2]);

        glTexGeni(GL_Q, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGen(GL_Q, GL_EYE_PLANE, textureMatrixRows[3]);
    }

    public static void setTextureGeneration() {
        glTexParameteri(GL_TEXTURE_2D, ARBShadow.GL_TEXTURE_COMPARE_MODE_ARB, ARBShadow.GL_COMPARE_R_TO_TEXTURE_ARB);
        glTexParameteri(GL_TEXTURE_2D, ARBShadow.GL_TEXTURE_COMPARE_FUNC_ARB, GL_LEQUAL);
        glTexParameteri(GL_TEXTURE_2D, ARBDepthTexture.GL_DEPTH_TEXTURE_MODE_ARB, GL_ALPHA);
    }
}
