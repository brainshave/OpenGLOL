package sw.cw8;

import org.lwjgl.input.Keyboard;
import sw.utils.GLBaza;
import sw.utils.Light;
import sw.utils.Material;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 13.04.11
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class Fly extends GLBaza {
    final int DENSITY = 5;

    Terrain NW = new Terrain(DENSITY);
    Terrain N = new Terrain(NW, Terrain.Side.WEST);
    Terrain NE = new Terrain(N, Terrain.Side.WEST);
    Terrain E = new Terrain(NE, Terrain.Side.NORTH);
    Terrain SE = new Terrain(E, Terrain.Side.NORTH);
    Terrain S = new Terrain(SE, Terrain.Side.EAST);
    Terrain SW = new Terrain(S, Terrain.Side.EAST);
    Terrain W = new Terrain(SW, Terrain.Side.SOUTH, NW, Terrain.Side.NORTH);

    Terrain C;

    {
        List<Origin> origins = new ArrayList<Origin>(4);
        origins.add(new Origin(N, Terrain.Side.NORTH));
        origins.add(new Origin(S, Terrain.Side.SOUTH));
        origins.add(new Origin(E, Terrain.Side.EAST));
        origins.add(new Origin(W, Terrain.Side.WEST));
        C = new Terrain(origins);
    }

    Terrain[][] grid = {
            {NW, N, NE},
            {W, C, E},
            {SW, S, SE}
    };

    protected void moveGridNorth() {
        for (int row = grid.length - 1; row > 0; --row) {
            for (int col = 0; col < grid[row].length; ++col) {
                grid[row][col] = grid[row - 1][col];
            }
        }
        grid[0][0] = new Terrain(grid[1][0], Terrain.Side.SOUTH);
        for (int col = 1; col < grid[0].length; ++col) {
            grid[0][col] = new Terrain(
                    grid[0][col - 1], Terrain.Side.WEST,
                    grid[1][col], Terrain.Side.SOUTH);
        }
    }

    protected void moveGridSouth() {
        for (int row = 0; row < grid.length - 1; ++row) {
            for (int col = 0; col < grid[row].length; ++col) {
                grid[row][col] = grid[row + 1][col];
            }
        }
        grid[grid.length - 1][0] = new Terrain(grid[grid.length - 2][0], Terrain.Side.NORTH);
        for (int col = 1; col < grid[0].length; ++col) {
            grid[grid.length - 1][col] = new Terrain(
                    grid[grid.length - 1][col - 1], Terrain.Side.WEST,
                    grid[grid.length - 2][col], Terrain.Side.NORTH);
        }
    }

    protected void moveGridEast() {

    }

    protected void moveGridWest() {

    }

    Light light = new Light(GL_LIGHT0, new float[][]{{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}, {10, 10, 0, 1}});
    Material material = new Material(100, new float[][]{{0f, 0.1f, 0, 1}, {0, 0.75f, 0, 1}, {1, 1, 1, 1}});

    @Override
    protected void init() {
//        glEnable(GL_LINE_SMOOTH);
//        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 1, 7);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(0, 2.2f, -1.7f, 0, 1f, -1.5f, 0, 1, 0);

        glEnable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);

        glClearColor(0, 0, 0, 0);

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        //glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, 1);
        light.on();
        material.set();
    }

    @Override
    protected void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_UP:
                        moveGridNorth();
                        break;
                    case Keyboard.KEY_DOWN:
                        moveGridSouth();
                }
            }
        }
    }

    long start = System.currentTimeMillis();

    int T = 1000;
    float movement = 0;

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glPushMatrix();
        //glRotatef(((System.currentTimeMillis() - start) % 7200) / 20f, 0, 1, 0);
        glScalef(-1, 1, 1); // zamiana współrz. X

        glTranslatef(-2, 0, 2f - movement);
        for (int x = 0; x < 3; ++x) {
            glPushMatrix();
            for (int z = 0; z < 3; ++z) {
                grid[x][z].draw();
                glTranslatef(2, 0, 0);
            }
            glPopMatrix();
            glTranslatef(0, 0, -2);
        }

        glPopMatrix();
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
        float newMovement = (((float)(System.currentTimeMillis() - start) % T) / T) * 2;
        if(newMovement < movement) {
            moveGridNorth();
        }
        movement = newMovement;
    }

    public static void main(String[] args) {
        new Fly().start();
    }
}
