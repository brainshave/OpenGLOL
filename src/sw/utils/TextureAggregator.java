package sw.utils;

import org.lwjgl.opengl.GL11;

import java.io.File;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.glBindTexture;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 11:37
 */
public class TextureAggregator {

    private IntBuffer textures;

    public TextureAggregator(File dir) {
        textures = Utils.textures(dir.listFiles());
    }

    public void resetTexturePointer() {
        textures.rewind();
    }

    public void nextTexture() {
        if (!textures.hasRemaining()) {
            textures.rewind();
        }
        glBindTexture(GL11.GL_TEXTURE_2D, textures.get());
    }

}
