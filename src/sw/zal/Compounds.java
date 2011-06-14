package sw.zal;

import sw.utils.GLBaza;
import sw.utils.Utils;

import java.io.File;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 09:22
 * To change this template use File | Settings | File Templates.
 */
public class Compounds extends GLBaza {
    Tetrahedron tetrahedron;

    @Override
    protected void init() {
        Utils.initPerspective(this, 1, 100);
        Utils.enable(new int[]{GL_DEPTH_TEST, GL_NORMALIZE, GL_TEXTURE_2D});

        gluLookAt(0, 0, 4, 0, 0, 0, 0, 1, 0);

        glShadeModel(GL_SMOOTH);
        glColor4f(1, 1, 1, 1);
        glClearColor(0, 0, 0, 0);

        tetrahedron = new Tetrahedron(new File("tekstury"));
    }

    @Override
    protected void input() {
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glRotatef(1,0,1,1);
        tetrahedron.setBuffer();
        tetrahedron.resetTexturePointer();
        tetrahedron.draw(true);
    }

    @Override
    protected void logic() {
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new Compounds().start();
    }
}
