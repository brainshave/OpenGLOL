package sw.cw8;

import org.lwjgl.BufferUtils;
import sw.utils.GLBaza;
import sw.utils.Light;
import sw.utils.Material;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;
import static sw.utils.Utils.normalize;
import static sw.utils.Utils.vector;
import static sw.utils.Utils.vectorProduct;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 13.04.11
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class Fly extends GLBaza {
    final int DENSITY = 6;

    Terrain terrain;
    Terrain terrain2;
    Terrain terrain3;

    {
        terrain = new Terrain(DENSITY);

        List<Origin> origins = new ArrayList<Origin>(1);
        origins.add(new Origin(terrain, Terrain.Side.WEST));
        terrain2 = new Terrain(origins);


        origins = new ArrayList<Origin>(1);
        origins.add(new Origin(terrain, Terrain.Side.EAST));
        terrain3 = new Terrain(origins);
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
                    size * (float) width / height, -size, size, 2, 20);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(0, 4f, 4f, 0, 0, 0, 0, 1, 0);

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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    long start = System.currentTimeMillis();

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glPushMatrix();
        glRotatef(((System.currentTimeMillis() - start) % 7200) / 20f, 0, 1, 0);
        terrain.draw();
        glTranslatef(2, 0, 0);
        terrain2.draw();
        glTranslatef(-4,0,0);
        terrain3.draw();
        glPopMatrix();
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new Fly().start();
    }
}
