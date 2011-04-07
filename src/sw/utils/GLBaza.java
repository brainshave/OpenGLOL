package sw.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 *
 * @author Szymon Witamborski
 */
public abstract class GLBaza {

    public final int width, height;
    public final String title;

    public GLBaza(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public GLBaza() {
        width = 800;
        height = 600;
        title = "SW OpenGL";
    }

    protected final void checkKeys(int[] keys, int[] keysStatus) {

        while (Keyboard.next()) {
            int actKey = Keyboard.getEventKey();
            for (int i = 0; i < keys.length; i++) {
                if (actKey == keys[i]) {
                    keysStatus[i] = Keyboard.getEventKeyState() ? 1 : 2;
                }
            }
            boolean repeat = false;
            for (int i = 0; i < keys.length; i++) {
                if (keysStatus[i] == 0) {
                    repeat = true;
                }
            }
            if (!repeat) {
                break;
            }
        }
    }

    protected final void initDisplay() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(width, height));
        Display.setTitle(title);
        Display.create();
    }

    protected final void start() {
        try {
            initDisplay();
            init();
            while (!Display.isCloseRequested()) {
                input();
                logic();
                render();
                Display.update();
            }
        } catch (LWJGLException ex) {
            Logger.getLogger(GLBaza.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract void init();

    protected abstract void input();

    protected abstract void render();

    protected abstract void logic();
}
