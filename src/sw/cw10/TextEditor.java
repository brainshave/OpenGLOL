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
    String[] FONT_NAMES = {"141.gif", "056.gif", "034.gif"};
    String[][] FONT_MAPS = {
            {
                    "abcdef",
                    "ghijkl",
                    "mnopqr",
                    "stuvwx",
                    "yz()-0",
                    "123456",
                    "789.:,",
                    "'\"?!`_"
            },
            {
                    "abcdefgh",
                    "ijklmnop",
                    "qrstuvwx",
                    "yz:.!,-\"",
                    "   '?)(;"
            },
            {
                    "abcdef",
                    "ghijkl",
                    "mnopqr",
                    "stuvwx",
                    "yz1234",
                    "56789.",
                    ",-:!  ",
                    "() ?0 "
            }
    };

    Font[] fonts = new Font[Math.min(FONT_NAMES.length, FONT_MAPS.length)];

    @Override
    protected void init() {
        glClearColor(0, 0, 0, 0);
        glPixelZoom(1, -1);

        try {
            for (int i = 0; i < fonts.length; ++i) {
                fonts[i] = new Font(new File(FONT_NAMES[i]), FONT_MAPS[i], width, height);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    StringBuilder text = new StringBuilder("To jest edytor\ntekstu! :)\nF1 zmienia czcionke|");

    @Override
    protected void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_F1) {
                    font = (font + 1) % fonts.length;
                } else if (Keyboard.getEventKey() == Keyboard.KEY_BACK && text.length() > 1) {
                    text.deleteCharAt(text.length() - 2);
                } else if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
                    text.insert(text.length() - 1, '\n');
                } else if (activeFont().getAvailableChars().contains(Character.toLowerCase(Keyboard.getEventCharacter()))) {
                    text.insert(text.length() - 1, Keyboard.getEventCharacter());
                }
            }
        }
    }

    int font = 2;
    long start = System.currentTimeMillis();
    boolean blink = true;

    void blink() {
        blink = (((System.currentTimeMillis() - start) % 700) / 350) == 1;
    }

    Font activeFont() {
        return fonts[font];
    }

    @Override
    protected void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        glPushMatrix();
        blink();
        activeFont().draw(0, 0, text.substring(0, text.length() - (blink ? 1 : 0)));
        glRasterPos2f(0, 0);
        glPopMatrix();
    }

    @Override
    protected void logic() {
        Utils.sleep60Hz();
    }

    public static void main(String[] args) {
        new TextEditor().start();
    }
}
