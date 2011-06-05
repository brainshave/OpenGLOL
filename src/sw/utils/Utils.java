package sw.utils;

import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 13.04.11
 * Time: 20:19
 */
public class Utils {
    public static IntBuffer bufferFromArray(int[] array) {
        IntBuffer buf = BufferUtils.createIntBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.flip();
        return buf;
    }

    public static FloatBuffer bufferFromArray(float[] array) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.flip();
        return buf;
    }


    public static ByteBuffer bufferFromArray(byte[] array) {
        ByteBuffer buf = BufferUtils.createByteBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.flip();
        return buf;
    }

    public static float[] vector(float[] p1, float[] p2) {
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = p1[i] - p2[i];
        }
        return result;
    }

    public static float length(float[] v) {
        float sum = 0;
        for (float x : v) {
            sum += x * x;
        }
        return (float) Math.sqrt(sum);
    }

    public static float[] normalize(float[] p) {
        float sum = length(p);
        //p = p.clone();
        for (int i = 0; i < p.length; i++) {
            p[i] /= sum;
        }
        return p;
    }

    public static float[] vectorProduct(float[] a, float[] b) {
        return new float[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
    }

    public static void sleep60Hz() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static ByteBuffer imageData(BufferedImage image) {
        ByteBuffer bb = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        bb.rewind();
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int p = image.getRGB(x, y);
                for (int i = 16; i >= 0; i -= 8) {
                    bb.put((byte) ((p >> i) & 0xFF));
                }
                bb.put((byte) -1);
            }
        }
        bb.flip();
        return bb;
    }

    public static void initPerspective(GLBaza program, float near, float far) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float size = 1;
        if (program.width > program.height) {
            glFrustum(-size * (float) program.width / program.height,
                    size * (float) program.width / program.height, -size, size, near, far);
        } else {
            glFrustum(-size, size, -size * (float) program.height / program.width,
                    size * (float) program.height / program.width, near, far);
        }
        glMatrixMode(GL_MODELVIEW);
    }

    public static void enable(int[] enables) {
        for (int flag : enables) {
            glEnable(flag);
        }
    }


    public static void disable(int[] disables) {
        for (int flag : disables) {
            glDisable(flag);
        }
    }
}
