/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw2.zad4;

import org.lwjgl.util.glu.GLU;
import sw.utils.GLBaza;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author student
 */
public class BeeRainbow extends GLBaza {

    byte bee_original[] = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x80,
        (byte) 0x01, (byte) 0xC0, (byte) 0x06, (byte) 0xC0, (byte) 0x03,
        (byte) 0x60, (byte) 0x04, (byte) 0x60, (byte) 0x06, (byte) 0x20,
        (byte) 0x04, (byte) 0x30, (byte) 0x0C, (byte) 0x20, (byte) 0x04,
        (byte) 0x18, (byte) 0x18, (byte) 0x20, (byte) 0x04, (byte) 0x0C,
        (byte) 0x30, (byte) 0x20, (byte) 0x04, (byte) 0x06, (byte) 0x60,
        (byte) 0x20, (byte) 0x44, (byte) 0x03, (byte) 0xC0, (byte) 0x22,
        (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22, (byte) 0x44,
        (byte) 0x01, (byte) 0x80, (byte) 0x22, (byte) 0x44, (byte) 0x01,
        (byte) 0x80, (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
        (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22,
        (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22, (byte) 0x66,
        (byte) 0x01, (byte) 0x80, (byte) 0x66, (byte) 0x33, (byte) 0x01,
        (byte) 0x80, (byte) 0xCC, (byte) 0x19, (byte) 0x81, (byte) 0x81,
        (byte) 0x98, (byte) 0x0C, (byte) 0xC1, (byte) 0x83, (byte) 0x30,
        (byte) 0x07, (byte) 0xe1, (byte) 0x87, (byte) 0xe0, (byte) 0x03,
        (byte) 0x3f, (byte) 0xfc, (byte) 0xc0, (byte) 0x03, (byte) 0x31,
        (byte) 0x8c, (byte) 0xc0, (byte) 0x03, (byte) 0x33, (byte) 0xcc,
        (byte) 0xc0, (byte) 0x06, (byte) 0x64, (byte) 0x26, (byte) 0x60,
        (byte) 0x0c, (byte) 0xcc, (byte) 0x33, (byte) 0x30, (byte) 0x18,
        (byte) 0xcc, (byte) 0x33, (byte) 0x18, (byte) 0x10, (byte) 0xc4,
        (byte) 0x23, (byte) 0x08, (byte) 0x10, (byte) 0x63, (byte) 0xC6,
        (byte) 0x08, (byte) 0x10, (byte) 0x30, (byte) 0x0c, (byte) 0x08,
        (byte) 0x10, (byte) 0x18, (byte) 0x18, (byte) 0x08, (byte) 0x10,
        (byte) 0x00, (byte) 0x00, (byte) 0x08};
    ByteBuffer bee = ByteBuffer.allocateDirect(bee_original.length);

    public BeeRainbow() {
        //bee.reset();
        for(byte b: bee_original) {
            bee.put(b);
        }
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
        glEnable(GL_POLYGON_STIPPLE);
        bee.rewind();
        glPolygonStipple(bee);
    }

    @Override
    protected void input() {
    }

    protected void render() {
        glBegin(GL_QUAD_STRIP);
        {
            glColor3f(1, 0, 0);
            glVertex2d(-0.5, -0.5);
            glVertex2d(-0.5, 0.5);
            glColor3f(1, 1, 0);
            glVertex2d(-0.5 + 1.0 / 6, -0.5);
            glVertex2d(-0.5 + 1.0 / 6, 0.5);
            glColor3f(0, 1, 0);
            glVertex2d(-0.5 + 2.0 / 6, -0.5);
            glVertex2d(-0.5 + 2.0 / 6, 0.5);
            glColor3f(0, 1, 1);
            glVertex2d(-0.5 + 3.0 / 6, -0.5);
            glVertex2d(-0.5 + 3.0 / 6, 0.5);
            glColor3f(0, 0, 1);
            glVertex2d(-0.5 + 4.0 / 6, -0.5);
            glVertex2d(-0.5 + 4.0 / 6, 0.5);
            glColor3f(1, 0, 1);
            glVertex2d(-0.5 + 5.0 / 6, -0.5);
            glVertex2d(-0.5 + 5.0 / 6, 0.5);
            glColor3f(1, 0, 0);
            glVertex2d(-0.5 + 6.0 / 6, -0.5);
            glVertex2d(-0.5 + 6.0 / 6, 0.5);
        }
        glEnd();
    }

    @Override
    protected void logic() {
    }

    public static void main(String[] args) {
        new BeeRainbow().start();
    }
}
