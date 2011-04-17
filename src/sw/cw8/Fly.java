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
    final int DENSITY = 4;
    final int SIZE = 9;

    Terrain[][] grid = new Terrain[SIZE][SIZE];

    {
        grid[0][0] = new Terrain(DENSITY);
        for(int col = 1; col < SIZE; ++col) {
            grid[0][col] = new Terrain(grid[0][col-1], Terrain.Side.WEST);
        }

        for (int row = 1; row < SIZE; ++row) {
            grid[row][0] = new Terrain(grid[row - 1][0], Terrain.Side.NORTH);
            for (int col = 1; col < SIZE; ++col) {
                grid[row][col] = new Terrain(
                        grid[row - 1][col], Terrain.Side.NORTH,
                        grid[row][col - 1], Terrain.Side.WEST);
            }
        }
    }

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


    protected void moveGridWest() {
        for (int col = grid[0].length - 1; col > 0; --col) {
            for (int row = 0; row < grid.length; ++row) {
                grid[row][col] = grid[row][col - 1];
            }
        }
        grid[0][0] = new Terrain(grid[0][1], Terrain.Side.EAST);
        for (int row = 1; row < grid.length; ++row) {
            grid[row][0] = new Terrain(
                    grid[row - 1][0], Terrain.Side.NORTH,
                    grid[row][1], Terrain.Side.EAST
            );
        }
    }

    protected void moveGridEast() {
        for (int col = 0; col < grid[0].length - 1; ++col) {
            for (int row = 0; row < grid.length; ++row) {
                grid[row][col] = grid[row][col + 1];
            }
        }
        grid[0][grid[0].length - 1] = new Terrain(grid[0][grid[0].length - 2], Terrain.Side.WEST);
        for (int row = 1; row < grid.length; ++row) {
            grid[row][grid[0].length - 1] = new Terrain(
                    grid[row - 1][grid[0].length - 1], Terrain.Side.NORTH,
                    grid[row][grid[0].length - 2], Terrain.Side.WEST
            );
        }
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
                    size * (float) width / height, -size, size, 1, 30);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);

        glClearColor(0, 0, 0, 0);

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        //glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, 1);
        light.on();
        material.set();
    }

    float rotation = 0;
    boolean rotateLeft;
    boolean rotateRight;
    float altitude = 3;
    boolean goUp;
    boolean goDown;

    @Override
    protected void input() {
        while (Keyboard.next()) {
            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_UP:
                    goUp = Keyboard.getEventKeyState();
                    break;
                case Keyboard.KEY_DOWN:
                    goDown = Keyboard.getEventKeyState();
                    break;
                case Keyboard.KEY_LEFT:
                    rotateLeft = Keyboard.getEventKeyState();
                    break;
                case Keyboard.KEY_RIGHT:
                    rotateRight = Keyboard.getEventKeyState();
                    break;
            }
        }
    }

    float moveX;
    float moveZ;
    boolean fly = true;

    double cameraDistance() {
        return - SIZE - Math.sqrt(altitude);
    }

    double cameraX() {
        return Math.sin(Math.toRadians(rotation)) * cameraDistance();
    }

    double cameraZ() {
        return Math.cos(Math.toRadians(rotation)) * cameraDistance();
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glLoadIdentity();
        gluLookAt((float) -cameraX(), altitude, (float) cameraZ(),
                0, 2, 0,
                0, 1, 0);
        glTranslatef(moveX, 0, -moveZ);

        glScalef(-1, 1, 1); // zamiana współrz. X
        glTranslatef(-SIZE+1, 0, SIZE-1);

        for (int x = 0; x < SIZE; ++x) {
            glPushMatrix();
            for (int z = 0; z < SIZE; ++z) {
                grid[x][z].draw();
                glTranslatef(2, 0, 0);
            }
            glPopMatrix();
            glTranslatef(0, 0, -2);
        }
    }

    double step = 0.2;

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }

        altitude += goUp ? 0.1f : (goDown ? -0.1f : 0);
        rotation += rotateLeft ? -1.5f : (rotateRight ? 1.5f : 0);

        if (fly) {
            moveX += Math.sin(Math.toRadians(rotation)) * step;
            if (moveX > 2) {
                moveGridEast();
                moveX %= 2;
            } else if (moveX < -2) {
                moveGridWest();
                moveX %= 2;
            }

            moveZ += Math.cos(Math.toRadians(rotation)) * step;
            if (moveZ > 2) {
                moveGridNorth();
                moveZ %= 2;
            } else if (moveZ < -2) {
                moveGridSouth();
                moveZ %= 2;
            }
        }
    }

    public static void main(String[] args) {
        new Fly().start();
    }
}
