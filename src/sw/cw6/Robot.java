package sw.cw6;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.Sphere;
import sw.utils.GLBaza;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: Szymon Witamborski
 * Date: 04.04.11
 * Time: 17:36
 */

class Cube {
    static FloatBuffer cubeBuffer = BufferUtils.createFloatBuffer(24);
    static IntBuffer cubeIndexes = BufferUtils.createIntBuffer(10);

    static {
        cubeBuffer.rewind();
        cubeBuffer.put(new float[]{
                -1, 1, 1,
                -1, -1, 1,
                1, 1, 1,
                1, -1, 1,
                1, 1, -1,
                1, -1, -1,
                -1, 1, -1,
                -1, -1, -1
        });
        cubeBuffer.flip();

        cubeIndexes.rewind();
        cubeIndexes.put(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 1});
        cubeIndexes.flip();
    }

    public static void draw(float size) {
        glPushMatrix();
        glScalef(size / 2, size / 2, size / 2);
        glColor3f(1, 1, 1);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glInterleavedArrays(GL_V3F, 0, cubeBuffer);
        glDrawElements(GL_QUAD_STRIP, cubeIndexes);
        glPopMatrix();
    }

    public static void draw(float size, float length) {
        glTranslatef(length / 2 + size, 0, 0);
        draw(size, length, 0);
    }

    public static void draw(float size, float length, float swing) {
        glRotatef(swing, 0, 1, 0);
        glPushMatrix();
        glScalef(length / size, 1, 1);
        draw(size);
        glPopMatrix();
        glTranslatef(length / 2 - size / 2, 0, 0);
    }

    public static void drawBackwards(float size, float length) {
        glTranslatef(length / 2 - size / 2, 0, 0);
        glPushMatrix();
        glScalef(length / size, 1, 1);
        draw(size);
        glPopMatrix();
        glTranslatef(length / 2 + size, 0, 0);
    }
}


public class Robot extends GLBaza {
    private long start;

    @Override
    protected void init() {
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 2, 40);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(15, 5, 15, 0, 5, 0, 0, 1, 0);

        glEnable(GL_DEPTH_TEST);

        glColor3f(1, 1, 1);
        glClearColor(0, 0, 0, 0);

        start = System.currentTimeMillis();
    }

    @Override
    protected void input() {
    }

