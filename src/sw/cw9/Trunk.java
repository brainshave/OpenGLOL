package sw.cw9;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 04.05.11
 * Time: 22:04
 */
public class Trunk {
    FloatBuffer buff;
    Side side;

    int density;

    public Trunk(int density) {
        this.density = density;
        buff = BufferUtils.createFloatBuffer(density * 6 * 4);
        side = new Side(density);

        buff.rewind();
        for (int i = 0; i < density; ++i) {
            double p1step = (2 * Math.PI * i) / density;
            double p2step = (2 * Math.PI * (i + 1)) / density;
            double normStep = (2 * Math.PI * (i + 0.5)) / density;
            float[] p1 = {(float) Math.cos(p1step), 0, (float) Math.sin(p1step)};
            float[] p2 = {(float) Math.cos(p2step), 0, (float) Math.sin(p2step)};
            float[] norm = {(float) Math.cos(normStep), 0, (float) Math.sin(normStep)};
            buff.put(norm).put(p1).put(norm).put(p2);
            p1[1] = 1;
            p2[1] = 1;
            buff.put(norm).put(p2).put(norm).put(p1);
        }
        buff.flip();


    }

    public void draw() {
        glInterleavedArrays(GL_N3F_V3F, 0, buff);
        glDrawArrays(GL_QUADS, 0, density * 4);
        side.draw();
        glPushMatrix();
        glTranslatef(0, 1, 0);
        glRotatef(180, 1, 0, 0);
        side.draw();
        glPopMatrix();
    }
}
