package sw.cw11;

import org.lwjgl.input.Keyboard;
import sw.utils.*;

import java.security.Key;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

public class ShadowMapping extends GLBaza {
    Cube cube;
    Light bright, dim;
    int firstDisplayList;

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
        bright = new Light(GL_LIGHT0, lightPos);
        dim = new Light(GL_LIGHT0, new float[][]{
                {0, 0, 0, 0},
                {0.3f, 0.3f, 0.3f, 1},
                {0, 0, 0, 0},
                lightPos
        });
        cubeMat = new Material(120, new float[][]{
                {0, 0.2f, 0.2f, 1},
                {0, 1, 1, 1},
                {1, 1, 1, 1,}
        });
        tabletopMat = new Material(1, new float[][]{
                {0, 0.1f, 0, 1},
                {0, 0.1f, 0, 1},
                {0, 0, 0, 0}
        });

        bright.on();

        createLists();
    }

    @Override
    protected void input() {
        while(Keyboard.next()) {
            if(Keyboard.getEventKeyState()) {
                dim.on();
            } else {
                bright.on();
            }
        }
    }

    float rotation;

    void createLists() {
        firstDisplayList = glGenLists(2);
        // table
        glNewList(firstDisplayList, GL_COMPILE);
        {
            glPushMatrix();
            glScalef(5, 0.2f, 5);
            tabletopMat.set();
            cube.draw();
            glPopMatrix();
            glTranslatef(0, 3, 0);
        }
        glEndList();

        // brick
        glNewList(firstDisplayList + 1, GL_COMPILE);
        {
            cubeMat.set();
            cube.draw();
        }
        glEndList();
    }

    protected void drawScene() {
        glCallList(firstDisplayList);
        glRotatef(rotation, 0, 1, 1);
        glCallList(firstDisplayList + 1);
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        lookAt(cameraPos, cameraUpV);

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
