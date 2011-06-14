package sw.zal;

import org.lwjgl.opengl.GL11;
import sw.utils.Utils;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 10:05
 */
public class Tetrahedron {

    FloatBuffer tetrahedronVerts = Utils.bufferFromArray(new float[]{
            0.5f, 1, 1, 1, 1, 1, 1, 1,
            0, 0, -1, -1, 1, -1, -1, 1,
            1, 0, -1, 1, -1, -1, 1, -1,
            0, 1, 1, -1, -1, 1, -1, -1});
    IntBuffer tetrahedronIndices = Utils.bufferFromArray(new int[]{0, 1, 2, 0, 1, 3, 0, 2, 3, 1, 2, 3});

    IntBuffer textures;

    public Tetrahedron(File dir) {
        this.textures = Utils.textures(dir.listFiles());
    }

    public void setBuffer() {
        glInterleavedArrays(GL_T2F_N3F_V3F, 0, tetrahedronVerts);
    }

    public void draw(boolean texturize) {
        if(texturize) {
            nextTexture();
        }
        glDrawElements(GL_TRIANGLES, tetrahedronIndices);
    }

    void resetTexturePointer() {
        textures.rewind();
    }

    void nextTexture() {
        if (!textures.hasRemaining()) {
            textures.rewind();
        }
        glBindTexture(GL11.GL_TEXTURE_2D, textures.get());
    }

}
