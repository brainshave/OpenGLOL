/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw3;

import sw.utils.GLBaza;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

/**
 *
 * @author student
 */
public class Zad1Roslinka extends GLBaza {

    float x = 0, y = 0, x2;
    final float tables[][] = {
        {-0.67f, -0.02f, 0f, -0.18f, 0.81f, 10},
        {0.4f, 0.4f, 0, -0.1f, 0.4f, 0},
        {-0.4f, -0.4f, 0, -0.1f, 0.4f, 0},
        {-0.1f, 0, 0, 0.44f, 0.44f, -2}};
    final Random r = new Random();

    //FloatBuffer[] matrixes
    @Override
    protected void init() {


        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        glScalef(0.03f, 0.03f, 0.03f);
        glTranslatef(0, -18f, 0);
        if (width > height) {
            gluOrtho2D(-(float) width / height,
                    (float) width / height, -1, 1);
        } else {

            gluOrtho2D(-1, 1, -(float) height / width,
                    (float) height / width);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glColor3f(1, 0, 0);
    }

    @Override
    protected void input() {
    }

    @Override
    protected void render() {
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        glBegin(GL_POINTS);
        for (int i = 0; i < 100000; i++) {
            glVertex2f(x, y);
            float[] t = tables[r.nextInt(4)];
            x2 = t[0] * x + t[1] * y + t[2];
            y = t[3] * x + t[4] * y + t[5];
            x = x2;
        }
        glEnd();
        glFlush();
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16,666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new Zad1Roslinka().start();
    }
}
