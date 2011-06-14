package sw.zal;

import org.lwjgl.input.Keyboard;
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
    Drawable[] drawables;
    TextureAggregator textureAggregator;

    @Override
    protected void init() {
        Utils.initPerspective(this, 1, 100);
        Utils.enable(new int[]{GL_DEPTH_TEST, GL_NORMALIZE, GL_TEXTURE_2D, GL_POLYGON_OFFSET_FILL});

        gluLookAt(0, 0, 6, 0, 0, 0, 0, 1, 0);

        glShadeModel(GL_SMOOTH);
        glColor4f(1, 1, 1, 1);
        glClearColor(0, 0, 0, 0);
        drawables = new Drawable[] {new Tetrahedron(), new Cube()};
        textureAggregator = new TextureAggregator(new File("tekstury"));
    }


    float rotationY;
    float rotationX;
    boolean lit = true;
    boolean rotate = true;
    int numberOfCompounds = 2;

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
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glPushMatrix();
        glRotatef(rotationX, 1, 0, 0);
        glRotatef(rotationY, 0, 1, 0);
        textureAggregator.resetTexturePointer();
        glTranslatef(-3, 0, 0);
        ShapeCombinations.RECURRENT.draw(drawables[0], numberOfCompounds);
        glTranslatef(6, 0, 0);
        ShapeCombinations.RECURRENT.draw(drawables[1], numberOfCompounds);
        textureAggregator.nextTexture();

        glPopMatrix();
    }

    int term = 5000;

    @Override
    protected void logic() {
        Utils.sleep60Hz();
        if (rotate) rotationY = ((float) (System.currentTimeMillis() % term) / term) * 360;
    }

    public static void main(String[] args) {
        new Compounds().start();
    }
}
