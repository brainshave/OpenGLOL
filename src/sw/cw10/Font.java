package sw.cw10;

import sw.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 29.05.11
 * Time: 11:15
 */
public class Font {
    private int windowWidth;
    private int windowHeight;

    static boolean checkCharMap(String[] char_map) {
        int row_len = char_map[0].length();
        for (int i = 1; i < char_map.length; ++i) {
            if (char_map[i].length() != row_len) return false;
        }
        return true;
    }

    Map<Character, Integer> displayListsMap = new HashMap<Character, Integer>();
    private int charWidth;
    private int charHeight;

    public Font(File file, String[] char_map, int windowWidth, int windowHeight) throws Exception {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        if (!checkCharMap(char_map)) throw new Exception("Character map doesn't have equal column counts in each row.");

        int map_size = char_map.length * char_map[0].length();
        final int first_display_list = glGenLists(map_size);
        if (first_display_list == 0) throw new Exception("Can't create display lists.");

        // Setting the "character -> display list index" map
        {
            int display_list_index = first_display_list;
            for (String row : char_map) {
                for (char c : row.toCharArray()) {
                    displayListsMap.put(c, display_list_index);
                    ++display_list_index;
                }
            }
        }

        BufferedImage img;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            throw new Exception("Can't load font. Exiting.", e);
        }

        ByteBuffer imgData = Utils.imageData(img);

        int chars_in_row = char_map[0].length();

        charWidth = img.getWidth() / chars_in_row;
        charHeight = img.getHeight() / char_map.length;

        for (int display_list_index = first_display_list, skip_chars = 0;
             display_list_index < first_display_list + map_size;
             ++display_list_index, skip_chars += 1) {
            glNewList(display_list_index, GL_COMPILE);
            glPixelStorei(GL_UNPACK_ROW_LENGTH, img.getWidth());
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, (skip_chars % chars_in_row) * charWidth);
            glPixelStorei(GL_UNPACK_SKIP_ROWS, (skip_chars / chars_in_row) * charHeight);
            glDrawPixels(charWidth, charHeight, GL_RGBA, GL_UNSIGNED_BYTE, imgData);
            glEndList();
        }
    }

    public void draw(char c) {
        try {
            glCallList(displayListsMap.get(Character.toLowerCase(c)));
        } catch (NullPointerException e) {
        }
    }

    public void draw(int x, int y, String str, boolean wrap) {
        int xoff = x;
        int yoff = y;
        for (char c : str.toCharArray()) {
            switch (c) {
                case '\n':
                    yoff += charHeight;
                    xoff = x;
                    break;
                case ' ':
                    xoff += charWidth;
                    break;
                default:
                    glRasterPos2f(
                            (-(float) windowWidth / 2 + xoff) * 2 / windowWidth,
                            ((float) windowHeight / 2 - yoff) * 2 / windowHeight
                    );
                    draw(c);
                    xoff += charWidth;
                    if (wrap && xoff + charWidth > windowWidth) {
                        yoff += charHeight;
                        xoff = x;
                    }
            }
        }
    }

    public Set<Character> getAvailableChars() {
        return displayListsMap.keySet();
    }


    public int getCharWidth() {
        return charWidth;
    }

    public int getCharHeight() {
        return charHeight;
    }
}
