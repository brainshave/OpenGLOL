/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw5;

import org.lwjgl.BufferUtils;
import sw.utils.GLBaza;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 *
 * @author Szymon
 */
public class Jajo extends GLBaza {

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
        gluLookAt(0, 7f, -15f, 0f, 3f, 10f, 0, 1, 0);
        //glScalef(0.3f, 0.3f, 0.3f);
    }
    public static final int DENSITY = 30;
    public static final float STEP = 1f / DENSITY;
    FloatBuffer vertices = BufferUtils.createFloatBuffer(DENSITY * DENSITY * 3);
    IntBuffer indices = BufferUtils.createIntBuffer(DENSITY * DENSITY);

    {
        vertices.rewind();
        for (float u = 0; u < 1; u += STEP) {
            float u2 = u * u;
            float u3 = u2 * u;
            float u4 = u3 * u;
            float u5 = u4 * u;
            float uu = (-90 * u5 + 225 * u4 - 270 * u3 + 180 * u2 - 45 * u);
            for (float v = 0; v < 1; v += STEP) {
                float piv = (float) (Math.PI * v);
                vertices.put(uu * ((float) Math.cos(piv))).put(160 * u4 - 320 * u3 + 160 * u2).put(uu * ((float) Math.sin(piv)));
            }
        }
        vertices.rewind();
        indices.rewind();
        for (int i = 0; i < DENSITY * DENSITY; i++) {
            indices.put(i);
        }
        indices.rewind();
    }

    @Override
    protected void input() {
    }

    @Override
    protected void render() {
        glInterleavedArrays(GL_V3F, 0, vertices);
        glDrawElements(GL_POINTS, indices);
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new Jajo().start();
    }
}
