package sw.cw11;

import org.lwjgl.opengl.GL11;
import sw.utils.GLBaza;
import sw.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 01.06.11
 * Time: 13:40
 * To change this template use File | Settings | File Templates.
 */
public class Textures extends GLBaza {
    FloatBuffer tetrahedronVerts = Utils.bufferFromArray(new float[]{1, 1, 1, -1, -1, 1, -1, 1, -1, 1, -1, -1});
    IntBuffer tetrahedronIndices = Utils.bufferFromArray(new int[] { 0,1,2, 0,1,3, 0,2,3, 1,2,3});

    File textureFile = new File("tekstury/M4_t.png");
    ByteBuffer texture;

    void initTexture() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(textureFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        texture = Utils.imageData(img);

        glEnable(GL_TEXTURE_2D);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    @Override
    protected void init() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 1, 30);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, 1, 30);
        }
        glMatrixMode(GL_MODELVIEW);
        gluLookAt(0, 0, 1, 0, 0, 0, 0, 1, 0);

        glEnable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);

        initTexture();
    }

    @Override
    protected void input() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glRotatef(1,0,0,1);
        glPushMatrix();

        glTranslatef(0.1f, -0.5f, 0);

        glBegin(GL_TRIANGLES);
        {
            glTexCoord2f(0.5f, 1);
            glVertex2f(0.5f, 1);

            glTexCoord2f(0, 0);
            glVertex2f(0, 0);

            glTexCoord2f(1, 0);
            glVertex2f(1, 0);
        }
        glEnd();

        glTranslatef(-1.2f, 0, 0);

        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2f(0, 1);

            glTexCoord2f(1, 1);
            glVertex2f(1, 1);

            glTexCoord2f(1, 0);
            glVertex2f(1, 0);

            glTexCoord2f(0, 0);
            glVertex2f(0, 0);
        }
        glEnd();

        glPopMatrix();
    }


    @Override
    protected void logic() {
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new Textures().start();
    }
}
