/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw3;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import sw.utils.GLBaza;
import static org.lwjgl.util.glu.GLU.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.Util.*;

/**
 *
 * @author student
 */
public class Zad34Sinus extends GLBaza {

    protected static final int AMOUNT = 50;
    protected static final float STEP = (float) (2 * Math.PI / AMOUNT);
    FloatBuffer vertBuff = BufferUtils.createFloatBuffer(4 * 2 * 50);
    FloatBuffer colorsBuff = BufferUtils.createFloatBuffer(4 * 3 * AMOUNT);
    float phase = 0;
    protected static final float PHASE_STEP = (float) (Math.PI / 100);
    Random rand = new Random();

    @Override
    protected void init() {
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        if (width > height) {
            gluOrtho2D(-(float) width / height,
                    (float) width / height, -1, 1);
        } else {

            gluOrtho2D(-1, 1, -(float) height / width,
                    (float) height / width);
        }
        glScalef((float) (1.0 / Math.PI), (float) (1.0 / Math.PI), 1);
        glTranslatef((float) -Math.PI, -0.5f, 0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glColor3f(1, 0, 0);

        recalcVertBuff(0);
        glVertexPointer(2, 0, vertBuff);

        recalcColorBuff();
        glColorPointer(3, 0, colorsBuff);
    }

    @Override
    protected void input() {
    }

    protected final void recalcColorBuff() {
        colorsBuff.rewind();
        while (colorsBuff.hasRemaining()) {
            colorsBuff.put(rand.nextFloat());
        }
        colorsBuff.rewind();
    }

    protected final void recalcVertBuff(float phase) {
        vertBuff.rewind();
        for (float x = 0; x < 2 * Math.PI; x += STEP) {
            float y = (float) Math.sin(x + phase);
            vertBuff.put(x).put(y).put(x + STEP).put(y).put(x + STEP).put(0).put(x).put(0);
        }
        vertBuff.rewind();
    }

    @Override
    protected void render() {
        glLoadIdentity();
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        glDrawArrays(GL_QUADS, 0, AMOUNT * 4);

        glFlush();
        checkGLError();
    }

    @Override
    protected void logic() {
        phase += PHASE_STEP;
        if (phase > 2 * Math.PI) {
            phase = 0;
        }
        recalcVertBuff(phase);
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
            Logger.getLogger(Zad34Sinus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new Zad34Sinus().start();
    }
}
