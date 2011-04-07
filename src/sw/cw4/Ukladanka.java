/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw4;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import sw.utils.GLBaza;
import static org.lwjgl.util.glu.GLU.*;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author student
 */
public class Ukladanka extends GLBaza {

    float[][] brickVertices = {
        {-0.5f, 0.5f, 0.5f},
        {-0.5f, -0.5f, 0.5f},
        {0.5f, 0.5f, 0.5f},
        {0.5f, -0.5f, 0.5f},
        {0.5f, 0.5f, -0.5f},
        {0.5f, -0.5f, -0.5f},
        {-0.5f, 0.5f, -0.5f},
        {-0.5f, -0.5f, -0.5f}
    };

    public static IntBuffer bufferFromArray(int[] array) {
        IntBuffer buf = BufferUtils.createIntBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.rewind();
        return buf;
    }
    IntBuffer wireIndices = bufferFromArray(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 0, 1});
    IntBuffer frontIndices = bufferFromArray(new int[]{0, 1, 2, 3});
    Random rand = new Random();

    public FloatBuffer randomColorBrick() {
        FloatBuffer buff = BufferUtils.createFloatBuffer(8 * 6);
        buff.rewind();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        for (float[] corner : brickVertices) {
            buff.put(r).put(g).put(b);
            buff.put(corner);
            //for(float c: corner) buff.put(c);
        }
        buff.rewind();
        return buff;
    }

    class Brick {

        final FloatBuffer buff;
        float x, y;

        public Brick(float x, float y) {
            this.x = x;
            this.y = y;
            this.buff = randomColorBrick();
        }

        public Brick(float x, float y, FloatBuffer buff) {
            this.buff = buff;
            this.x = x;
            this.y = y;
        }

        protected void render() {
            glPushMatrix();
            glTranslatef(-1.5f + this.x, -1.5f + this.y, 0);

            glInterleavedArrays(GL_C3F_V3F, 0, this.buff);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glDrawElements(GL_QUAD_STRIP, wireIndices);
            glPolygonMode(GL_FRONT, GL_FILL);
            glDrawElements(GL_QUAD_STRIP, frontIndices);
            glPopMatrix();
        }
    }
    Brick[] bricks = new Brick[8];
    int emptyX, emptyY;

    {
        emptyX = rand.nextInt(3);
        emptyY = rand.nextInt(3);
        int bricksPtr = 0;
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                if (!(x == emptyX && y == emptyY)) {
                    bricks[bricksPtr++] = new Brick(x, y);
                }
            }
        }
    }
    Brick[] goodBricks = new Brick[8];

    {
        Integer[] indexes = {0, 1, 2, 3, 4, 5, 6, 7};
        List<Integer> l = Arrays.asList(indexes);
        Collections.shuffle(l);
        indexes = (Integer[]) l.toArray();

        int emptyFinalX = 2;
        int emptyFinalY = 2;
        int bricksPtr = 0;
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                if (!(x == emptyFinalX && y == emptyFinalY)) {
                    goodBricks[bricksPtr] = new Brick(x, y, bricks[indexes[bricksPtr]].buff);
                    ++bricksPtr;
                }
            }
        }
    }

    @Override
    protected void init() {
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        if (width > height) {
            glOrtho(-3 * (float) width / height,
                    3 * (float) width / height, -3, 3, -3, 3);
        } else {
            glOrtho(-3, 3, -3 * (float) height / width,
                    3 * (float) height / width, -3, 3);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(1, 1, 1, 0, 0, 0, 0, 1, 0);
    }

    protected Brick findBrick(int x, int y) {
        for(Brick b: bricks) {
            if(b.x == x && b.y == y) {
                return b;
            }
        }
        return null;
    }

    @Override
    protected void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_UP:
                        break;
                    case Keyboard.KEY_DOWN:
                        break;
                    case Keyboard.KEY_LEFT:
                        break;
                    case Keyboard.KEY_RIGHT:
                        break;
                }
            }
        }
    }

    void renderBricks(Brick[] brs) {
        for (Brick brick : brs) {
            brick.render();
        }
    }

    @Override
    protected void render() {
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        glViewport(width / 3, height / 6, (width * 2) / 3, (height * 2) / 3);
        glLoadIdentity();
        gluLookAt(0f, 0f, 0f, -1, -1, -1, 0, 1, 0);
        renderBricks(bricks);

        glViewport(0, height / 6, width / 3, height / 3);
        glLoadIdentity();
        renderBricks(bricks);

        glViewport(0, (height * 2) / 3, width / 3, height / 3);
        glLoadIdentity();
        renderBricks(goodBricks);
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
            Logger.getLogger(Ukladanka.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        new Ukladanka().start();
    }
}
