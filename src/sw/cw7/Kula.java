/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw7;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * @author Szymon
 */
public class Kula {

    public static IntBuffer bufferFromArray(int[] array) {
        IntBuffer buf = BufferUtils.createIntBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.rewind();
        return buf;
    }

    public static FloatBuffer bufferFromArray(float[] array) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.rewind();
        return buf;
    }

    private float[][] startVerts = {
            normalize(new float[]{1, 1, 1}),
            normalize(new float[]{-1, -1, 1}),
            normalize(new float[]{-1, 1, -1}),
            normalize(new float[]{1, -1, -1})
    };

    private float[][][] startTriangles = {
            {startVerts[0], startVerts[1], startVerts[2]},
            {startVerts[0], startVerts[1], startVerts[3]},
            {startVerts[0], startVerts[3], startVerts[2]},
            {startVerts[3], startVerts[1], startVerts[2]}
    };
    public static final float R = (float) Math.sqrt(2.0);


    protected static final float[] between(float[] p1, float[] p2) {
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = (p1[i] + p2[i]) / 2f;
        }
        return result;
    }

    protected static final float length(float[] v) {
        float sum = 0;
        for (float x : v) {
            sum += x * x;
        }
        return (float) Math.sqrt(sum);
    }

    protected static final float[] normalize(float[] p) {
        float sum = length(p);
        for (int i = 0; i < p.length; i++) {
            p[i] /= sum;
        }
        return p;
    }

    protected static final float[] enlarge(float[] normVector, float factor) {
        for (int i = 0; i < normVector.length; i++) {
            normVector[i] *= factor;
        }
        return normVector;
    }

    protected static final float[][][] divideTriangle(float[][] vertices) {
        float[][] middles = new float[3][];
        for (int i = 0; i < 3; i++) {
            middles[i] = between(vertices[i], vertices[(i + 1) % 3]);
            normalize(middles[i]);
            enlarge(middles[i], R);
        }
        float[][][] result = new float[4][3][];
        for (int i = 0; i < 3; i++) {
            result[i][0] = vertices[i];
            result[i][1] = middles[i];
            result[i][2] = middles[(i + 2) % 3];
        }
        System.arraycopy(middles, 0, result[3], 0, 3);
        return result;
    }

    static final float minLength = R / 4;

    private void divideRecur(float[][] triangle, int recur) {
        float[] vec = new float[3];
        for (int i = 0; i < 3; i++) {
            vec[i] = triangle[0][i] - triangle[2][i];
        }
        if (recur <= 0) {
            //float[] norm = new float[0];

            float[] norm = normalize(between(new float[]{0, 0, 0}, between(triangle[0], between(triangle[1], triangle[2]))));

            for (float[] vert : triangle) {
                if (nice) norm = normalize(vert.clone());
                normsAndVerts.put(norm);
                normsAndVerts.put(vert);
            }
            return;
        }

        for (float[][] sub : divideTriangle(triangle)) {
            divideRecur(sub, recur - 1);
        }
    }

    private int count;
    private boolean nice;
    private FloatBuffer normsAndVerts;
    IntBuffer bufferNames = BufferUtils.createIntBuffer(1);

    public Kula(int deepness, boolean nice) {
        glGenBuffers(bufferNames);
        create(bufferNames.get(0), deepness, nice);
    }

    public Kula(int buffer, int deepness, boolean nice) {
        create(buffer, deepness, nice);
    }

    private void create(int buffer, int deepness, boolean nice) {
        this.nice = nice;
        count = (int) Math.pow(startTriangles.length, deepness + 2);
        normsAndVerts = BufferUtils.createFloatBuffer(count * 6);
        normsAndVerts.rewind();
        for (float[][] triangle : startTriangles) {
            divideRecur(triangle, deepness);
        }
        normsAndVerts.flip().rewind();
        count = normsAndVerts.remaining() / 6;
        bufferNames.rewind();
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, normsAndVerts, GL_STATIC_DRAW);
        glInterleavedArrays(GL_N3F_V3F, 0, 0);
        normsAndVerts.clear();
        normsAndVerts = null;
    }

    public void draw() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferNames.get(0));
        glInterleavedArrays(GL_N3F_V3F, 0, 0);
        glDrawArrays(GL_TRIANGLES, 0, count);
    }

    public int getCount() {
        return count;
    }

    public int getMode() {
        return GL_TRIANGLES;
    }
}
