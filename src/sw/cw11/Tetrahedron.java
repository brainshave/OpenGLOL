package sw.cw11;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sw.utils.GLBaza;
import sw.utils.Light;
import sw.utils.Material;
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
 */
public class Tetrahedron extends GLBaza {
    Light light;
    Material material;

    FloatBuffer tetrahedronVerts = Utils.bufferFromArray(new float[]{
            0.5f, 1, 1, 1, 1, 1, 1, 1,
            0, 0, -1, -1, 1, -1, -1, 1,
            1, 0, -1, 1, -1, -1, 1, -1,
            0, 1, 1, -1, -1, 1, -1, -1});
    IntBuffer tetrahedronIndices = Utils.bufferFromArray(new int[]{0, 1, 2, 0, 1, 3, 0, 2, 3, 1, 2, 3});

    IntBuffer textures;

    public static IntBuffer textures(File[] files) {
        IntBuffer ts = BufferUtils.createIntBuffer(files.length);
        ts.rewind();
        glEnable(GL_TEXTURE_2D);

        glGenTextures(ts);
        for (int i = 0; i < files.length; ++i) {
            glBindTexture(GL_TEXTURE_2D, ts.get(i));
            texture(files[i]);
        }

        ts.limit(files.length);
        return ts;
    }

    public static void texture(File file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ByteBuffer bb = Utils.imageDataUpsideDown(img);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);
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
        gluLookAt(0, 0, 3, 0, 0, 0, 0, 1, 0);

        glEnable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);

        textures = textures(new File("tekstury").listFiles());

        light = new Light(GL_LIGHT0, new float[][]{{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}, {0, 10, 5, 1}});
        material = new Material(120, new float[][]{{0.1f, 0.1f, 0.1f, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}});
        light.on();
        material.set();
        glInterleavedArrays(GL11.GL_T2F_N3F_V3F, 0, tetrahedronVerts);
    }

    void nextTexture() {
        if (!textures.hasRemaining()) {
            textures.rewind();
        }
        glBindTexture(GL11.GL_TEXTURE_2D, textures.get());
    }

    @Override
    protected void input() {
        while (Mouse.next()) {
            nextTexture();
        }
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //light.on();
        glRotatef(1, 0, 1, 0);

        glDrawElements(GL_TRIANGLES, tetrahedronIndices);
    }


    @Override
    protected void logic() {
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new Tetrahedron().start();
    }
}
