package tuner.helpers.ModelController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.megacrit.cardcrawl.core.Settings;
import org.lwjgl.opengl.EXTFramebufferObject;
import tuner.misc.charSelect.SkinSelectScreen;
import tuner.helpers.ModHelper;

import java.util.ArrayList;
import java.util.List;

public class character3DHelper {
    public static final int SSAA = 4;

    private OrthographicCamera camera;
    private FrameBuffer frameBuffer;
    private TextureRegion region;
    private Environment environment;
    private PolygonSpriteBatch psb;
    private boolean inited = false;
    private ModelController modelController;
    private float drawX;
    private float drawY;

    private int skinType;

    public character3DHelper() {
        this.skinType = SkinSelectScreen.Inst.index;
        init(this.skinType);
    }

//    @SpirePatch(clz = ModelController.class, method = SpirePatch.CONSTRUCTOR)
//    public static class Patch2 {
//        @SpirePostfixPatch
//        public static void Postfix(ModelController instance) {
//            ModelBatch modelBatch = ReflectionHacks.getPrivate(instance, ModelController.class, "modelBatch");
//            modelBatch.dispose();
//
//            DefaultShaderProvider shaderProvider = new DefaultShaderProvider();
//            shaderProvider.config.fragmentShader = Gdx.files.classpath("chaofanfragshader.fs").readString();
//
//            ModelBatch newModelBatch = new ModelBatch(shaderProvider);
//            ReflectionHacks.setPrivate(instance, ModelController.class, "modelBatch", newModelBatch);
//        }
//    }

    private void init(int skintype) {
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth() * SSAA, Gdx.graphics.getHeight() * SSAA);
        this.camera.position.set(0, 0, 0);
        this.camera.near = -1000.0F;
        this.camera.far = 1000.0F;
        this.camera.rotate(new Vector3(0.0F, 1.0F, 0.0F), 90.0F);

        camera.update();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                Gdx.graphics.getWidth() * SSAA,
                Gdx.graphics.getHeight() * SSAA, true);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0F, 1.0F, 1.0F, 1.0F));
//        environment.add(new DirectionalLight().set(0.8f,0.8f,0.8f,-0.2f,-1f,1f));
//        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
//        environment.add(new DirectionalLight().set(0.5f, 0.5f, 0.5f, -1f, -0.4f, 1f));

        psb = new PolygonSpriteBatch();

        switch (skintype) {
            case 0:
                modelController = new ModelController("tunerResources/img/3d/hina/hina.g3dj", 0, 0, 0, "Hina_Original_Normal_Idle");
                break;
            case 1:
                modelController = new ModelController("tunerResources/img/3d/hinaswim/hinaswim.g3dj", 0, 0, 0, "CH0063_Normal_Idle");
                break;
            case 2:
                modelController = new ModelController("tunerResources/img/3d/hinadress/hinadress.g3dj", 0, 0, 0, "CH0230_Normal_Idle");
                break;
            default:
                modelController = new ModelController("tunerResources/img/3d/hina/hina.g3dj", 0, 0, 0, "Hina_Original_Normal_Idle");
                break;
        }

        for (Material material : modelController.getInstance().materials) {
            material.set(ColorAttribute.createDiffuse(Color.WHITE));
            if (material.id.contains("Month")) {
                material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            }
        }
//        modelController.getInstance().materials.get(8).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));


        modelController.getTransForm().rotate(0, 1, 0, 180);
        modelController.getTransForm().rotate(0, 0, 1, 15);

        modelController.resetPosition(0, 0);

        modelController.setDefaultState();
        this.inited = true;
    }

    public void render(SpriteBatch spriteBatch, boolean flipHorizontal) {
        this.modelController.flip(flipHorizontal);
        spriteBatch.end();

        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth() * SSAA, Gdx.graphics.getHeight() * SSAA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        modelController.render(camera, environment);

        frameBuffer.end();

        region = new TextureRegion(frameBuffer.getColorBufferTexture());
        region.flip(false, true);

//        frameBuffer.getColorBufferTexture().bind(0);
        try {
            Texture texture = frameBuffer.getColorBufferTexture();

            texture.bind(0);
            EXTFramebufferObject.glGenerateMipmapEXT(texture.glTarget);
            texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        } catch (Throwable ex) {
            ModHelper.logger.warn("Generate mipmap for remember frame buffer failed: " + ex);
        }

        psb.begin();
        psb.draw(region, this.drawX - Settings.WIDTH / 2.0F,
                this.drawY - Settings.HEIGHT / 2.0F,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                1.0F, 1.0F, 0.0F);

        psb.setShader(null);

        psb.setBlendFunction(770, 771);
        psb.end();
        spriteBatch.begin();
    }

    public void update(float current_x, float current_y) {
        if (this.skinType != SkinSelectScreen.Inst.index) {
            this.skinType = SkinSelectScreen.Inst.index;
            init(skinType);
        }

        this.modelController.update();
        this.drawX = current_x;
        this.drawY = current_y;
        this.modelController.resetPosition(0, 0);
//        this.modelController.\
    }


    public void queueAnimaItem(List<AnimaItem> items, boolean clearQueue) {
        queueAnimaItem(items, clearQueue, 1);
    }

    public void queueAnimaItem(AnimaItem item, boolean clearQueue) {
        List<AnimaItem> list = new ArrayList<>();
        list.add(item);
        queueAnimaItem(list, clearQueue, 1);
    }

    public void queueAnimaItem(List<AnimaItem> items, boolean clearQueue, int loop) {
        if (clearQueue) {
            this.modelController.clearQueue();
        }
        if (!items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                String id = this.modelController.getAnimaId(items.get(i).get());
                modelController.queueAnimation(modelController -> {
                    modelController.animate(id, loop, 1, null, 0.2F);
                });
            }
        }
    }

    public void queueTransItem(List<ModelController.TransItem> items) {
        items.forEach(this.modelController::queueTrans);
    }

    public void rotateModel(int x, int y, int z, int degree) {
        modelController.getTransForm().rotate(x, y, z, degree);
    }

    //有big
//    public void resetModelRotation() {
//        modelController.getTransForm().setToRotation(0, 0, 0, 0);
//        modelController.getTransForm().rotate(0, 0, 1, 15);
//    }

    public void clearAnimation() {
        this.modelController.clearQueue();
        this.modelController.setDefaultState();
    }

    public ArrayList<String> getCommandList() {
        ArrayList<String> commands = new ArrayList<>();
        for (Animation a : this.modelController.getAnimaMap()) {
            commands.add(a.id);
        }
        return commands;
    }

    public String getCurrentAnima() {
        return this.modelController.getCurrentAnima();
    }

    public boolean isAttacking() {
        String anima = this.getCurrentAnima();
        return (SkinSelectScreen.Inst.index == 0 && anima.equals(this.modelController.getAnimaId(HinaAnimaItem.ATTACK_START.get()))) ||
                (SkinSelectScreen.Inst.index == 1 && anima.equals(this.modelController.getAnimaId(HinaSwimAnimaItem.ATTACK_START.get())));
    }

}
