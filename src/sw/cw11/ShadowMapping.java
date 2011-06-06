package sw.cw11;

import sw.utils.GLBaza;
import sw.utils.Utils;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

public class ShadowMapping extends GLBaza {
    @Override
    protected void init() {
        Utils.initPerspective(this, 1, 100);
        Utils.enable(new int[]{GL_DEPTH_TEST});

        gluLookAt(0, 0, 6, 0, 0, 0, 0, 1, 0);

        glShadeModel(GL_SMOOTH);
        glColor4f(1,1,1,1);
        glClearColor(0,0,0,0);
    }

    @Override
    protected void input() {
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    protected void logic() {
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new ShadowMapping().start();
    }
}
