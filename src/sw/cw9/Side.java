package sw.cw9;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 04.05.11
 * Time: 22:54
 */
public class Side {
    FloatBuffer buff;
    int density;

    public Side(int density) {
        this.density = density;
        buff = BufferUtils.createFloatBuffer((density + 2) * 6 * 2);
        buff.rewind();

        float[] topNorm = {0, -1, 0};
        buff.put(topNorm).put(new float[]{0,0,0});
        for (int i = 0; i <= density; ++i) {
            double p1step = (2 * Math.PI * i) / density;
            float[] p1 = {(float) Math.cos(p1step), 0, (float) Math.sin(p1step)};
            buff.put(topNorm).put(p1);
        }
        buff.flip();
    }

    public void draw() {
        glInterleavedArrays(GL_N3F_V3F, 0, buff);
        glDrawArrays(GL_TRIANGLE_FAN, 0, density + 2);
    }
}
