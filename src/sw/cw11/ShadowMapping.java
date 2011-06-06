package sw.cw11;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ARBDepthTexture;
import org.lwjgl.opengl.ARBShadow;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import sw.utils.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluLookAt;

public class ShadowMapping extends GLBaza {
    Cube cube;
    Light bright, dim;
    int firstDisplayList;


    Material cubeMat, tabletopMat;
    float[] lightPos = {0, 10, 0, 1};
    float[] lightUpV = {0, 0, 1};
    float[] cameraPos = {0, 6, 6, 1};
    float[] cameraUpV = {0, 1, 0};
    private int shadowMapTexture;
    private int shadowMapSize = 512;
    FloatBuffer cameraProjMat = BufferUtils.createFloatBuffer(16);
    FloatBuffer cameraViewMat = BufferUtils.createFloatBuffer(16);
    FloatBuffer lightProjMat = BufferUtils.createFloatBuffer(16);
    FloatBuffer lightViewMat = BufferUtils.createFloatBuffer(16);

    FloatBuffer[] textureMatrixRows = {
            BufferUtils.createFloatBuffer(4),
            BufferUtils.createFloatBuffer(4),
            BufferUtils.createFloatBuffer(4),
            BufferUtils.createFloatBuffer(4)
    };

    private void lookAt(float[] pos, float[] upV) {
        glLoadIdentity();
        gluLookAt(pos[0], pos[1], pos[2], 0, 0, 0, upV[0], upV[1], upV[2]);
    }

    private void loadMatrices(FloatBuffer projection, FloatBuffer modelView) {
        glMatrixMode(GL_PROJECTION);
        projection.rewind();
        glLoadMatrix(projection);
        glMatrixMode(GL_MODELVIEW);
        modelView.rewind();
        glLoadMatrix(modelView);
    }

    @Override
    protected void init() {
        Utils.enable(new int[]{GL_DEPTH_TEST, GL_NORMALIZE});
        //glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        //glEnable(GL_CULL_FACE);
        //glEnable(GL_COLOR_MATERIAL);
        //glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

        //glShadeModel(GL_SMOOTH);
        glDepthFunc(GL11.GL_LEQUAL);

        glColor4f(1, 1, 1, 1);
        glClearColor(0, 0, 0, 0);

        cube = new Cube();
        bright = new Light(GL_LIGHT0, new float[][]{
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                lightPos
        });
        dim = new Light(GL_LIGHT0, new float[][]{
                {0, 0, 0, 1},
                {0.3f, 0.3f, 0.3f, 1},
                {0, 0, 0, 1},
                lightPos
        });
        cubeMat = new Material(120, new float[][]{
                {0, 0.2f, 0.2f, 1},
                {0, 1, 1, 1},
                {1, 1, 1, 1,}
        });
        tabletopMat = new Material(1, new float[][]{
                {0, 0.1f, 0, 1},
                {0, 0.1f, 0, 1},
                {0, 0, 0, 0}
        });

        createLists();

        createShadowMapTexture();

        saveMatrices();
    }


    private void createShadowMapTexture() {
        shadowMapTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, shadowMapTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
                shadowMapSize, shadowMapSize, 0, GL_DEPTH_COMPONENT,
                GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
    }

