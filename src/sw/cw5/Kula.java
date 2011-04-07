/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw5;

import java.util.Random;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import sw.utils.GLBaza;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 *
 * @author Szymon
 */
public class Kula extends GLBaza {

    public static void main(String[] args) {
        new Kula().start();
    }

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
    private float[] tetrahedronVerts = {
        1, 1, 1,
        -1, -1, 1,
        -1, 1, -1,
        1, -1, -1
    };
    private float[][] startVerts = {
        {1, 1, 1},
        {-1, -1, 1},
        {-1, 1, -1},
        {1, -1, -1}
    };
    private float[][][] startTriangles = {
        {startVerts[0], startVerts[1], startVerts[2]},
        {startVerts[0], startVerts[1], startVerts[3]},
        {startVerts[0], startVerts[3], startVerts[2]},
        {startVerts[3], startVerts[1], startVerts[2]}
    };
    private FloatBuffer tetrahedronVertsBuff = bufferFromArray(tetrahedronVerts);
    private IntBuffer tetrahedronIndicesBuff = bufferFromArray(new int[]{0, 1, 2, 0, 1, 3, 0, 2, 3, 1, 2, 3});
    public static final float R = (float) Math.sqrt(3.0);

    @Override
    protected void init() {
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 1.5, 20);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(-3f, -3f, -3f, 0f, 0f, 0f, 0, 1, 0);
        glEnable(GL_DEPTH_TEST);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    @Override
    protected void input() {
    }

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
    Random rand = new Random();
    static final float minLength = R/3;

    private void divideRecur(float[][] triangle) {
        float[] vec = new float[3];
        for (int i= 0; i < 3; i++) {
            vec[i] = triangle[0][i] - triangle[2][i];
        }
        if (length(vec) <= minLength) {
            //glColor3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            glBegin(GL_TRIANGLES);
            for (float[] vert : triangle) {
                //glColor3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                glVertex3f(vert[0], vert[1], vert[2]);
            }
            glEnd();
            return;
        }

        for (float[][] sub : divideTriangle(triangle)) {
            divideRecur(sub);
        }
    }

    @Override
    protected void render() {
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glRotatef(1, 1, 0, 0);

        for (float[][] triangle : startTriangles) {
            divideRecur(triangle);
        }
    }
    public void draw() {
        for (float[][] triangle : startTriangles) {
            divideRecur(triangle);
        }
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }
}
