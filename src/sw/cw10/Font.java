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
import java.util.StringTokenizer;

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
        final int first_display_list = glGenLists(map_size + 2); // +1 for space
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
        displayListsMap.put(' ', first_display_list + map_size);
        displayListsMap.put('|', first_display_list + map_size + 1);

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
            glRasterPos2f(0, 0);
            glDrawPixels(charWidth, charHeight, GL_RGBA, GL_UNSIGNED_BYTE, imgData);
            glTranslatef((float) charWidth * 2 / windowWidth, 0, 0);
            glEndList();
        }
        // space
        glNewList(first_display_list + map_size, GL_COMPILE);
        glTranslatef((float) charWidth * 2 / windowWidth, 0, 0);
        glEndList();
        // cursor
        glNewList(first_display_list + map_size + 1, GL_COMPILE);
        glLineWidth(3);
        glColor3f(1,1,1);
        glBegin(GL_LINES);
        glVertex2f(0, 0);
        glVertex2f(0, -(float) charHeight*2 / windowHeight);
        glEnd();
        glTranslatef((float) charWidth * 2 / windowWidth, 0, 0);
        glEndList();
    }

    public void draw(char c) {
        try {
            glCallList(displayListsMap.get(Character.toLowerCase(c)));
        } catch (NullPointerException e) {
        }
    }

    public void draw(String str) {
        int[] lists = new int[str.length()];
        for (int i = 0; i < str.length(); ++i) {
            try {
                lists[i] = displayListsMap.get(Character.toLowerCase(str.charAt(i)));
            } catch (NullPointerException e) {
            }
        }
        glCallLists(Utils.bufferFromArray(lists));
    }

    private void nextLine() {
        glTranslatef(0, -(float) charHeight * 2 / windowHeight, 0);
    }

    public void draw(int x, int y, String str) {
        glTranslatef((-(float) windowWidth / 2 + x) * 2 / windowWidth,
                ((float) windowHeight / 2 - y) * 2 / windowHeight, 0);
        int max_chars_in_line = (windowWidth - x) / charWidth;
        StringTokenizer tokenizer = new StringTokenizer(str, "\n", true);
        boolean was_empty = false;
        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken();
            int start = -max_chars_in_line;
            do {
                start += max_chars_in_line;
                String subline = line.substring(start, Math.min(start + max_chars_in_line, line.length()));
                glPushMatrix();
                draw(subline);
                glPopMatrix();
                boolean is_empty = "\n".equals(subline);
                if (!is_empty || (was_empty && is_empty)) nextLine();
                was_empty = is_empty;
            } while (line.length() - start > max_chars_in_line);
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
