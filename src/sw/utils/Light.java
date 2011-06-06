package sw.utils;

import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import sun.text.normalizer.IntTrie;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static sw.utils.Utils.bufferFromArray;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 13.04.11
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */
public class Light {

    public static final int[] SETTINGS = {GL_AMBIENT, GL_DIFFUSE, GL_SPECULAR, GL_POSITION};
    FloatBuffer[] settings;
    int name;

    public Light(int name, float[] pos) {
        this(name, new float[][]{{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}, pos});
    }

    public Light(int name, float[][] settings) {
        this.name = name;
        this.settings = new FloatBuffer[SETTINGS.length];
        for (int i = 0; i < SETTINGS.length; ++i) {
            this.settings[i] = bufferFromArray(settings[i]);
        }
    }

    public void on() {
        glEnable(GL_LIGHTING);
        glEnable(name);

        for (int i = 0; i < SETTINGS.length; ++i) {
            glLight(name, SETTINGS[i], settings[i]);
        }
    }

    public void pos() {
        glLight(name, SETTINGS[3], settings[3]);
    }
}
