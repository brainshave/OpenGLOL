package sw.cw8;

import org.lwjgl.BufferUtils;
import sw.utils.GLBaza;
import sw.utils.Light;
import sw.utils.Material;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static sw.utils.Utils.normalize;
import static sw.utils.Utils.vector;
import static sw.utils.Utils.vectorProduct;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 13.04.11
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class Teren extends GLBaza {
    float[][] startVerts = {{-1, 0, -1}, {1, 0, -1}, {-1, 0, 1}, {1, 0, 1}};

    void putRect(FloatBuffer fb, float[][] verts) {
        // trojkat 1
        for (int i = 2; i >= 0; --i)
            fb.put(verts[i]);
        // trojkat 2
        for (int i = 1; i < 4; ++i)
            fb.put(verts[i]);
    }

    Random rand = new Random();

    float[] between(float[] a, float[] b) {
        float[] ret = new float[3];
        for (int i = 0; i < 3; i += 2) {
            ret[i] = (a[i] + b[i]) / 2;
        }
        double x = Math.max(Math.abs(b[0] - a[0]), Math.abs(b[2] - a[2])) / 2.0;
        double Wx = W(x);
        ret[1] = (float) ((1.0 - 2.0 * Wx) * rand.nextDouble() + Wx * (a[1] + b[1]));
        return ret;
    }
//
//    float between(float[] a, float[] b) {
//        double x = Math.abs(b[0] - a[0]) / 2.0;
//        double Wx = W(x);
//        return (float) ((1.0 - 2.0 * Wx) * rand.nextDouble() + Wx * (a[1] + b[1]));
//    }

    float[] between(float[] north, float[] south, float[] west, float[] east) {
        float[] ret = between(north, south);
        double x = Math.abs(east[0] - west[0]) / 2.0;
        double Wcx = Wc(x);
        ret[1] = (float) ((1.0 - 4.0 * Wcx) * rand.nextDouble() + Wcx * (north[1] + south[1] + west[1] + east[1]));
        return ret;
    }

    float[][][] divideRect(float[][] rect) {
        float[][][] ret = new float[4][][];
        float[] north = between(rect[0], rect[1]);
        float[] south = between(rect[2], rect[3]);
        float[] west = between(rect[0], rect[2]);
        float[] east = between(rect[1], rect[3]);
        float[] centre = between(north, south, west, east);
        ret[0] = new float[][]{rect[0], north, west, centre};
        ret[1] = new float[][]{north, rect[1], centre, east};
        ret[2] = new float[][]{west, centre, rect[2], south};
        ret[3] = new float[][]{centre, east, south, rect[3]};
        return ret;
    }

    final int DENSITY = 6;
    final int SIZE = (int) (Math.pow(2, DENSITY) + 1);
    double STEP = 2.0 / (SIZE - 1);
    FloatBuffer terrain = BufferUtils.createFloatBuffer(SIZE * SIZE * 3 * 4 * 4);
    //IntBuffer indexes = BufferUtils.createIntBuffer(SIZE * SIZE * 3 * 2);
    float[][][] verts = new float[SIZE][SIZE][];

    void setupTerrain(float[][] rect, int density) {
        if (density == 0) {
            putRect(terrain, rect);
        } else {
            for (float[][] quarter : divideRect(rect)) {
                setupTerrain(quarter, density - 1);
            }
        }
    }

    void setupTerrainOld() {
        terrain.rewind();
        setupTerrain(startVerts, DENSITY);
        terrain.flip();
    }

    void setupTerrain() {
        for (int x = 0; x < SIZE; ++x) {
            for (int z = 0; z < SIZE; ++z) {
                verts[x][z] = new float[]{(float) (x * STEP - 1.0), 0, (float) (z * STEP - 1.0)};
            }
        }

        calcHeights();

        terrain.rewind();
        for (int x = 0; x < SIZE - 1; ++x) {
            for (int z = 0; z < SIZE - 1; ++z) {
                putTriangle(verts[x][z], verts[x + 1][z], verts[x][z + 1]);
                putTriangle(verts[x + 1][z], verts[x + 1][z + 1], verts[x][z + 1]);
            }
        }
        terrain.flip();
    }

    void putTriangle(float[] v1, float[] v2, float[] v3) {
        float[] a = normalize(vector(v1, v2));
        float[] b = normalize(vector(v1, v3));
        float[] norm = vectorProduct(b, a);
        terrain.put(norm);
        terrain.put(v3);
        terrain.put(norm);
        terrain.put(v2);
        terrain.put(norm);
        terrain.put(v1);
    }

    private void calcHeights() {
        for (int i = (SIZE - 1) / 2; i > 0; i /= 2) {

            for (int x = i; x < SIZE; x += 2 * i) { // zerowy rząd
                verts[x][0][1] = between(verts[x - i][0], verts[x + i][0])[1];
            }
            for (int z = i; z < SIZE; z += 2 * i) { // zerowa kolumna
                verts[0][z][1] = between(verts[0][z - i], verts[0][z + i])[1];
            }
            for (int x = i; x < SIZE; x += 2 * i) {
                for (int z = i; z < SIZE; z += 2 * i) {
                    verts[x][z + i][1] = between(verts[x - i][z + i], verts[x + i][z + i])[1];
                    verts[x + i][z][1] = between(verts[x + i][z - i], verts[x + i][z + i])[1];
                    verts[x][z][1] = between(verts[x - i][z - i], verts[x + i][z - i], verts[x - i][z + i], verts[x + i][z + i])[1];
                }
            }
        }
    }

    double W(double x) {
        return (1.0 - Math.cos(Math.pow(1.0 - x, 1.75) * Math.PI)) / 4.0;
    }

    double Wc(double x) {
        return W(x) / 2.0;
    }

    Light light = new Light(GL_LIGHT0, new float[][]{{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}, {10, 10, 0, 1}});

    Material material = new Material(100, new float[][]{{0f, 0.1f, 0, 1}, {0, 0.75f, 0, 1}, {1, 1, 1, 1}});

    @Override
    protected void init() {
//        glEnable(GL_LINE_SMOOTH);
//        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 2, 20);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(2.1f, 2.1f, 2.1f, 0, 0, 0, 0, 1, 0);

        glEnable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);

        glClearColor(0, 0, 0, 0);

        setupTerrain();
        glInterleavedArrays(GL_N3F_V3F, 0, terrain);

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        //glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, 1);
        light.on();
        material.set();
    }

    @Override
    protected void input() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    long start = System.currentTimeMillis();

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glPushMatrix();
        glRotatef(((System.currentTimeMillis() - start) % 7200) / 20f, 0, 1, 0);
        glDrawArrays(GL_TRIANGLES, 0, terrain.remaining() / 6);
        //glDrawElements(GL_TRIANGLES, indexes);
        glPopMatrix();
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new Teren().start();
    }
}
