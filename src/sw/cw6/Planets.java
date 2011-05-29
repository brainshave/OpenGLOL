/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sw.cw6;

import org.lwjgl.util.glu.Sphere;
import sw.utils.GLBaza;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

/**
 *
 * @author Szymon Witamborski
 */
public class Planets extends GLBaza {

    Sphere sphere = new Sphere();
    long start;

    @Override
    protected void init() {  // Wygładzanie
//        glEnable(GL_LINE_SMOOTH);
//        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST); // brak efektu na moim kompie
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glMatrixMode(GL_PROJECTION); // chcemy zrobić coś na macierzy projekcji
        glLoadIdentity();
        float size = 1;
        if (width > height) {
            glFrustum(-size * (float) width / height,
                    size * (float) width / height, -size, size, 2, 10);
        } else {
            glFrustum(-size, size, -size * (float) height / width,
                    size * (float) height / width, -size, size);
        }
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt(3, 1, 0f, 0, 0, 0, 0, 1, 0);

        glEnable(GL_DEPTH_TEST);
        start = System.currentTimeMillis();
    }

    protected final static double SPEED = 0.05;

    protected final double active(double lambda, double t) {
        return 1.0 / (1.0 + Math.exp(-lambda * (2*t - 1)));
    }

    protected float[] position(double a, double e, double T, float[] centre, double lambda) {
        float[] xy = new float[2];
        double b = Math.sqrt(a * a - e * e * a * a);
        T /= SPEED;
        double t = (double) ((System.currentTimeMillis() - start) % T) / T;
        double min = active(lambda, 0);
        t = (active(lambda, t) - min) / (1-2*min) - 0.25;
        xy[0] = (float) (a * Math.sin(Math.PI * 2 * t) - a * e + centre[0]);
        xy[1] = (float) (b * Math.cos(Math.PI * 2 * t) + centre[1]);
        return xy;
    }

    protected float[] drawPlanet(double a, double e, double T, float R, float r, float g, float b) {
        return drawPlanet(a,e,T,R,r,g,b,new float[]{0,0}, 2);
    }

    protected float[] drawPlanet(double a, double e, double T, float R, float r, float g, float b, float[] centre, double lambda) {
        float[] pos = position(a, e, T, centre, lambda);
        glPushMatrix();
        glTranslatef(pos[1],0, pos[0]);
        glRotatef(90, 1, 0, 0);
        glRotatef((System.currentTimeMillis() - start)/10, 0,0,1);
        glColor3f(r,g,b);
        glPolygonMode(GL_BACK, GL_FILL);
        sphere.draw(R, 10, 5);
        glPolygonMode(GL_FRONT, GL_LINE);
        glColor3f(.5f,.5f,.5f);
        sphere.draw(R, 20, 10);
        glPopMatrix();
        return pos;
    }

    @Override
    protected void render() {
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawPlanet(0, 0, 1, 0.2f, 1, 1, 0);
        drawPlanet(0.39, 0.2056, 87.97, 0.05f, 0,0,1); // Merkury
        drawPlanet(0.72, 0.0068, 224.70, 0.07f, 1,0,1); // Wenus
        float[] earthPos = drawPlanet(1, 0.0167, 365, 0.1f, 0,1,0); // Ziemia
        drawPlanet(0.2f, 0, 100, 0.03f, 0.5f, 0.5f, 0.5f, earthPos, 1); // Księżyc

        //planeta testowa
        drawPlanet(1, 0.6, 224.70, 0.07f, 0,1,1); // Wenus
    }

    @Override
    protected void input() {
    }

    @Override
    protected void logic() {

        try {
            Thread.sleep(16, 666);
        } catch (InterruptedException ex) {
        }
    }

    public static void main(String[] args) {
        new Planets().start();
    }
}
