package sw.utils;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 13.04.11
 * Time: 20:19
 * To change this template use File | Settings | File Templates.
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
}
