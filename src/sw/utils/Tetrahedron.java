package sw.utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_T2F_N3F_V3F;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 10:05
 */
public class Tetrahedron extends IndexedShape {

    final int buffer;
    final int count;
    final int indicesBuffer;
    final int indicesCount;

    public Tetrahedron(int buffer, int indicesBuffer) {
        this.buffer = buffer;
        this.count = 4 * 3;
        this.indicesBuffer = indicesBuffer;
        FloatBuffer tetrahedronVerts = Utils.bufferFromArray(new float[]{
                0.5f, 1, 1, 1, 1, 1, 1, 1,
                0, 0, -1, -1, 1, -1, -1, 1,
                1, 0, -1, 1, -1, -1, 1, -1,
                0, 1, 1, -1, -1, 1, -1, -1});

        int[] indices = {0, 1, 2, 0, 1, 3, 0, 2, 3, 1, 2, 3};
        IntBuffer tetrahedronIndices = Utils.bufferFromArray(indices);
        this.indicesCount = indices.length;

        tetrahedronIndices.rewind();
        tetrahedronVerts.rewind();
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, tetrahedronVerts, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, tetrahedronIndices, GL_STATIC_DRAW);
    }

    public Tetrahedron() {
        this(glGenBuffers(), glGenBuffers());
    }

    public int getBuffer() {
        return buffer;
    }

    public int getMethod() {
        return GL_TRIANGLES;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int getLayout() {
        return GL_T2F_N3F_V3F;
    }

    public int getIndicesBuffer() {
        return indicesBuffer;
    }

    public int getIndicesCount() {
        return indicesCount;
    }
}
