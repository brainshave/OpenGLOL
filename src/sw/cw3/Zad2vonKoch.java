/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw3;

import sw.utils.GLBaza;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

/**
 *
 * @author student
 */
public class Zad2vonKoch extends GLBaza {

    @Override
    protected void init() {
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        if (width > height) {
            gluOrtho2D(-(float) width / height,
                    (float) width / height, -1, 1);
        } else {

            gluOrtho2D(-1, 1, -(float) height / width,
                    (float) height / width);
        }
        glTranslatef(-0.5f, -0.3f, 0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glColor3f(1, 0, 0);
    }

    @Override
    protected void input() {
    }

    protected final static void drawKoch(float len, int deep) {
        if (deep == 0) {
            glBegin(GL_LINES);
            glVertex2f(0, 0);
            glVertex2f(len, 0);
            glEnd();
            glTranslatef(len, 0, 0);
            return;
        }

        deep--;
        len /= 3;

        drawKoch(len, deep);

        glRotatef(-60, 0, 0, 1);
        drawKoch(len, deep);

        glRotatef(120, 0, 0, 1);
        drawKoch(len, deep);

        glRotatef(-60, 0, 0, 1);
        drawKoch(len, deep);
    }
    int depth = 0;
    int direction = 1;

    @Override
    protected void render() {
        glLoadIdentity();
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        for (int i = 0; i < 3; i++) {
            drawKoch(1, depth);
            glRotatef(120, 0, 0, 1);
        }
        glFlush();

        depth += direction;
        if (depth <= 0 || depth >= 6) {
            direction = -direction;
        }

    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new Zad2vonKoch().start();
    }
}
