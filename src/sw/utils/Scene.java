package sw.utils;

import sun.reflect.generics.tree.VoidDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 15.06.11
 * Time: 10:12
 */
public interface Scene {

    /**
     * Method to implement of drawing scene
     * @param observerMode if true perform transformations on view and use textures in client code.
     */
    public void drawScene(boolean observerMode);
    public void transformWorld();
    public int getWidth();
    public int getHeight();
}
