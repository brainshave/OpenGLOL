package sw.utils;

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
public class Material {

    public static final int[] SETTINGS = {GL_AMBIENT, GL_DIFFUSE, GL_SPECULAR};
    FloatBuffer[] settings;
    float shininess;

    public Material(float shininess, float[][] settings) {
        this.shininess = shininess;
        this.settings = new FloatBuffer[SETTINGS.length];
        for(int i = 0; i < SETTINGS.length; ++i) {
            this.settings[i] = bufferFromArray(settings[i]);
        }
    }

    public void set() {
        for (int i = 0; i < SETTINGS.length; ++i) {
            glMaterial(GL_FRONT, SETTINGS[i], settings[i]);
        }
        glMaterialf(GL_FRONT, GL_SHININESS, shininess);
    }
}
