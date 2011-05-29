package sw.cw10;

import org.lwjgl.input.Keyboard;
import sw.utils.GLBaza;
import sw.utils.Utils;

import java.io.File;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 29.05.11
 * Time: 10:40
 */
public class TextEditor extends GLBaza {
    final String IMAGE_FILE = "141.gif";
    final String[] CHAR_MAP = {
            "abcdef",
            "ghijkl",
            "mnopqr",
            "stuvwx",
            "yz()-0",
            "123456",
            "789.:,",
            "'\"?!~ "
    };

    Font font;

    @Override
    protected void init() {
        glClearColor(0, 0, 0, 0);
        glPixelZoom(1, -1);

        try {
            font = new Font(new File(IMAGE_FILE), CHAR_MAP, width, height);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    StringBuilder text = new StringBuilder("To jest edytor tekstu! :)");

    @Override
    protected void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_BACK && text.length() > 0) {
                    text.delete(text.length() - 1, text.length());
                } else if (font.getAvailableChars().contains(Keyboard.getEventCharacter())) {
                    text.append(Keyboard.getEventCharacter());
                }
            }
        }
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        font.draw(0, 0, text.toString(), true);
    }

    @Override
    protected void logic() {
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new TextEditor().start();
    }
}
