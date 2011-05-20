package sw.cw9;

import org.lwjgl.BufferUtils;
import sw.utils.Material;
import sw.utils.Utils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 04.05.11
 * Time: 21:26
 */
public class Cone {
    FloatBuffer buff;
    int density;
    Side side;

    public Cone(int density) {
        this.density = density;
        buff = BufferUtils.createFloatBuffer(density * 6 * 3);
        side = new Side(density);
        buff.rewind();
        float[] tip = {0, 1, 0};
        float[] zero = {0, 0, 0};
        float[] bottomNorm = {0, -1, 0};
        for (int i = 0; i < density; ++i) {
            double p1step = (2 * Math.PI * i) / density;
            double p2step = (2 * Math.PI * (i + 1)) / density;
            float[] p1 = {(float) Math.cos(p1step), 0, (float) Math.sin(p1step)};
            float[] p2 = {(float) Math.cos(p2step), 0, (float) Math.sin(p2step)};
            float[] tipNorm = Utils.normalize(Utils.vectorProduct(Utils.vector(p1, tip), Utils.vector(tip, p2)));
            float[] p1Norm = Utils.normalize(Utils.vectorProduct(Utils.vector(p2, p1), Utils.vector(p1, tip)));
            float[] p2Norm = Utils.normalize(Utils.vectorProduct(Utils.vector(tip, p2), Utils.vector(p2, p1)));
            buff.put(p1Norm).put(p1).put(p2Norm).put(p2).put(tipNorm).put(tip);
        }
        buff.flip();
    }

    public void draw() {
        glInterleavedArrays(GL_N3F_V3F, 0, buff);
        glDrawArrays(GL_TRIANGLES, 0, density * 3);
        side.draw();
    }
}
