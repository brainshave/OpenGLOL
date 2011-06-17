package sw.zal;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sw.utils.*;

import java.io.File;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 15.06.11
 * Time: 12:20
 */
public class NarrowRoomTake2 extends GLBaza implements Scene {
    SceneWithShadowRenderer renderer;
    Cube cube;

    Drawable[] drawables;
    Bouncer[] bouncers;
    ShapeCombinations[] shapeCombinations;
    int[] numbersOfCompounds;

    Material cubeMat, tabletopMat;
    float[] lookingAt = {0, 0, 0};
    float cameraNear = 1.4f;
    float cameraFar = 12;
    float[] cameraPos = {0, 0, 5, 1};
    float[] cameraUpV = {0, 1, 0};
    float[] lightPos = {0, 8f, 0, 1};
    float[] lightUpV = {0, 0, 1};
    float lightNear = 1f;
    float lightFar = 100;

    float rotation;
    boolean lit = true;
    boolean rotate = true;
    boolean camera = true;
    boolean objects = true;
    int term = 5000;
    private int numberOfCompounds = 0;
    private float viewRotationY = 0;
    private float viewRotationX = 0;
    private int roomTexture;


    @Override
    protected void init() {
        renderer = new SceneWithShadowRenderer(
                this, 512, cameraNear, cameraFar,
                cameraPos, lookingAt, cameraUpV,
                GL_LIGHT0, lightNear, lightFar,
                lightPos, lookingAt, lightUpV);

        cube = new Cube(true);
        //glActiveTexture(GL_TEXTURE0);
        roomTexture = glGenTextures();
        //glBindTexture(GL_TEXTURE_2D, roomTexture);
        //Utils.texture(new File("tekstury/P5_t.png"));
        cubeMat = new Material(120, new float[][]{
                {0, 0.2f, 0.2f, 1},
                {0, 1, 1, 1},
                {1, 1, 1, 1}
        });
        tabletopMat = new Material(1, new float[][]{
                {0, 0.1f, 0, 1},
                {0, 0.1f, 0, 1},
                {0, 0, 0, 0}
        });

        Random rand = new Random();
        drawables = new Drawable[rand.nextInt(11) + 2];
        bouncers = new Bouncer[drawables.length];
        shapeCombinations = new ShapeCombinations[drawables.length];
        numbersOfCompounds = new int[drawables.length];

        ShapeCombinations[] possibleShapeCombinations = ShapeCombinations.values();

        for (int i = 0; i < drawables.length; ++i) {
            drawables[i] = rand.nextBoolean() ? new Tetrahedron() : new Cube();
            bouncers[i] = new Bouncer(
                    new float[]{0, 0, 0},
                    new float[]{rand.nextFloat(), rand.nextFloat(), rand.nextFloat()},
                    (float) Math.sqrt(2) / 2,
                    4);
            shapeCombinations[i] = possibleShapeCombinations[rand.nextInt(possibleShapeCombinations.length)];
            numbersOfCompounds[i] = rand.nextInt(2) + 1;
        }

    }

    public void drawScene(boolean observerMode) {
        glPushMatrix();
        {
            if (observerMode) {
                glPushMatrix();
                {
                    glScalef(4f, 4f, 4f);
                    tabletopMat.set();
                    cube.draw();
                }
                glPopMatrix();
            }

            ////glActiveTexture(GL_TEXTURE0);
            //glBindTexture(GL11.GL_TEXTURE_2D, roomTexture);
            cubeMat.set();

            if (objects) {
                for (int i = 0; i < drawables.length; ++i) {
                    glPushMatrix();
                    float[] pos = bouncers[i].increment(0.01f);
                    glTranslatef(pos[0], pos[1], pos[2]);
                    glRotatef(rotation * numbersOfCompounds[i], 0, 1, 1);
                    glScalef(0.5f, 0.5f, 0.5f);
                    shapeCombinations[i].draw(drawables[i], numbersOfCompounds[i] + numberOfCompounds);
                    //if (observerMode) textureAggregator.nextTexture();
                    glPopMatrix();
                }
            }
        }
        glPopMatrix();
    }

    public void transformWorld() {
        glRotatef(viewRotationX, 1, 0, 0);
        glRotatef(viewRotationY, 0, 1, 0);
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
                    case Keyboard.KEY_C:
                        camera = !camera;
                        break;
                    case Keyboard.KEY_O:
                        objects = !objects;
                        break;
                    case Keyboard.KEY_LEFT:
                        rotation += 1;
                        break;
                    case Keyboard.KEY_RIGHT:
                        rotation -= 1;
                        break;
                    case Keyboard.KEY_NEXT:
                        numberOfCompounds--;
                        System.out.println(numberOfCompounds);
                        break;
                    case Keyboard.KEY_PRIOR:
                        numberOfCompounds++;
                        System.out.println(numberOfCompounds);
                        break;
                }
            }
        }
        viewRotationY = 360 * (float) (Mouse.getX() - width / 2) / width;
        viewRotationX = 360 * (float) (Mouse.getY() - height / 2) / height;
    }

    @Override
    protected void render() {
        if (camera) {
            renderer.render(lit);
        } else {
            renderer.renderFromLightPerspective(false);
        }
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
        new NarrowRoomTake2().start();
    }
}
