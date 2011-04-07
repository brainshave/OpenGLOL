package sw.cw2.zad5;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.GLU;
import sw.utils.GLBaza;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Szymon Witamborski
 */
public class Napkins extends GLBaza {

    protected final static int[] KEYS = {Keyboard.KEY_SPACE};
    protected final int[] keysStatus = new int[KEYS.length];
    private boolean animation = false;
    private int oldKeyValue = 0;
    private float degree = 0;

    @Override
    protected void init() {
        // ustawienia wygladzania
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        if (width > height) {
            GLU.gluOrtho2D(-(float) width / height,
                    (float) width / height, -1, 1);
        } else {
            GLU.gluOrtho2D(-1, 1, -(float) height / width,
                    (float) height / width);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    @Override
    protected void input() {
        checkKeys(KEYS, keysStatus);
        if (oldKeyValue != 1 && keysStatus[0] == 1) {
            animation = !animation;
        }
        oldKeyValue = keysStatus[0];
    }

    @Override
    protected void render() {
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);

        glColor3f(1, 1, 1);

        if (animation) {
            degree += 1;
            if (degree >= 360) {
                degree = 0;
            }
        }

        glLoadIdentity();
        glViewport(0, height / 2, width / 2, height / 2);
        glRotatef(degree, 0, 0, 1);
        rosette(0.2f, 10, 10);

        glLoadIdentity();
        glViewport(width / 2, height / 2, width / 2, height / 2);
        glRotatef(degree, 0, 0, 1);
        rosette(0.4f, 6, 10);

        glLoadIdentity();
        glViewport(0, 0, width / 2, height / 2);
        glRotatef(degree, 0, 0, 1);
        rosette(0.5f, 4, 12);

        glLoadIdentity();
        glViewport(width / 2, 0, width / 2, height / 2);
        glRotatef(degree, 0, 0, 1);
        rosette(0.5f, 5, 10);

        glFlush();
        try {
            // sleep coby nie usmażyć procesora. daje ok. 60Hz
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    protected void polygon(float a, int n) {
        final float alpha = (float) (2 * Math.PI / n);
        float d = (float) (a / (2 * Math.sin(alpha / 2)));

        glTranslatef(-d, 0, 0);
        final float y = 0;
        final float x = d;

        float actAlpha = 0;
        float actX = x, actY = y;

        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < n; i++) {
            glVertex2f(actX, actY);
            actAlpha += alpha;
            actX = (float) (x * Math.cos(actAlpha) - y * Math.sin(actAlpha));
            actY = (float) (x * Math.sin(actAlpha) + y * Math.cos(actAlpha));
        }
        glEnd();
        glTranslatef(d, 0, 0);
    }

    protected void rosette(float a, int n, int m) {
        final float alpha = 360f / m;
        for (int i = 0; i < m; i++) {
            polygon(a, n);
            glRotatef(alpha, 0, 0, 1);
        }
    }

    //  protected void
    @Override
    protected void logic() {
    }

    public static void main(String[] args) {
        new Napkins().start();
    }
}
