package sw.zal;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 14.06.11
 * Time: 10:41
 */

import sw.utils.Drawable;

import static org.lwjgl.opengl.GL11.*;

public enum TetrahedronCombinations {
    SIMPLE {
        @Override
        public void draw(Drawable shape, int n) {
            glPushMatrix();
            float deg = 180f / (float) n;

            for (int x = 0; x < n; ++x) {
                shape.draw();
                glRotatef(deg, 1, 0, 0);
            }

            glRotatef(90, 0, 1, 0);

            for (int x = 0; x < n; ++x) {
                shape.draw();
                glRotatef(deg, 1, 0, 0);
            }

            glRotatef(90, 0, 0, 1);
            for (int x = 0; x < n; ++x) {
                shape.draw();
                glRotatef(deg, 1, 0, 0);
            }

            glPopMatrix();
        }
    },
    RECURRENT {
        @Override
        public void draw(Drawable shape, int n) {
            glPushMatrix();
            n = n * 2 - 1;
            float deg = 180f / (float) n;

            for (int z = 0; z < n; ++z) {
                glPushMatrix();
                for (int y = 0; y < n; ++y) {
                    glPushMatrix();
                    for (int x = 0; x < n; ++x) {
                        shape.draw();
                        glRotatef(deg, 1, 0, 0);
                    }
                    glPopMatrix();
                    glRotatef(deg, 0, 1, 0);
                }
                glPopMatrix();
                glRotatef(deg, 0, 0, 1);
            }

            glPopMatrix();
        }
    };

    public abstract void draw(Drawable shape, int n);
}
