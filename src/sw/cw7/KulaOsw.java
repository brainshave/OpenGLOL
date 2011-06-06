package sw.cw7;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import sw.utils.GLBaza;
import sw.utils.Sphere;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: Szymon Witamborski
 * Date: 06.04.11
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public class KulaOsw extends GLBaza {

    public static final int GLEBOKOSC_REKURENCJI = 7;
    public static final boolean OSOBNE_WEKTORY_NORMALNE = true;
    public static final boolean OBETNIJ_POL_KULI = true;

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

    public static DoubleBuffer bufferFromArray(double[] array) {
        DoubleBuffer buf = BufferUtils.createDoubleBuffer(array.length);
        buf.rewind();
        buf.put(array);
        buf.flip();
        return buf;
    }

    @Override
    protected void init() {
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 2, 7);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(2.1f, 2.1f, 2.1f, 0, 0, 0, 0, 1, 0);

        glEnable(GL_DEPTH_TEST);
        glShadeModel(GL_SMOOTH);
        setupLights();
        if (OBETNIJ_POL_KULI) {
            setupClipPlane();
            glLightModelf(GL_LIGHT_MODEL_TWO_SIDE, 1);
        }

        glColor3f(0, 0, 0);
        glClearColor(0, 0, 0, 0);

        sphere = new Sphere(GLEBOKOSC_REKURENCJI, OSOBNE_WEKTORY_NORMALNE);

        setupMaterial(ball_material, ball_shininess);
    }

    DoubleBuffer clip = bufferFromArray(new double[]{1, -1, -1, 0});

    private void setupClipPlane() {
        glClipPlane(GL_CLIP_PLANE0, clip);
        glEnable(GL_CLIP_PLANE0);
    }

    private static int[] LIGHT_SETTINGS = {GL_AMBIENT, GL_DIFFUSE, GL_SPECULAR, GL_POSITION};

    FloatBuffer[] light0 = {
            bufferFromArray(new float[]{1, 0, 0, 1}), // ambient
            bufferFromArray(new float[]{1, 0, 0, 1}), // diffuse
            bufferFromArray(new float[]{1, 1, 1, 1}), // specular
            bufferFromArray(new float[]{-20, 20, 20, 1}) // position
    };

    FloatBuffer[] light1 = {
            bufferFromArray(new float[]{0, 0, 1, 1}), // ambient
            bufferFromArray(new float[]{0, 0, 1, 1}), // diffuse
            bufferFromArray(new float[]{1, 1, 1, 1}), // specular
            bufferFromArray(new float[]{20, 20, -20, 1}) // position
    };


    public void setupLight(int light, FloatBuffer[] settings) {
        glEnable(light);
        for (int i = 0; i < LIGHT_SETTINGS.length; ++i) {
            glLight(light, LIGHT_SETTINGS[i], settings[i]);
        }
    }

    public void setupLights() {
        glEnable(GL_LIGHTING);
        setupLight(GL_LIGHT0, light0);
        setupLight(GL_LIGHT1, light1);
    }

    private static int[] MATERIAL_SETTINGS = {GL_AMBIENT, GL_DIFFUSE, GL_SPECULAR};
    FloatBuffer[] ball_material = {
            bufferFromArray(new float[]{0, 0, 0, 1}), //ambient
            bufferFromArray(new float[]{1, 1, 1, 1}), //diffuse
            bufferFromArray(new float[]{1, 1, 1, 1}), //specular
    };

    float ball_shininess = 100;

    public void setupMaterial(FloatBuffer[] material, float shininess) {
        for (int i = 0; i < MATERIAL_SETTINGS.length; ++i) {
            glMaterial(GL_FRONT_AND_BACK, MATERIAL_SETTINGS[i], material[i]);
        }
        glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, shininess);
    }


    @Override
    protected void input() {
        while (Mouse.next()) {
            if (Mouse.isButtonDown(0)) {
                azimuth0 += Mouse.getDX();
                elevation0 -= Mouse.getDY();
            } else if (Mouse.isButtonDown(1)) {
                azimuth1 += Mouse.getDX();
                elevation1 -= Mouse.getDY();
            }
        }
    }

    Sphere sphere;

    float azimuth0 = 0;
    float azimuth1 = 0;
    float elevation0 = 0;
    float elevation1 = 0;

    void rotateLight(int light, FloatBuffer[] buff, float azimuth, float elevation) {
        glPushMatrix();
        glRotatef(azimuth, 0, 1, 0);
        glRotatef(elevation, 1, 0, 0);
        glLight(light, GL_POSITION, buff[3]);
        glPopMatrix();
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        rotateLight(GL_LIGHT0, light0, azimuth0, elevation0);
        rotateLight(GL_LIGHT1, light1, azimuth1, elevation1);

        glPushMatrix();
        sphere.draw();
        glPopMatrix();
        glFlush();
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new KulaOsw().start();
    }

}
