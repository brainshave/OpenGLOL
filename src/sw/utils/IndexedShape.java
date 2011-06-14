package sw.utils;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 14:05
 */


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public abstract class IndexedShape extends Shape implements IndexedDrawable {
    public final void draw() {
        glBindBuffer(GL_ARRAY_BUFFER, getBuffer());
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, getIndicesBuffer());
        glInterleavedArrays(getLayout(), 0,0);
        glDrawElements(getMethod(), getIndicesCount(), GL_UNSIGNED_INT, 0);
    }
}
