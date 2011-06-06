package sw.cw11;

import com.sun.deploy.panel.ITreeNode;
import org.lwjgl.opengl.GL11;
import sw.utils.*;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

public class ShadowMapping extends GLBaza {
    Cube cube;
    Light light;
    Material cubeMat, tabletopMat;
    float[] lightPos = {0, 10, 0, 1};
    float[] lightUpV = {0, 0, 1};
    float[] cameraPos = {0, 6, 6, 1};
    float[] cameraUpV = {0, 1, 0};

    private void lookAt(float[] pos, float[] upV) {
        glLoadIdentity();
        gluLookAt(pos[0], pos[1], pos[2], 0, 0, 0, upV[0], upV[1], upV[2]);
    }

    @Override
    protected void init() {
        Utils.initPerspective(this, 1.3f, 100);
        Utils.enable(new int[]{GL_DEPTH_TEST});

        lookAt(cameraPos, cameraUpV);

        glShadeModel(GL_SMOOTH);
        glColor4f(1, 1, 1, 1);
        glClearColor(0, 0, 0, 0);

        cube = new Cube();
        light = new Light(GL_LIGHT0, lightPos);
        cubeMat = new Material(120, new float[][]{
                {0, 0.2f, 0.2f, 1},
                {0, 1, 1, 1},
                {1, 1, 1, 1,}
        });
        tabletopMat = new Material(1, new float[][]{
                {0, 0, 0.2f, 1},
                {0, 0, 0.25f, 1},
                {0, 0, 0, 0}
        });

        light.on();

    }

    @Override
    protected void input() {
    }

    float rotation;

    protected void drawScene() {
        // table
        glPushMatrix();
        glScalef(5, 0.2f, 5);
        tabletopMat.set();
        cube.draw();
        glPopMatrix();

        // brick
        glPushMatrix();
        glTranslatef(0, 3, 0);
        glRotatef(rotation, 0, 1, 1);
        cubeMat.set();
        cube.draw();
        glPopMatrix();
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawScene();
    }


    int term = 5000;

    @Override
    protected void logic() {
        Utils.sleep60Hz();
        rotation = ((float) (System.currentTimeMillis() % term) / term) * 360;
    }

    public static void main(String[] args) {
        new ShadowMapping().start();
    }
}