    private void saveMatrices() {
        lightProjMat.rewind();
        lightViewMat.rewind();
        cameraProjMat.rewind();
        cameraViewMat.rewind();
        Utils.initPerspective(this, 3, 10);
        lookAt(lightPos, lightUpV);
        glGetFloat(GL_PROJECTION_MATRIX, lightProjMat);
        glGetFloat(GL_MODELVIEW_MATRIX, lightViewMat);

        Utils.initPerspective(this, 1.3f, 100);
        lookAt(cameraPos, cameraUpV);
        glGetFloat(GL_PROJECTION_MATRIX, cameraProjMat);
        glGetFloat(GL_MODELVIEW_MATRIX, cameraViewMat);


        Matrix4f lightPro = new Matrix4f();
        lightPro.load(lightProjMat);
        Matrix4f lightView = new Matrix4f();
        lightView.load(lightViewMat);

        Matrix4f biasMatrix = new Matrix4f();
        FloatBuffer biasBuff = Utils.bufferFromArray(new float[]{
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f
        });
        biasBuff.rewind();
        biasMatrix.load(biasBuff);

        Matrix4f textureMatrix = new Matrix4f();
        Matrix4f.mul(Matrix4f.mul(biasMatrix, lightPro, null), lightView, textureMatrix);
        //Matrix4f.mul(Matrix4f.mul(lightPro, lightView, null), biasMatrix, textureMatrix);

        FloatBuffer texMatBuff = BufferUtils.createFloatBuffer(16);
        texMatBuff.rewind();
        textureMatrix.storeTranspose(texMatBuff);
        texMatBuff.rewind();
        for (int row = 0; row < 4; ++row) {
            textureMatrixRows[row].rewind();
            for (int col = 0; col < 4; ++col) {
                textureMatrixRows[row].put(texMatBuff.get());
            }
            textureMatrixRows[row].rewind();
        }

        glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGen(GL_S, GL_EYE_PLANE, textureMatrixRows[0]);

        glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGen(GL_T, GL_EYE_PLANE, textureMatrixRows[1]);

        glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGen(GL_R, GL_EYE_PLANE, textureMatrixRows[2]);

        glTexGeni(GL_Q, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
        glTexGen(GL_Q, GL_EYE_PLANE, textureMatrixRows[3]);

        glTexParameteri(GL_TEXTURE_2D, ARBShadow.GL_TEXTURE_COMPARE_MODE_ARB, ARBShadow.GL_COMPARE_R_TO_TEXTURE_ARB);
        glTexParameteri(GL_TEXTURE_2D, ARBShadow.GL_TEXTURE_COMPARE_FUNC_ARB, GL11.GL_LEQUAL);
        glTexParameteri(GL_TEXTURE_2D, ARBDepthTexture.GL_DEPTH_TEXTURE_MODE_ARB, GL_INTENSITY);

    }

    @Override
    protected void input() {
        while (Keyboard.next()) {
            lit = !Keyboard.getEventKeyState();
        }
    }

    float rotation;
    boolean lit = true;

    void createLists() {
        firstDisplayList = glGenLists(2);
        // table
        glNewList(firstDisplayList, GL_COMPILE);
        {
            glPushMatrix();
            glScalef(5, 0.2f, 5);
            tabletopMat.set();
            cube.draw();
            glPopMatrix();
            glTranslatef(0, 3, 0);
        }
        glEndList();

        // brick
        glNewList(firstDisplayList + 1, GL_COMPILE);
        {
            cubeMat.set();
            cube.draw();
        }
        glEndList();
    }

    protected void drawScene() {
        glPushMatrix();
        glCallList(firstDisplayList);
        glRotatef(rotation, 0, 1, 1);
        glCallList(firstDisplayList + 1);
        glPopMatrix();
    }

    int[] textureGenerators = {GL_TEXTURE_GEN_S, GL_TEXTURE_GEN_T, GL_TEXTURE_GEN_R, GL_TEXTURE_GEN_Q, GL_TEXTURE_2D, GL_ALPHA_TEST, GL_LIGHTING};

    @Override
    protected void render() {
        //saveMatrices();
        glClear(GL_DEPTH_BUFFER_BIT);

        // 1. Light's point of view
        loadMatrices(lightProjMat, lightViewMat);
        glViewport(0, 0, shadowMapSize, shadowMapSize);

        //glCullFace(GL_FRONT);
        glShadeModel(GL_FLAT);
        glColorMask(false, false, false, false);

        glBindTexture(GL_TEXTURE_2D, shadowMapTexture);
        drawScene();
        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, shadowMapSize, shadowMapSize);

        // Revert state
        glShadeModel(GL_SMOOTH);
        glColorMask(true, true, true, true);
        glViewport(0, 0, width, height);

        // 2. Dim-lit scene from eye perspective
        loadMatrices(cameraProjMat, cameraViewMat);
        dim.on();
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        drawScene();

        // 3. Bright-lit scene with mask
        Utils.enable(textureGenerators);
        glAlphaFunc(GL_GEQUAL, 0.99f);
        if (lit) bright.on();
        drawScene();
        Utils.disable(textureGenerators);
    }


    int term = 5000;

    @Override
    protected void logic() {
        Utils.sleep60Hz();
        rotation = ((float) (System.currentTimeMillis() % term) / term) * 360;
    }

    public static void main(String[] args) {
        new ShadowMapping().start();
    }
}
