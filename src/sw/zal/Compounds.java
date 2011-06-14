package sw.zal;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import sun.rmi.runtime.NewThreadAction;
import sw.utils.*;

import java.io.File;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 09:22
 */
public class Compounds extends GLBaza {
    Cube room;
    int roomTexture;
    Drawable[] drawables;
    Bouncer[] bouncers;
    ShapeCombinations[] shapeCombinations;
    int[] numbersOfCompounds;
    TextureAggregator textureAggregator;
    Light bright;
    Material material;

    float[] lightPos = {0, 4, 0, 1};

    @Override
    protected void init() {
        Utils.initPerspective(this, 0.7f, 12);
        Utils.enable(new int[]{GL_DEPTH_TEST, GL_NORMALIZE, GL_TEXTURE_2D, GL_POLYGON_OFFSET_FILL, GL_ALPHA_TEST});

        gluLookAt(0, 0, -4.3f, 0, 0, 0, 0, 1, 0);

        glShadeModel(GL_SMOOTH);
        glColor4f(1, 1, 1, 1);
        glClearColor(0, 0, 0, 0);
        bright = new Light(GL_LIGHT0, lightPos);
        material = new Material(1, new float[][]{
                {0.2f, 0.2f, 0.2f, 0.2f},
                {0.7f, 0.7f, 0.7f, 0.7f},
                {0, 0, 0, 0}
        });
        room = new Cube(true);
        roomTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, roomTexture);
        Utils.texture(new File("tekstury/P5_t.png"));

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
            numbersOfCompounds[i] = rand.nextInt(4) + 1;
        }

        textureAggregator = new TextureAggregator(new File("tekstury"));

        material.set();
        bright.on();
    }

    float rotation;
    float rotationX;
    float viewRotationX;
    float viewRotationY;
    boolean lit = true;
    boolean rotate = true;
    int numberOfCompounds = 0;

    private void drawScene(boolean doTextures) {

        glPushMatrix();
        glScalef(4f, 4f, 4f);
        if (doTextures) glBindTexture(GL_TEXTURE_2D, roomTexture);
        room.draw();
        glPopMatrix();

        if (doTextures) {
            textureAggregator.resetTexturePointer();
            textureAggregator.nextTexture();
        }

        for (int i = 0; i < drawables.length; ++i) {
            glPushMatrix();
            float[] pos = bouncers[i].increment(0.1f);
            glTranslatef(pos[0], pos[1], pos[2]);
            glRotatef(rotation, 0, 1, 1);
            glScalef(0.5f, 0.5f, 0.5f);
            shapeCombinations[i].draw(drawables[i], numbersOfCompounds[i] + numberOfCompounds);
            if(doTextures) textureAggregator.nextTexture();
            glPopMatrix();
        }
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glPushMatrix();
        glRotatef(viewRotationX, 1, 0, 0);
        glRotatef(viewRotationY, 0, 1, 0);
        drawScene(true);
        glPopMatrix();
    }

    int term = 4000;

    @Override
    protected void logic() {
        Utils.sleep60Hz();
        if (rotate) rotation = ((float) (System.currentTimeMillis() % term) / term) * 360;
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
                        rotation += 10;
                        break;
                    case Keyboard.KEY_RIGHT:
                        rotation -= 10;
                        break;
                    case Keyboard.KEY_UP:
                        rotationX += 10;
                        break;
                    case Keyboard.KEY_DOWN:
                        rotationX -= 10;
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


    public static void main(String[] args) {
        new Compounds().start();
    }
}
