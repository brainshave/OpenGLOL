package sw.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

public abstract class Shape implements Drawable {
    public final void draw() {
        glBindBuffer(GL_ARRAY_BUFFER, getBuffer());
        glInterleavedArrays(getLayout(), 0, 0);
        glDrawArrays(getMethod(), 0, getCount());
    }

    public int getLayout() {
        return GL_N3F_V3F;
    }
}
