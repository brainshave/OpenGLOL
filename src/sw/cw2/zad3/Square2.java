/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw2.zad3;

import org.lwjgl.util.glu.GLU;
import sw.utils.GLBaza;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author student
 */
public class Square2 extends GLBaza {

    @Override
    protected void init() {
        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        if (width > height) {
            GLU.gluOrtho2D(-(float) width / height,
                    (float) width / height, -1, 1);
        } else {

            GLU.gluOrtho2D(-1, 1, -(float) height / width,
                    (float) height / width);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    @Override
    protected void input() {
    }

    private void square() {
        glBegin(GL_LINE_LOOP);
        {
            glColor3f(1, 0, 0);
            glVertex2f(0.3f, 0.3f);
            glColor3f(0, 1, 0);
            glVertex2f(0.3f, -0.3f);
            glColor3f(0, 0, 1);
            glVertex2f(-0.3f, -0.3f);
            glColor3f(0, 1, 1);
            glVertex2f(-0.3f, 0.3f);
        }
        glEnd();
    }

    @Override
    protected void render() {
        glClearColor(0,0,0,0);
        glClear(GL_COLOR_BUFFER_BIT);
        glLoadIdentity();
        square();

        glTranslatef(0.3f, 0.3f, 0);
        glEnable(GL_LINE_STIPPLE);
        glLineStipple(1, (short) 0x77);
        square();
        glScalef(0.5f, 0.5f, 1);
        square();
        glRotatef(45, 0, 0, 1);
        square();
        glDisable(GL_LINE_STIPPLE);
    }

    @Override
    protected void logic() {
    }


    public static void main(String[] args) {
        new Square2().start();
    }
}
