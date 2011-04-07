/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw2.zad2;

import org.lwjgl.util.glu.GLU;
import sw.utils.GLBaza;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author student
 */
public class Square extends GLBaza {

    public Square(int width, int height, String title) {
        super(width, height, title);
    }

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

    @Override
    protected void render() {

        for (int i = 0; i < width; i += width / 2) {
            for (int j = 0; j < height; j += height / 2) {
                glViewport(i, j, width / 2, height / 2);
                glBegin(GL_POLYGON);
                {
                    glColor3f(1, 0, 0);
                    glVertex2f(0.5f, 0.5f);
                    glColor3f(0, 1, 0);
                    glVertex2f(0.5f, -0.5f);
                    glColor3f(0, 0, 1);
                    glVertex2f(-0.5f, -0.5f);
                    glColor3f(0, 1, 1);
                    glVertex2f(-0.5f, 0.5f);
                }
                glEnd();
            }
        }
    }

    @Override
    protected void logic() {
    }

    public static void main(String[] args) {
        new Square(800, 600, "ASDF").start();
    }
}
