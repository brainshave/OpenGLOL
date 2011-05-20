package sw.cw9;

import sw.utils.GLBaza;
import sw.utils.Light;
import sw.utils.Material;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 04.05.11
 * Time: 21:44
 */
public class Las extends GLBaza {
    Cone cone;
    Trunk trunk;
    Light light;
    Material green, bronze;
    int amount = 100;
    float[][] forrest = new float[amount][];

    {
        Random r = new Random();
        for (int i = 0; i < amount; ++i) {
            forrest[i] = new float[]{correctCoords(r.nextFloat() * 10 - 5), correctCoords(r.nextFloat() * 10 - 5)};
        }
    }

    @Override
    protected void init() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 2, 20);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, 2, 20);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(4, 1, 4, 0, 0, 0, 0, 1, 0);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_NORMALIZE);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        cone = new Cone(50);
        trunk = new Trunk(50);
        light = new Light(GL_LIGHT0, new float[][]{{0, 0, 0, 0}, {1, 1, 1, 1}, {1, 1, 1, 1}, {5, 5, 5, 1}});
        green = new Material(10, new float[][]{{0, 0, 0, 0}, {0, 0.4f, 0, 1}, {0.5f, 0.5f, 0.5f, 1}});
        bronze = new Material(1, new float[][]{{0, 0, 0, 0}, {0.4f, 0.4f, 0, 1}, {0.5f, 0.5f, 0.5f, 1}});

        //green.set();
        //bronze.set();
        light.on();
        glClearColor(0, 0, 0, 0);
    }

    @Override
    protected void input() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    float correctCoords(float x) {
        if (x > 0) return x + 3;
        else return x - 3;
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glRotatef(1, 0, 1, 0);
        for (float[] coords : forrest) {
            drawTree(coords[0], coords[1]);
        }

        glFlush();
    }

    void drawTree(float x, float z) {
        glPushMatrix();
        glTranslatef(x, 0, z);
        glScalef(0.05f, 0.3f, 0.05f);
        bronze.set();
        trunk.draw();
        glTranslatef(0, 1, 0);
        glScalef(4, 1, 4);
        green.set();
        cone.draw();
        glTranslatef(0, 0.6f, 0);
        glScalef(0.7f, 0.7f, 0.7f);
        cone.draw();
        glTranslatef(0, 0.6f, 0);
        glScalef(0.7f, 0.7f, 0.7f);
        cone.draw();
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
        new Las().start();
    }
}
