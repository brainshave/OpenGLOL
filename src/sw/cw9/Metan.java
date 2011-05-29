package sw.cw9;

import org.lwjgl.util.glu.Sphere;
import sw.utils.GLBaza;
import sw.utils.Light;
import sw.utils.Material;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 28.04.11
 * Time: 18:37
 */
public class Metan extends GLBaza {
    Light light = new Light(GL_LIGHT0, new float[][]{{1, 1, 0, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}, {-5, 10, 10, 1}});
    Material carbon = new Material(50, new float[][]{{0, 0, 0, 1}, {0.3f, 0.3f, 1f, 1f}, {0, 0, 1, 1}});
    //Material carbon = new Material(50, new float[][]{{0, 0, 1, 1}, {0,0,0,1}, {0,0,0,1}});
    Material hydrogen = new Material(10, new float[][]{{0.1f, 0.1f, 0.1f, 0}, {0.5f, 0.5f, 0.5f, 0.5f}, {0.3f, 0.3f, 0.3f, 0.1f}});

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
                    size * (float) height / width, 2, 7);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(4, 4, 4, 0, 0, 0, 0, 0, 1);

        glEnable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        glPolygonOffset(1, 1);
        glShadeModel(GL_SMOOTH);
        light.on();

        glClearColor(0.1f, 0.1f, 0.1f, 1f);
    }

    Sphere sphere = new Sphere();

    @Override
    protected void input() {

    }

    void drawH(float x, float y, float z) {
        glPushMatrix();
        glTranslatef(x, y, z);
        glEnable(GL_BLEND);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_POLYGON_OFFSET_FILL);
        //glPolygonOffset(1,1);
        sphere.draw(1, 20, 10);
        glDisable(GL_POLYGON_OFFSET_FILL);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        sphere.draw(1, 20, 10);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    @Override
    protected void render() {
        glRotatef(1, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDepthMask(false);

        //light.on();
        carbon.set();
        glEnable(GL_BLEND);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_POLYGON_OFFSET_FILL);
        //glPolygonOffset(-1,1);
        sphere.draw(2, 40, 20);
        glDisable(GL_POLYGON_OFFSET_FILL);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        sphere.draw(2, 40, 20);
        glDisable(GL_BLEND);

        glDepthMask(true);
        hydrogen.set();
        for (float[] coords : new float[][]{{1, 1, 1}, {-1, -1, 1}, {-1, 1, -1}, {1, -1, -1}}) {
            drawH(coords[0], coords[1], coords[2]);
        }
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new Metan().start();
    }
}
