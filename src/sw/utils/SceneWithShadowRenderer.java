package sw.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static sw.utils.ShadowMappingUtils.*;

/**
 * Created by IntelliJ IDEA.
 * User: SW
 * Date: 15.06.11
 * Time: 10:12
 */
public class SceneWithShadowRenderer {
    Scene scene;
    int shadowMapSize;
    float cameraNear, cameraFar;
    float[] cameraPos, cameraAt, cameraUp;
    Light bright;
    Light dim;
    float lightNear, lightFar;
    float[] lightPos, lightAt, lightUp;
    int shadowMapTexture;

    public SceneWithShadowRenderer(
            Scene scene, int shadowMapSize,
            float cameraNear, float cameraFar,
            float[] cameraPos, float[] cameraAt, float[] cameraUp,
            Light bright, Light dim,
            float lightNear, float lightFar,
            float[] lightPos, float[] lightAt, float[] lightUp) {
        this.scene = scene;
        this.shadowMapSize = shadowMapSize;
        this.cameraNear = cameraNear;
        this.cameraFar = cameraFar;
        this.cameraPos = cameraPos;
        this.cameraAt = cameraAt;
        this.cameraUp = cameraUp;
        this.bright = bright;
        this.dim = dim;
        this.lightNear = lightNear;
        this.lightFar = lightFar;
        this.lightPos = lightPos;
        this.lightAt = lightAt;
        this.lightUp = lightUp;

        init();
    }

    public SceneWithShadowRenderer(
            Scene scene, int shadowMapSize,
            float cameraNear, float cameraFar,
            float[] cameraPos, float[] cameraAt, float[] cameraUp,
            int lightName, float lightNear, float lightFar,
            float[] lightPos, float[] lightAt, float[] lightUp) {
        this.scene = scene;
        this.shadowMapSize = shadowMapSize;
        this.cameraNear = cameraNear;
        this.cameraFar = cameraFar;
        this.cameraPos = cameraPos;
        this.cameraAt = cameraAt;
        this.cameraUp = cameraUp;
        this.lightNear = lightNear;
        this.lightFar = lightFar;
        this.lightPos = lightPos;
        this.lightAt = lightAt;
        this.lightUp = lightUp;

        this.bright = new Light(lightName, new float[][]{
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                {1, 1, 1, 1},
                lightPos
        });
        this.dim = new Light(lightName, new float[][]{
                {0, 0, 0, 1},
                {0.4f, 0.4f, 0.4f, 1},
                {0, 0, 0, 1},
                lightPos
        });

        init();
    }

    private FloatBuffer lightProjectionMatrix = BufferUtils.createFloatBuffer(16);
    private FloatBuffer lightModelViewMatrix = BufferUtils.createFloatBuffer(16);
    private FloatBuffer cameraProjectionMatrix = BufferUtils.createFloatBuffer(16);
    private FloatBuffer cameraModelViewMatrix = BufferUtils.createFloatBuffer(16);

    private void init() {
        Utils.enable(new int[]{GL_DEPTH_TEST, GL_NORMALIZE, GL_POLYGON_OFFSET_FILL});
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        glDepthFunc(GL_LEQUAL);
        glAlphaFunc(GL_GEQUAL, 0.99f);

        glColor4f(1, 1, 1, 1);
        glClearColor(0, 0, 0, 0);

        shadowMapTexture = createShadowMapTexture(shadowMapSize);

        lightProjectionMatrix.rewind();
        lightModelViewMatrix.rewind();
        cameraProjectionMatrix.rewind();
        cameraModelViewMatrix.rewind();
        setMatrices(scene.getWidth(), scene.getHeight(), lightNear, lightFar, lightPos, lightAt, lightUp);
        saveMatrices(lightProjectionMatrix, lightModelViewMatrix);
        setMatrices(scene.getWidth(), scene.getHeight(), cameraNear, cameraFar, cameraPos, cameraAt, cameraUp);
        saveMatrices(cameraProjectionMatrix, cameraModelViewMatrix);

        setTextureGenerationVectors(lightProjectionMatrix, lightModelViewMatrix);
        setTextureGeneration();
    }

    private int[] texGens = {
            GL_TEXTURE_GEN_S, GL_TEXTURE_GEN_T, GL_TEXTURE_GEN_R,
            GL_TEXTURE_GEN_Q};
    private int[] textureGenerators = { GL_TEXTURE_2D, GL_ALPHA_TEST, GL_LIGHTING };

    public void render(boolean lit) {
        glClear(GL_DEPTH_BUFFER_BIT);

        // 1. Light's point of view
        loadMatrices(lightProjectionMatrix, lightModelViewMatrix);
        glViewport(0, 0, shadowMapSize, shadowMapSize);

        //glCullFace(GL_FRONT);
        glShadeModel(GL_FLAT);
        glColorMask(false, false, false, false);

        scene.drawScene(false);
        glBindTexture(GL_TEXTURE_2D, shadowMapTexture);
        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, shadowMapSize, shadowMapSize);

        // Revert state
        glShadeModel(GL_SMOOTH);
        glColorMask(true, true, true, true);
        glViewport(0, 0, scene.getWidth(), scene.getHeight());

        // 2. Dim-lit scene from eye perspective
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        loadMatrices(cameraProjectionMatrix, cameraModelViewMatrix);

        glPolygonOffset(1, 3);
        dim.on();
        scene.drawScene(true);

        // 3. Bright-lit scene with mask
        if (lit) bright.on();
        glBindTexture(GL_TEXTURE_2D, shadowMapTexture);
        Utils.enable(textureGenerators);
        Utils.enable(texGens);
        glPolygonOffset(1, 0);
        scene.drawScene(true);
        Utils.disable(textureGenerators);
        Utils.disable(texGens);
    }

    public void renderFromLightPerspective() {
       glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        loadMatrices(lightProjectionMatrix, lightModelViewMatrix);

        //glPolygonOffset(1, 1);
        dim.on();
        scene.drawScene(false);

        // 3. Bright-lit scene with mask
        bright.on();
        //glBindTexture(GL_TEXTURE_2D, shadowMapTexture);
        //Utils.enable(textureGenerators);
        //Utils.enable(texGens);
        //glPolygonOffset(1, 0);
        scene.drawScene(false);
        Utils.disable(textureGenerators);
        Utils.disable(texGens);
    }
}
