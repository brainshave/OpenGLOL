package sw.zal;

import com.sun.deploy.panel.ITreeNode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sw.utils.*;

import java.io.File;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
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
    Bouncer bouncer = new Bouncer(new float[]{0,0,0}, new float[]{1,0.2f,0.3f}, 1, 4);
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

        drawables = new Drawable[]{new Tetrahedron(), new Cube()};
        textureAggregator = new TextureAggregator(new File("tekstury"));

        material.set();
        bright.on();
    }

    float rotationY;
    float rotationX;
    float viewRotationX;
    float viewRotationY;
    boolean lit = true;
    boolean rotate = true;
    int numberOfCompounds = 2;

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

        glPushMatrix();
        float[] pos = bouncer.increment(0.1f);
        glTranslatef(pos[0], pos[1], pos[2]);
        ShapeCombinations.RECURRENT.draw(drawables[0], numberOfCompounds);
        glPopMatrix();
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glPushMatrix();
        glRotatef(viewRotationX, 1, 0, 0);
        glRotatef(viewRotationY, 0, 1, 0);
        drawScene(true);
        glPopMatrix();

//        glPushMatrix();
//        glRotatef(rotationX, 1, 0, 0);
//        glRotatef(rotationY, 0, 1, 0);
//        textureAggregator.resetTexturePointer();
//        glTranslatef(-3, 0, 0);
//        ShapeCombinations.RECURRENT.draw(drawables[0], numberOfCompounds);
//        glTranslatef(6, 0, 0);
//        ShapeCombinations.RECURRENT.draw(drawables[1], numberOfCompounds);
//        textureAggregator.nextTexture();
//
//        glPopMatrix();
    }

    int term = 5000;

    @Override
    protected void logic() {
        Utils.sleep60Hz();
        if (rotate) rotationY = ((float) (System.currentTimeMillis() % term) / term) * 360;
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
                        rotationY += 10;
                        break;
                    case Keyboard.KEY_RIGHT:
                        rotationY -= 10;
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
