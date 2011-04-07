/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw4;

import java.util.logging.Level;
import java.util.logging.Logger;
import sw.utils.GLBaza;
import static org.lwjgl.util.glu.GLU.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author student
 */
public class Kostka extends GLBaza {

    float[] data = {
        1, 1, 1, -1,1,1,
        1, 1, 1, -1,-1,1,
        1, 1, 1, 1,1,1,
        1, 1, 1, 1,-1,1,
        1,1,1, 1,1,-1,
        1,1,1, 1,-1,-1,
        1,1,1, -1, 1, -1,
        1,1,1, -1, -1, -1
    };
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    int[] indidata = {0,1,2,3,4,5,6,7,0,1};
    IntBuffer brickIndices = BufferUtils.createIntBuffer(indidata.length);
    int[] paintedWallindi = {0,1,3,2};
    IntBuffer paintedWall = BufferUtils.createIntBuffer(paintedWallindi.length);


    @Override
    protected void init() {
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        if (width > height) {
            glOrtho(-5*(float) width / height,
                    5*(float) width / height, -5, 5, -5, 5);
        } else {

            glOrtho(-5, 5, -5*(float) height / width,
                    5*(float) height / width, -5, 5);
        }
        //glTranslatef(-0.5f, -0.5f, 0);
        //glScalef(0.5f, 0.5f, 0.5f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(1, 1, 1, 0, 0, 0, 0, 1, 0);
        //glScalef(0.5f, 0.5f, 0.5f);

        buffer.rewind();
        buffer.put(data);
        buffer.rewind();

        brickIndices.rewind();
        brickIndices.put(indidata);
        brickIndices.rewind();

        paintedWall.rewind();
        paintedWall.put(paintedWallindi);
        paintedWall.rewind();

        glInterleavedArrays(GL_C3F_V3F, 0, buffer);
    }

    @Override
    protected void input() {
    }

    @Override
    protected void render() {
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        glRotatef(1, 1, 0, 0);

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glDrawElements(GL_QUAD_STRIP, brickIndices);
        glPolygonMode(GL_FRONT, GL_FILL);
        glDrawElements(GL_QUADS, paintedWall);
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
            Logger.getLogger(Kostka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new Kostka().start();
    }
}