//    protected void drawBot(float[] degs) {
//        drawBot(degs, );
//    }


    protected void drawBot(float[] degs, boolean happy) {
        float a, b, c, d, z;
        if (degs[seqlength - 1] == 1f) {
            a = degs[0];
            b = degs[1];
            c = degs[2];
            d = degs[3];
            z = 1;
        } else {
            a = degs[2];
            b = degs[3];
            c = degs[0];
            d = degs[1];
            z = -1;
        }
        float swing = degs[4];
        glPushMatrix();
        glTranslatef(0, 0, z);
        //System.out.println(swing);
        Cube.draw(.3f, 1, swing);
        glRotatef(180 - a, 0, 0, 1);
        Cube.draw(.3f, 2);
        glRotatef(180 - b, 0, 0, -1);
        Cube.draw(.3f, 2);
        glRotatef(a - b, 0, 0, 1);
        glTranslatef(0, 0, -z);
        Cube.draw(1);
        Face.draw(happy);
        glTranslatef(0, 0, -z);
        glRotatef(c - d + 180, 0, 0, -1);
        Cube.drawBackwards(.3f, 2);
        glRotatef(180 - d, 0, 0, 1);
        Cube.drawBackwards(.3f, 2);
        glRotatef(180 - c, 0, 0, -1);
        Cube.drawBackwards(.3f, 1);
        glPopMatrix();
    }

    static final float R = 10;
    static final int WAY_DENSITY = 200;
    FloatBuffer ways = BufferUtils.createFloatBuffer(WAY_DENSITY * 6);

    {
        ways.rewind();
        for (int i = 0; i < WAY_DENSITY; ++i) {
            double t = (Math.PI * 2 * i) / (double) WAY_DENSITY;
            ways.put(new float[]{
                    (R - 1) * (float) Math.cos(t),
                    0,
                    (R - 1) * (float) Math.sin(t)
            });
        }

        for (int i = 0; i < WAY_DENSITY; ++i) {
            double t = (Math.PI * 2 * i) / (double) WAY_DENSITY;
            ways.put(new float[]{
                    (R + 1) * (float) Math.cos(t),
                    0,
                    (R + 1) * (float) Math.sin(t)
            });
        }
        ways.flip();
    }

    private void drawWays() {
        glColor3f(0, 1, 0);
        glInterleavedArrays(GL_V3F, 0, ways);
        // wew. okręg
        glDrawArrays(GL_LINE_LOOP, 0, WAY_DENSITY);
        // zew. okręg
        glDrawArrays(GL_LINE_LOOP, WAY_DENSITY, WAY_DENSITY);
    }

    float term = 1000;
    float pos = 10;
    float rotation = 180;
    double stepLen = 3.7;
    float rotStep = 21.43f;
    float leftDeg = 11.25f;
    float rightDeg = 10.18f;
    int last_i = 0;

    float[][] sequence = {
            {90, 120, 30, 120, -leftDeg, 0, 1},
            {76, 180, 30, 60, 0, leftDeg, 1},
            {30, 120, 90, 120, -rightDeg, 0, -1},
            {30, 60, 90, 180, 0, rightDeg, -1}
    };

    final int seqlength = sequence[0].length;

    private float[] botFigureInTime() {
        final float moment = (((System.currentTimeMillis() - start) % term) * sequence.length) / term;
        final int i = (int) Math.floor(moment);
        final int i2 = (i + 1) % sequence.length;
        float amount = moment - i;
        float[] degs = new float[seqlength];
        for (int d = 0; d < 4; ++d) {
            degs[d] = sequence[i][d] * (1 - amount) + sequence[i2][d] * amount;
        }
        degs[4] = sequence[i][4] * (1f - amount) + amount * sequence[i][5];

        degs[seqlength - 1] = sequence[i][seqlength - 1];
        if (sequence[i][seqlength - 1] != sequence[last_i][seqlength - 1]) {
            rotation += rotStep;
        }
        last_i = i;
        return degs;
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawWays();
        sadBot();
        glPushMatrix();
        float[] figure = botFigureInTime();
        glRotatef(rotation, 0, 1, 0);
        for (int i = 0; i < 8; ++i) {
            glRotatef(45, 0, 1, 0);
            glPushMatrix();
            glTranslatef(10, .15f, 0);
            glRotatef(-90, 0, 1, 0);
            drawBot(figure, true);
            glPopMatrix();
        }
        glPopMatrix();
        glFlush();
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }


    public static void main(String[] args) {
        new Robot().start();
    }

    float sadBotTerm = 15000;
    public void sadBot() {
        glPushMatrix();
        glScalef(2,2,2);
        //glRotatef(160, 0,1,0);
        glRotatef(((System.currentTimeMillis() - start) % sadBotTerm) * 360 / sadBotTerm, 0, -1, 0);
        drawBot(new float[]{0,90, 0, 90, 0,0,0}, false);
        glPopMatrix();
    }
}


class Face {
    private static Sphere sphere = new Sphere();
    static float[] smile_verts = {
            0, 0, -0.3f,
            0, -0.2f, -0.1f,
            0, -0.2f, 0.1f,
            0, 0, 0.3f,
    };
    static float[] mask_verts = {
            0, 0.5f, 0.5f,
            0, 0.5f, -0.5f,
            0, -0.5f, 0.5f,
            0, -0.5f, -0.5f
    };
    static FloatBuffer smile_buff = BufferUtils.createFloatBuffer(smile_verts.length);
    static FloatBuffer mask_buff = BufferUtils.createFloatBuffer(mask_verts.length);

    static {
        smile_buff.rewind();
        smile_buff.put(smile_verts);
        smile_buff.flip();

        mask_buff.rewind();
        mask_buff.put(mask_verts);
        mask_buff.flip();
    }

    public static void draw() {
        draw(true);
    }

    public static void draw(boolean happy) {
        glPushMatrix();
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glTranslatef(-0.5f, 0.25f, 0.25f);
        glColor3f(0, 1, 1);
        sphere.draw(0.15f, 10, 10);
        glTranslatef(0, 0, -0.5f);
        sphere.draw(0.15f, 10, 10);
        glTranslatef(0, -0.25f, 0.25f);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glTranslatef(0.15f, 0,0);
        glColor3f(0,0,0);
        glInterleavedArrays(GL_V3F, 0, mask_buff);
        glDrawArrays(GL_QUAD_STRIP, 0, 4);

        if(!happy) {
            glRotatef(180, 1,0,0);
            glTranslatef(0,0.3f,0);
        }
        glTranslatef(-0.15f, -0.05f,0);
        glColor3f(1, 0, 0);
        glInterleavedArrays(GL_V3F, 0, smile_buff);
        glLineWidth(3);
        glDrawArrays(GL_LINE_STRIP, 0, 4);
        glLineWidth(1);
        glPopMatrix();
    }
}
