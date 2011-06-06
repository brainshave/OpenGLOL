package sw.utils;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL15.*;

public class Cube extends Shape {
    private int buffer;
    private int count;

    public Cube() {
        create(glGenBuffers());
    }

    public Cube(int buffer) {
        create(buffer);
    }

    protected void create(int buffer) {
        this.buffer = buffer;
        this.count = 4 * 6;
        FloatBuffer cube = BufferUtils.createFloatBuffer(count * 6);
        cube.rewind();

        // magicTransform array says which coordinate is inverted in order to produce next vertex.
        // notice that sequence 0,1,0,1 for example returns to original start point.
        int[] magicTransform = {0, 1, 0, 1, 0, 2, 0, 2, 1, 2, 1};
        // Wall's vertices are produced from start points {-1, -1, -1} and {1,1,1}
        for (int V = -1; V <= 1; V += 2) {
            float[] vec = {V, V, V};
            float[] norm = {0, 0, V};
            cube.put(norm).put(vec);
            for (int i = 0; i < 11; ++i) {
                int index = magicTransform[i];
                vec[index] = -vec[index];

                // these checks are changing normative vectors when current wall changes.
                if (i == 3) norm = new float[]{0, V, 0};
                else if (i == 7) norm = new float[]{V, 0, 0};
                cube.put(norm).put(vec);
            }
        }
        cube.rewind();

        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, cube, GL_STATIC_DRAW);
    }

    public int getBuffer() {
        return buffer;
    }

    public int getMethod() {
        return GL_QUADS;
    }

    public int getCount() {
        return count;
    }
}
