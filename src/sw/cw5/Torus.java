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
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 *
 * @author Szymon
 */
public class Torus extends GLBaza {

    public static final int DENSITY = 35;
    public static final float STEP = 1f / DENSITY;
    FloatBuffer vertices = BufferUtils.createFloatBuffer(DENSITY * DENSITY * 3);
    IntBuffer indices = BufferUtils.createIntBuffer(DENSITY * DENSITY);
    public static float R = 3;
    public static float r = 1;

    {
        vertices.rewind();
        for (float u = 0; u < 1; u += STEP) {
            float pi2u = (float) (Math.PI * 2 * u);
            float sin2piu = (float) Math.sin(pi2u);
            float cos2piu = (float) Math.cos(pi2u);
            for (float v = 0; v < 1; v += STEP) {
                float pi2v = (float) (Math.PI * 2 * v);
                float Rrcos2piv = R + r * (float) Math.cos(pi2v);
                vertices.put(Rrcos2piv * cos2piu).put(Rrcos2piv * sin2piu).put(r * (float) (Math.sin(pi2v)));
            }
        }
        vertices.flip();

        for (int i = 0; i < DENSITY * DENSITY; i++) {
            indices.put(i);
        }
        indices.flip();
    }

    @Override
    protected void init() {
//        glEnable(GL_LINE_SMOOTH);
//        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
        gluLookAt(-5f, -5f, -5f, 0f, 0f, 0f, 0, 1, 0);

        glGenBuffers(buffers);
        glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0));
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexPointer(3, GL_FLOAT, 0, 0);

        glEnableClientState(GL_VERTEX_ARRAY);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers.get(1));
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glColor3f(1, 1, 1);
    }
    IntBuffer buffers = BufferUtils.createIntBuffer(2);

    @Override
    protected void input() {
    }

    @Override
    protected void render() {
        //glInterleavedArrays(GL_V3F, 0, vertices);
        //glDrawElements(GL_POINTS, indices);
        //vertices.rewind();
        //glDrawArrays(GL_POINTS, 0, DENSITY * DENSITY);
        glDrawElements(GL_POINTS, DENSITY * DENSITY, GL_UNSIGNED_INT, 0);
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new Torus().start();
    }
}
