package sw.cw11;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL15;
import sw.cw7.Kula;
import sw.utils.GLBaza;
import sw.utils.Utils;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 05.06.11
 * Time: 13:14
 */
public class SphereMap extends GLBaza {
    int backBuffer;

    int MODEL_COUNT = 4;
    int density = 100;

    IntBuffer models;
    int[] sizes;
    int[] modes;
    int currentModel = 0;

    void initModels() {
        models = BufferUtils.createIntBuffer(MODEL_COUNT);
        sizes = new int[MODEL_COUNT];
        modes = new int[MODEL_COUNT];

        models.rewind();
        glGenBuffers(models);

        // walec
        int cylinderSize = density * 6 * 2;
        int cylinderMode = GL_QUAD_STRIP;
        FloatBuffer cylinder = BufferUtils.createFloatBuffer(cylinderSize);
        cylinder.rewind();
        for (int i = 0; i < density; ++i) {
            float a = (float) (i * 2 * Math.PI / (density - 1));
            float x = (float) Math.cos(a);
            float z = (float) Math.sin(a);
            float[] norm = {x, 0, z};
            float[] v1 = {x, -1, z};
            float[] v2 = {x, 1, z};
            cylinder.put(norm).put(v1).put(norm).put(v2);
        }
        cylinder.rewind();

        // stożek
        int coneSize = (density + 1) * 6;
        int coneMode = GL_TRIANGLE_FAN;
        FloatBuffer cone = BufferUtils.createFloatBuffer(coneSize);
        cone.rewind();
        cone.put(new float[]{0, 1, 0, 0, 1, 0});
        for (int i = 0; i < density; ++i) {
            float a = (float) (i * 2 * Math.PI / (density - 1));
            float x = (float) Math.cos(a);
            float z = (float) Math.sin(a);
            cone.put(new float[]{x, 0, z, x, -1, z});
        }
        cone.rewind();

        // sześcian
        int cubeSize = 6 * 4 * 6;
        int cubeMode = GL_QUADS;
        FloatBuffer cube = BufferUtils.createFloatBuffer(cubeSize);
        cube.rewind();
        int[] magicTransform = {0, 1, 0, 1, 0, 2, 0, 2, 1, 2, 1};
        for (int V = -1; V <= 1; V += 2) {
            float[] vec = {V, V, V};
            float[] norm = {0, 0, V};
            cube.put(norm).put(vec);
            for (int i = 0; i < 11; ++i) {
                int index = magicTransform[i];
                vec[index] = -vec[index];
                if (i == 3) norm = new float[]{0, V, 0};
                else if (i == 7) norm = new float[]{V, 0, 0};
                cube.put(norm).put(vec);
            }
        }
        cube.rewind();

        glBindBuffer(GL_ARRAY_BUFFER, models.get(0));
        glBufferData(GL_ARRAY_BUFFER, cylinder, GL_STATIC_DRAW);
        modes[0] = cylinderMode;
        sizes[0] = cylinderSize / 6;

        glBindBuffer(GL_ARRAY_BUFFER, models.get(1));
        glBufferData(GL_ARRAY_BUFFER, cone, GL_STATIC_DRAW);
        modes[1] = coneMode;
        sizes[1] = coneSize / 6;

        // kula
        Kula kula = new Kula(models.get(2), 4, true); // bindbuffer, bufferdata is done in constructor
        modes[2] = kula.getMode();
        sizes[2] = kula.getCount();

        // sześcian
        glBindBuffer(GL_ARRAY_BUFFER, models.get(3));
        glBufferData(GL_ARRAY_BUFFER, cube, GL_STATIC_DRAW);
        modes[3] = cubeMode;
        sizes[3] = cubeSize / 6;
    }

    void initBackBuffer() {
        backBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, backBuffer);

        FloatBuffer backVerts = Utils.bufferFromArray(new float[]{
                0, 0, -1, -1, -2,
                0, 1, -1, 1, -2,
                1, 1, 1, 1, -2,
                1, 0, 1, -1, -2
        });
        glBufferData(GL15.GL_ARRAY_BUFFER, backVerts, GL15.GL_STATIC_DRAW);
    }

    IntBuffer textures;

    @Override
    protected void init() {
        Utils.initPerspective(this, 1, 30);
        Utils.enable(new int[]{
                GL_DEPTH_TEST, GL_TEXTURE_2D
        });
        gluLookAt(0, 0, 4, 0, 0, 0, 0, 1, 0);

        textures = Tetrahedron.textures(new File[]{new File("tekstury/BG.png"), new File("tekstury/Reflect.png")});
        glTexGenf(GL_S, GL_TEXTURE_GEN_MODE, GL_SPHERE_MAP);
        glTexGenf(GL_T, GL_TEXTURE_GEN_MODE, GL_SPHERE_MAP);

        glClearColor(0, 0, 0, 0);
        glColor3f(1, 1, 1);

        initModels();

        initBackBuffer();
    }

    @Override
    protected void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
                currentModel = (currentModel + 1) % MODEL_COUNT;
            }
        }
    }

    int[] texGens = {GL_TEXTURE_GEN_S, GL_TEXTURE_GEN_T};

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glPushMatrix();
        glScalef(6, 6, 1);
        Utils.disable(texGens);
        glBindTexture(GL_TEXTURE_2D, textures.get(0));

        glBindBuffer(GL_ARRAY_BUFFER, backBuffer);
        glInterleavedArrays(GL_T2F_V3F, 0, 0);
        glDrawArrays(GL_QUADS, 0, 4);

        glPopMatrix();

        glPushMatrix();

        glRotatef(rotation, 0, 1, 1);
        glTranslatef(0.5f, 0, 0);

        Utils.enable(texGens);
        glBindTexture(GL_TEXTURE_2D, textures.get(1));

        glBindBuffer(GL_ARRAY_BUFFER, models.get(currentModel));
        glInterleavedArrays(GL_N3F_V3F, 0, 0);
        glDrawArrays(modes[currentModel], 0, sizes[currentModel]);

        glPopMatrix();

    }

    float rotation = 0;
    int term = 7000;

    @Override
    protected void logic() {
        Utils.sleep60Hz();
        rotation = ((float) (System.currentTimeMillis() % term) / term) * 360;
    }

    public static void main(String[] args) {
        new SphereMap().start();
    }
}
