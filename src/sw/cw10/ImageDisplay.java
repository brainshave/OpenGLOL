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

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 25.05.11
 * Time: 12:14
 * <p/>
 * Opens image from "input.jpg" file and saves to "output.jpg" after selecting rectangle. Scroll zooms in and out.
 */
public class ImageDisplay extends GLBaza {

    BufferedImage image = null;
    ByteBuffer buff = null;
    float zoom, maxZoom;

    @Override
    protected void init() {
        glClearColor(0, 0, 0, 0);
        glColor3f(1, 1, 0);
        glLineWidth(3);
        try {
            image = ImageIO.read(new File("input.jpg"));
            buff = Utils.imageData(image);
            maxZoom = zoom = Math.min((float) width / image.getWidth(), (float) height / image.getHeight());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(1);
        }
    }

    boolean drag;
    boolean save;
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
                if (drag) {
                    drag = false;
                    save = true;
                }
            }
        }
    }

    @Override
    protected void render() {
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        glRasterPos2f(-image.getWidth() * zoom / width, image.getHeight() * zoom / height);
        glPixelZoom(zoom, -zoom);
        glDrawPixels(image.getWidth(), image.getHeight(), GL_RGBA, GL11.GL_UNSIGNED_BYTE, buff);

        if (drag) {
            float x1 = -1 + 2 * (float) firstCornerX / width;
            float y1 = -1 + 2 * (float) firstCornerY / height;
            float x2 = -1 + 2 * (float) secondCornerX / width;
            float y2 = -1 + 2 * (float) secondCornerY / height;
            glBegin(GL11.GL_LINE_LOOP);
            glVertex2f(x1, y1);
            glVertex2f(x2, y1);
            glVertex2f(x2, y2);
            glVertex2f(x1, y2);
            glEnd();
        }

        glFlush();
    }

    int toInt(byte b) {
        if (b < 0) return b + 256;
        else return (int) b;
    }

    @Override
    protected void logic() {
        if (save) {
            save = false;
            int startX = Math.min(firstCornerX, secondCornerX);
            int startY = Math.min(firstCornerY, secondCornerY);
            int w = Math.abs(firstCornerX - secondCornerX);
            int h = Math.abs(firstCornerY - secondCornerY);
            ByteBuffer tmp = BufferUtils.createByteBuffer(w * h * 4);
            tmp.rewind();
            render(); // re-render before grabbing pixels to eliminate yellow frame from output file.
            glReadPixels(startX, startY, w, h, GL_RGBA, GL11.GL_UNSIGNED_BYTE, tmp);
            BufferedImage frag = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pos = (x + (h - y - 1) * w) * 4;
                    int pix = 0;
                    for (int i = 0; i < 3; ++i) {
                        pix <<= 8;
                        pix |= toInt(tmp.get(pos + i));
                    }
                    frag.setRGB(x, y, pix);
                }
            }
            try {
                ImageIO.write(frag, "JPEG", new File("output.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new ImageDisplay().start();
    }
}
