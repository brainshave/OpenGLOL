package sw.cw8;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static sw.utils.Utils.normalize;
import static sw.utils.Utils.vector;
import static sw.utils.Utils.vectorProduct;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 15.04.11
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */

class Origin {
    public final Terrain.Side side;
    public final Terrain terrain;

    Origin(Terrain terrain, Terrain.Side side) {
        this.terrain = terrain;
        this.side = side;
    }
}

public class Terrain {
    public enum Side {
        NORTH {
            @Override
            protected int x(int i, int len) {
                return i;
            }

            @Override
            protected int z(int i, int len) {
                return 0;
            }

            @Override
            protected Side opposite() {
                return SOUTH;
            }
        },
        SOUTH {
            @Override
            protected int x(int i, int len) {
                return i;
            }

            @Override
            protected int z(int i, int len) {
                return len - 1;
            }

            @Override
            protected Side opposite() {
                return NORTH;
            }
        },
        EAST {
            @Override
            protected int x(int i, int len) {
                return 0;
            }

            @Override
            protected int z(int i, int len) {
                return i;
            }

            @Override
            protected Side opposite() {
                return WEST;
            }

        },
        WEST {
            @Override
            protected int x(int i, int len) {
                return len - 1;
            }

            @Override
            protected int z(int i, int len) {
                return i;
            }

            @Override
            protected Side opposite() {
                return EAST;
            }
        };


        void copyHeights(float[][][] from, float[][][] to) {
            int len = from.length;
            for (int i = 0; i < len; ++i) {
                to[opposite().x(i, len)][opposite().z(i, len)][1] = from[x(i, len)][z(i, len)][1];
            }
        }

        protected abstract int z(int i, int len);

        protected abstract int x(int i, int len);

        protected abstract Side opposite();
    }

    private Random rand = new Random();
    protected float[][][] verts;
    public final int density;
    public final int size;
    public final double step;
    private FloatBuffer terrain;

    public Terrain(List<Origin> sides) {
        Terrain firstOrigin = sides.get(0).terrain;
        this.density = firstOrigin.density;
        this.size = firstOrigin.size;
        this.step = firstOrigin.step;
        this.verts = new float[size][size][];
        setupTerrain(sides);
    }

    public Terrain(int density) {
        this.density = density;
        this.size = (int) (Math.pow(2, density) + 1);
        this.step = 2.0 / (this.size - 1);
        this.verts = new float[size][size][];
        setupTerrain(null);
    }

    private void setupTerrain(List<Origin> sides) {
        for (int x = 0; x < size; ++x) {
            for (int z = 0; z < size; ++z) {
                verts[x][z] = new float[]{(float) (x * step - 1.0), rand.nextFloat(), (float) (z * step - 1.0)};
            }
        }

        int leftMargin = 0, rightMargin = 0, topMargin = 0, bottomMargin = 0;
        if (sides != null) {
            for (Origin origin : sides) {
                origin.side.copyHeights(origin.terrain.verts, verts);
                switch (origin.side) {
                    case NORTH:
                        topMargin = 1;
                        break;
                    case SOUTH:
                        bottomMargin = 1;
                        break;
                    case EAST:
                        rightMargin = 1;
                        break;
                    case WEST:
                        leftMargin = 1;
                        break;
                }
            }
        }

        for (int i = (size - 1) / 2; i > 0; i /= 2) {

            if (topMargin == 0) {
                for (int x = i; x < size - rightMargin; x += 2 * i) { // zerowy rząd
                    verts[x][0][1] = between(verts[x - i][0], verts[x + i][0])[1];
                }
            }

            if (leftMargin == 0) {
                for (int z = i; z < size - bottomMargin; z += 2 * i) { // zerowa kolumna
                    verts[0][z][1] = between(verts[0][z - i], verts[0][z + i])[1];
                }
            }

            for (int x = i; x < size - rightMargin; x += 2 * i) {
                for (int z = i; z < size - bottomMargin; z += 2 * i) {
                    if (z + i < size - bottomMargin)
                        verts[x][z + i][1] = between(verts[x - i][z + i], verts[x + i][z + i])[1];
                    if (x + i < size - rightMargin)
                        verts[x + i][z][1] = between(verts[x + i][z - i], verts[x + i][z + i])[1];
                    verts[x][z][1] = between(verts[x - i][z - i], verts[x + i][z - i], verts[x - i][z + i], verts[x + i][z + i])[1];
                }
            }
        }


//        if (sides != null) {
//            for (Origin origin : sides) {
//                origin.side.copyHeights(origin.terrain.verts, verts);
//            }
//        }

        terrain = BufferUtils.createFloatBuffer(size * size * 3 * 16);

        terrain.rewind();
        for (int x = 0; x < size - 1; ++x) {
            for (int z = 0; z < size - 1; ++z) {
                putTriangle(verts[x][z], verts[x + 1][z], verts[x][z + 1]);
                putTriangle(verts[x + 1][z], verts[x + 1][z + 1], verts[x][z + 1]);
            }
        }
        terrain.flip();
    }

    public void draw() {
        glInterleavedArrays(GL_N3F_V3F, 0, terrain);
        glDrawArrays(GL_TRIANGLES, 0, terrain.remaining() / 6);
    }

    float[] between(float[] a, float[] b) {
        float[] ret = new float[3];
        for (int i = 0; i < 3; i += 2) {
            ret[i] = (a[i] + b[i]) / 2;
        }
        double x = Math.max(Math.abs(b[0] - a[0]), Math.abs(b[2] - a[2])) / 2.0;
        double Wx = W(x);
        ret[1] = (float) ((1.0 - 2.0 * Wx) * rand.nextDouble() + Wx * (a[1] + b[1]));
        return ret;
    }

    float[] between(float[] north, float[] south, float[] west, float[] east) {
        float[] ret = between(north, south);
        double x = Math.abs(east[0] - west[0]) / 2.0;
        double Wcx = Wc(x);
        ret[1] = (float) ((1.0 - 4.0 * Wcx) * rand.nextDouble() + Wcx * (north[1] + south[1] + west[1] + east[1]));
        return ret;
    }

    double W(double x) {
        return (1.0 - Math.cos(Math.pow(1.0 - x, 1.75) * Math.PI)) / 4.0;
    }

    double Wc(double x) {
        return W(x) / 2.0;
    }

    void putTriangle(float[] v1, float[] v2, float[] v3) {
        float[] a = normalize(vector(v1, v2));
        float[] b = normalize(vector(v1, v3));
        float[] norm = vectorProduct(b, a);
        terrain.put(norm);
        terrain.put(v3);
        terrain.put(norm);
        terrain.put(v2);
        terrain.put(norm);
        terrain.put(v1);
    }

}
