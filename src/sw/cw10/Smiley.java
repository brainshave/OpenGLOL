package sw.cw10;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sw.utils.GLBaza;
import sw.utils.Utils;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 25.05.11
 * Time: 10:58
 */
public class Smiley extends GLBaza {
    int[] orig = {0x03, 0xc0, 0x0f, 0xf0, 0x1e, 0x78, 0x39, 0x9c, 0x77, 0xee, 0x6f, 0xf6, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x73, 0xce, 0x73, 0xce, 0x3f, 0xfc, 0x1f, 0xf8, 0x0f, 0xf0, 0x03, 0xc0};
    ByteBuffer bb = BufferUtils.createByteBuffer(16 * 16 * 3);

    {
        bb.rewind();
        for (int b : orig) {
            for (int i = 7; i >= 0; --i) {
                byte v = (byte) ((b & (1 << i)) == 0 ? 0 : -1);
                for (int j = 0; j < 3; ++j) {
                    bb.put(v);
                }
            }
        }
        bb.flip();
    }

    @Override
    protected void init() {
        glClearColor(0, 0, 0, 0);
        glColor3f(1, 1, 1);
    }

    float zoom = 1;

    @Override
    protected void input() {
        while (Mouse.next()) {
            zoom += ((float) Mouse.getEventDWheel()) / 100;

            if (Math.abs(16 * zoom) > Math.min(width, height))
                zoom -= ((float) Mouse.getEventDWheel()) / 100;
        }
    }

    @Override
    protected void render() {
        glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        glRasterPos2f(-16 * zoom / width, -16 * zoom / height);
        glPixelZoom(zoom, zoom);
        glDrawPixels(16, 16, GL_RGB, GL11.GL_UNSIGNED_BYTE, bb);
        glFlush();
    }

    @Override
    protected void logic() {
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new Smiley().start();
    }
}
