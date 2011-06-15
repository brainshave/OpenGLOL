package sw.cw11;

import org.lwjgl.input.Keyboard;
import sun.rmi.runtime.NewThreadAction;
import sw.utils.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 15.06.11
 * Time: 12:20
 */
public class ShadowMapping2 extends GLBaza implements Scene {
    SceneWithShadowRenderer renderer;
    Cube cube;
    int firstDisplayList;

    Material cubeMat, tabletopMat;
    float[] lookingAt = {0, 0, 0};
    float cameraNear = 1.5f;
    float cameraFar = 10;
    float[] cameraPos = {0, 6, 6, 1};
    float[] cameraUpV = {0, 1, 0};
    float[] lightPos = {-3, 20, -3, 1};
    float[] lightUpV = {1, 0, 0};
    float lightNear = 8;
    float lightFar = 20;

    float rotation;
    boolean lit = true;
    boolean rotate = true;
    int term = 5000;


    @Override
    protected void init() {
        renderer = new SceneWithShadowRenderer(
                this, 512, cameraNear, cameraFar,
                cameraPos, lookingAt, cameraUpV,
                GL_LIGHT0, lightNear, lightFar,
                lightPos, lookingAt, lightUpV);

        cube = new Cube();
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

        createLists();
    }


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

    public void drawScene(boolean lit) {
        glPushMatrix();
        glCallList(firstDisplayList);
        glRotatef(rotation, 0, 1, 1);
        glCallList(firstDisplayList + 1);
        glPopMatrix();
    }


    @Override
    protected void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_SPACE:
                        lit = !lit;
                        break;
                    case Keyboard.KEY_R:
                        rotate = !rotate;
                        break;
                    case Keyboard.KEY_LEFT:
                        rotation += 1;
                        break;
                    case Keyboard.KEY_RIGHT:
                        rotation -= 1;
                }
            }
        }
    }

    @Override
    protected void render() {
        renderer.render(true);
    }

    @Override
    protected void logic() {
        Utils.sleep60Hz();
        if (rotate)
            rotation = ((float) (System.currentTimeMillis() % term) / term) * 360;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static void main(String[] args) {
        new ShadowMapping2().start();
    }
}
