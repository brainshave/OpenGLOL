package sw.utils;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 06.06.11
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
public interface Drawable {
    public void draw();

    /**
     * Get buffer name
     * @return buffer name
     */
    public int getBuffer();

    /**
     * Get GL_* constant that's used to draw this Drawable
     * @return one of GL_QUADS, GL_LINES, GL_TRIANGLES or similar
     */
    public int getMethod();

    /**
     * Get count of vertices to draw
     * @return vertices count
     */
    public int getCount();

    /**
     * Get layout of buffer
     * @return values like GL_N3F_V3F
     */
    public int getLayout();
}
