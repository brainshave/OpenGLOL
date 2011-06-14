package sw.utils;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public interface IndexedDrawable extends Drawable {
    public int getIndicesBuffer();
    public int getIndicesCount();
}
