package sw.cw10;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sw.utils.GLBaza;
import sw.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glFlush;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 25.05.11
 * Time: 12:14
 */
public class ImageDisplay extends GLBaza {

    ByteBuffer imageData(BufferedImage image) {
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

    BufferedImage image = null;
    ByteBuffer buff = null;
    float zoom, maxZoom;

    @Override
    protected void init() {
        glClearColor(0, 0, 0, 0);
        glColor3f(1, 1, 0);
        glLineWidth(3);
        try {
            image = ImageIO.read(new File("logon.jpg"));
            buff = imageData(image);
            maxZoom = zoom = Math.min((float) width / image.getWidth(), (float) height / image.getHeight());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
    }

    boolean drag;
    int firstCornerX = 0;
    int firstCornerY = 0;
    int secondCornerX = 0;
    int secondCornerY = 0;

    @Override
    protected void input() {
        while (Mouse.next()) {
            zoom = Math.max(0.001f, Math.min(zoom + ((float) Mouse.getEventDWheel() / 2000), maxZoom));
            if (Mouse.isButtonDown(0)) {
                if (!drag) {
                    drag = true;
                    secondCornerX = firstCornerX = Mouse.getEventX();
                    secondCornerY = firstCornerY = Mouse.getEventY();
                } else {
                    secondCornerX = Mouse.getX();
                    secondCornerY = Mouse.getY();
                }
            } else {
                drag = false;
            }
        }
    }

    @Override
    protected void render() {
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        glRasterPos2f(image.getWidth() * zoom / width, image.getHeight() * zoom / height);
        glPixelZoom(-zoom, -zoom);
        glDrawPixels(image.getWidth(), image.getHeight(), GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff);

        if(drag) {
            float x1 = -1+2*(float)firstCornerX/width;
            float y1 = -1+2*(float)firstCornerY/height;
            float x2 = -1+2*(float)secondCornerX/width;
            float y2 = -1+2*(float)secondCornerY/height;
            glBegin(GL11.GL_LINE_LOOP);
            glVertex2f(x1, y1);
            glVertex2f(x2, y1);
            glVertex2f(x2, y2);
            glVertex2f(x1, y2);
            glEnd();
        }

        glFlush();
    }

    @Override
    protected void logic() {
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new ImageDisplay().start();
    }
}
