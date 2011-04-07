package sw.cw4;

import org.lwjgl.input.Keyboard;
import java.util.Collections;
import java.util.List;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import org.lwjgl.BufferUtils;
import sw.utils.GLBaza;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 *
 * @author Szymon Witamborski
 */
public class Ukladanka2 extends GLBaza {

    float[][] brickVertices = {
        {-0.5f, -0.5f, 0.5f},
        {-0.5f, 0.5f, 0.5f},
        {0.5f, -0.5f, 0.5f},
        {0.5f, 0.5f, 0.5f},
        {0.5f, -0.5f, -0.5f},
        {0.5f, 0.5f, -0.5f},
        {-0.5f, -0.5f, -0.5f},
        {-0.5f, 0.5f, -0.5f}
    };

    public static IntBuffer bufferFromArray(int[] array) {
        IntBuffer buf = BufferUtils.createIntBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.rewind();
        return buf;
    }
    IntBuffer wireIndices = bufferFromArray(new int[]{0, 1, 6, 7, 4, 5, 2, 3, 0, 1});
    IntBuffer frontIndices = bufferFromArray(new int[]{0, 1, 2, 3});
    Random rand = new Random();
    // losowanie kolorów w rogach kostek
    float[][][] colors = new float[4][4][3];

    {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int c = 0; c < 3; c++) {
                    colors[i][j][c] = rand.nextFloat();
                }
            }
        }
    }

    // Tworzenie pojedynczej kostki, kolory pobierane sa z rogow (x, y) do (x, y+1)
    public FloatBuffer fourColorBrick(int x, int y) {
        FloatBuffer buff = BufferUtils.createFloatBuffer(8 * 6);
        buff.rewind();
        int cornerPtr = 0;
        for (int i = x; i <= x + 1; i++) {
            for (int j = y; j <= y + 1; j++) {
                buff.put(colors[i][j]);
                buff.put(brickVertices[cornerPtr++]);
            }
        }

        for (int i = x + 1; i >= x; --i) {
            for (int j = y; j <= y + 1; j++) {
                buff.put(colors[i][j]);
                buff.put(brickVertices[cornerPtr++]);
            }
        }
        buff.rewind();
        return buff;
    }
    FloatBuffer[][] goodBricks = new FloatBuffer[3][3];
    FloatBuffer[][] currentBricks = new FloatBuffer[3][3];

    {
        // Tworzenie kostek rozwiazania
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!(i == 2 && j == 2)) {
                    goodBricks[i][j] = fourColorBrick(i, j);
                } else {
                    goodBricks[i][j] = null;
                }
            }
        }

        // tasowanie kostek, zeby uzyskac kostki dla gracza
        List<int[]> indexes = new ArrayList<int[]>(10);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                indexes.add(new int[]{i, j});
            }
        }
        Collections.shuffle(indexes);
        Iterator<int[]> indexIt = indexes.iterator();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int[] coords = indexIt.next();
                currentBricks[i][j] = goodBricks[coords[0]][coords[1]];
                if(currentBricks[i][j] == null) {
                    nextX = currX = i;
                    nextY = currY = j;
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

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        if (width > height) {
            glOrtho(-3 * (float) width / height,
                    3 * (float) width / height, -3, 3, -3, 3);
        } else {
            glOrtho(-3, 3, -3 * (float) height / width,
                    3 * (float) height / width, -3, 3);
        }
        glMatrixMode(GL_MODELVIEW);
    }
    int currX, currY;
    int nextX, nextY;

    @Override
    protected void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_UP:
                        nextY = currY + 1;
                        break;
                    case Keyboard.KEY_DOWN:
                        nextY = currY - 1;
                        break;
                    case Keyboard.KEY_LEFT:
                        nextX = currX - 1;
                        break;
                    case Keyboard.KEY_RIGHT:
                        nextX = currX + 1;
                        break;
                }
            }
        }
    }

    protected void renderBricks(FloatBuffer[][] bricks) {
        glTranslatef(-1.5f, -1.5f, 0);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (bricks[i][j] != null) {
                    glInterleavedArrays(GL_C3F_V3F, 0, bricks[i][j]);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glDrawElements(GL_QUAD_STRIP, wireIndices);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    glDrawElements(GL_QUAD_STRIP, frontIndices);
                }

                glTranslatef(0, 1, 0);
            }
            glTranslatef(1, -3, 0);
        }
    }

    @Override
    protected void render() {
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        glViewport(width / 3, height / 6, (width * 2) / 3, (height * 2) / 3);
        glLoadIdentity();
        gluLookAt(0f, 0f, 0f, -0.5f, -0.5f, -0.5f, 0, 1, 0);
        renderBricks(currentBricks);

        glViewport(0, height / 6, width / 3, height / 3);
        glLoadIdentity();
        renderBricks(currentBricks);

        glViewport(0, (height * 2) / 3, width / 3, height / 3);
        glLoadIdentity();
        renderBricks(goodBricks);

    }

    @Override
    protected void logic() {
        if (nextX > 2) {
            nextX = 2;
        }
        if (nextX < 0) {
            nextX = 0;
        }
        if (nextY > 2) {
            nextY = 2;
        }
        if (nextY < 0) {
            nextY = 0;
        }

        if (currX != nextX || currY != nextY) {
            currentBricks[currX][currY] = currentBricks[nextX][nextY];
            currentBricks[nextX][nextY] = null;
            currX = nextX;
            currY = nextY;
        }

        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        // ludzie dzisiaj posiadaja nierozsadnie wielkie monitory
        new Ukladanka2().start();
    }
}
