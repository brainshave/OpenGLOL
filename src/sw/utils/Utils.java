package sw.utils;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 13.04.11
 * Time: 20:19
 */
public class Utils {
    public static IntBuffer bufferFromArray(int[] array) {
        IntBuffer buf = BufferUtils.createIntBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.flip();
        return buf;
    }

    public static FloatBuffer bufferFromArray(float[] array) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.flip();
        return buf;
    }


    public static ByteBuffer bufferFromArray(byte[] array) {
        ByteBuffer buf = BufferUtils.createByteBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.flip();
        return buf;
    }

    public static float[] vector(float[] p1, float[] p2) {
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = p1[i] - p2[i];
        }
        return result;
    }

    public static float length(float[] v) {
        float sum = 0;
        for (float x : v) {
            sum += x * x;
        }
        return (float) Math.sqrt(sum);
    }

    public static float[] normalize(float[] p) {
        float sum = length(p);
        //p = p.clone();
        for (int i = 0; i < p.length; i++) {
            p[i] /= sum;
        }
        return p;
    }

    public static float[] vectorProduct(float[] a, float[] b) {
        return new float[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
    }

    public static void sleep60Hz() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static ByteBuffer imageData(BufferedImage image) {
        ByteBuffer bb = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        bb.rewind();
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int p = image.getRGB(x, y);
                for (int i = 16; i >= 0; i -= 8) {
                    bb.put((byte) ((p >> i) & 0xFF));
                }
                bb.put((byte) -1);
            }
        }
        bb.flip();
        return bb;
    }

    public static ByteBuffer imageDataUpsideDown(BufferedImage image) {
        ByteBuffer bb = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        bb.rewind();
        for (int y = image.getHeight() -1; y >= 0; --y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int p = image.getRGB(x, y);
                for (int i = 16; i >= 0; i -= 8) {
                    bb.put((byte) ((p >> i) & 0xFF));
                }
                bb.put((byte) -1);
            }
        }
        bb.flip();
        return bb;
    }

    public static void initPerspective(int windowWidth, int windowHeight, float near, float far) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float size = 1;
        if (windowWidth > windowHeight) {
            glFrustum(-size * (float) windowWidth / windowHeight,
                    size * (float) windowWidth / windowHeight, -size, size, near, far);
        } else {
            glFrustum(-size, size, -size * (float) windowHeight / windowWidth,
                    size * (float) windowHeight / windowWidth, near, far);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();


    }
    
    public static void initPerspective(GLBaza program, float near, float far) {
        initPerspective(program.width, program.height, near, far);
    }

    public static void enable(int[] enables) {
        for (int flag : enables) {
            glEnable(flag);
        }
    }


    public static void disable(int[] disables) {
        for (int flag : disables) {
            glDisable(flag);
        }
    }

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
        ByteBuffer bb = imageDataUpsideDown(img);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }
}
