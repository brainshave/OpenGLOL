package sw.cw5;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import sw.utils.GLBaza;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 * BAJER: Rozdzielczość przybliżenia powierzchni Beziera można zwiększać
 * i mniejszać strzałkami.
 *
 * Każdy inny klawisz zmienia widok.
 *
 * @author Szymon Witamborski santamon@gmail.com
 */
public class BeSurf extends GLBaza {

    private static final float[][][] P = {
        {{0, 0, 4}, {1, 0, 4}, {2, 0, 4}, {3, 0, 4}, {4, 1, 4}},
        {{0, 0, 3}, {1, 1, 3}, {2, 1, 3}, {3, 1, 3}, {4, 1, 3}},
        {{0, 1, 2}, {1, 2, 2}, {2, 6, 2}, {3, 2, 2}, {4, 1, 2}},
        {{0, 0, 1}, {1, 1, 1}, {2, 1, 1}, {3, 1, 1}, {4, 1, 1}},
        {{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {3, 0, 0}, {4, 1, 0}}
    };
    
    private static final int m = P.length - 1;
    private static final int n = P[0].length - 1;
    // Rozdzielczość siatki powierzchni Beziera:
    private int density = 9;

    private Random rand = new Random();
    private boolean drawBezier = false;

    private FloatBuffer controlPoints;
    private IntBuffer controlPointsIndices;
    private FloatBuffer bezierPoints;
    private IntBuffer bezierIndices;
    
    private void initMatrices(){
        // Inicjalizacja punktów kontrolnych
        controlPoints = BufferUtils.createFloatBuffer(5 * 5 * 6);
        controlPoints.rewind();
        for (float[][] row : P) {
            for (float[] point : row) {
                addRandomColor(controlPoints);
                controlPoints.put(point);
            }
        }
        controlPoints.flip();

        // Inicjalizacja kolejności punktów kontrolnych
        IntBuffer grid = gridIndices(5);
        controlPointsIndices = BufferUtils.createIntBuffer(25 + grid.capacity());
        controlPointsIndices.rewind();
        // dodajemy siatkę:
        controlPointsIndices.put(grid);

        // a później przekątne:
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 4; ++col) {
                controlPointsIndices.put(row * 5 + col).put((row+1)*5 + col +1);
            }
        }
        controlPointsIndices.flip();

        // Inicjalizacja punktów powierzchni beziera
        recalcBezier(density);
    }

    protected void recalcBezier(int size) {
        float step = 1f / (size - 1);
        bezierPoints = BufferUtils.createFloatBuffer(size * size * 6);
        bezierPoints.rewind();
        for (int ui= 0; ui < size; ui++) {
            float u = step * ui;
            for (int vi= 0; vi < size; vi++) {
                float v = step * vi;
                addRandomColor(bezierPoints);
                for (int i = 0; i < 3; ++i) {
                    float sum = 0;
                    for (int j = 0; j <= m; j++) {
                        for (int k = 0; k <= n; k++) {
                            sum += P[j][k][i] * B(j, m, u) * B(k, n, v);
                        }
                    }
                    bezierPoints.put(sum);
                }
            }
        }
        bezierPoints.flip();

        bezierIndices = gridIndices(size);
    }

    @Override
    protected void init() {
        initMatrices();

        // Wygładzanie
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 1.5, 20);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(5, 8, 5, 0, 0, 0, 0, 1, 0);
    }

    @Override
    protected void input() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch(Keyboard.getEventKey()) {
                    case Keyboard.KEY_UP:
                        density = density * 2 - 1;
                        recalcBezier(density);
                        break;
                    case Keyboard.KEY_DOWN:
                        density = density / 2 + 1;
                        recalcBezier(density);
                        break;
                    default:
                        drawBezier = !drawBezier;
                }
            }
        }
    }

    @Override
    protected void render() {
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        glTranslatef(2, 0, 2);
        glRotatef(1, 0, 1, 0);
        glTranslatef(-2, 0, -2);
        if (drawBezier) {
            glInterleavedArrays(GL_C3F_V3F, 0, bezierPoints);
            glDrawElements(GL_LINES, bezierIndices);
        } else {
            glInterleavedArrays(GL_C3F_V3F, 0, controlPoints);
            glDrawElements(GL_LINES, controlPointsIndices);
        }
    }

    @Override
    protected void logic() {
        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new BeSurf().start();
    }

    // Tworzy IntBuffer zawierający indeksy punktów dla GL_LINES do narysowania
    // siatki o danym rozmiarze.
    public final static IntBuffer gridIndices (int size) {
        IntBuffer indices = BufferUtils.createIntBuffer(2 * size * 2 * size);
        indices.rewind();

        // linie pionowe
        for (int row = 0; row < size - 1; ++row) {
            for (int col = 0; col < size; ++col) {
                indices.put(row * size + col).put((row+1) * size + col);
            }
        }

        // linie poziome
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size - 1; ++col) {
                indices.put(row * size + col).put(row * size + col + 1);
            }
        }

        indices.flip();
        return indices;
    }

    protected void addRandomColor(FloatBuffer buff) {
        buff.put(rand.nextFloat()).put(rand.nextFloat()).put(rand.nextFloat());
    }

    public static final float newton(int n, int k) {
        return fact[n] / (fact[k] * fact[n - k]);
    }

    public static final float B(int j, int m, float u) {
        return newton(m, j) * (float) (Math.pow(u, j) * Math.pow(1f - u, m - j));
    }

    // Tablica silni
    private static final float fact[] = new float[5];
    // inicjalizacja tablicy silni:
    static {
        fact[0] = 1;
        for (int i = 1; i < fact.length; i++) {
            fact[i] = fact[i - 1] * i;
        }
    }
}
