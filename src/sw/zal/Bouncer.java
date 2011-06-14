package sw.zal;

import org.lwjgl.util.vector.Vector3f;

import java.io.PipedOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 18:36
 */
public class Bouncer {
    float[] pos;
    float[] dir;
    float r;
    float border;

    /**
     * @param pos    Starting position
     * @param dir    vector by which pos is incremented
     * @param r      radius of bounding sphere
     * @param border how far walls are from zero?
     */
    public Bouncer(float[] pos, float[] dir, float r, float border) {
        this.pos = pos;
        this.dir = dir;
        this.r = r;
        this.border = border;
    }

    public float[] increment(float factor) {
        float[] newPos = new float[3];

        boolean bounce = false;
        for (int i = 0; i < 3; ++i) {
            newPos[i] = pos[i] + factor * dir[i];

            if (Math.abs(newPos[i]) + r > border) {
                dir[i] = -dir[i];
                bounce = true;
            }
        }
        if(bounce) newPos = increment(factor);
        pos = newPos;
        return pos;
    }
}
