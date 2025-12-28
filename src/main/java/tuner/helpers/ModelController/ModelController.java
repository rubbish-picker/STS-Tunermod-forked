package tuner.helpers.ModelController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.megacrit.cardcrawl.core.Settings;
import tuner.helpers.ModHelper;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

import static tuner.helpers.ModelController.character3DHelper.SSAA;

public class ModelController {
    private static final float SCALE = 2.2F * Settings.scale * SSAA;
    private static final float MOVESCALE = Settings.scale * 4 * SSAA;
    private G3dModelLoader loader;
    private Model model;
    private ModelInstance instance;
    private ModelBatch modelBatch;
    private AnimationController animationController;
    private float current_x = 0, current_y = 0, target_x = 0, target_y = 0;
    private String StandAnima;

    private Queue<Consumer<AnimationController>> animaQueue;
    private Queue<TransItem> transQueue;
    private Matrix4 DefaultState;
    private boolean fliped = false;

    public ModelController(String modelPath, float x, float y, float z, String StandAnima) {
        loader = new G3dModelLoader(new JsonReader());
        model = loader.loadModel(Gdx.files.internal(modelPath));
        instance = new ModelInstance(model, -500, 0, 0);
        animationController = new AnimationController(instance);
        animationController.allowSameAnimation = true;
        modelBatch = new ModelBatch();
        instance.transform.setTranslation(z, y, x);
//        instance.transform.rotate(0, 3, 1, 180);
//        instance.transform.rotate(1, 0, 0, 34);
        instance.transform.scale(SCALE, SCALE, SCALE);


//        for (int i = 0; i < instance.materials.size; i++) {
//            instance.materials.get(i).set(ColorAttribute.createDiffuse(Color.WHITE));
//            if (this.instance.materials.get(i).id.contains("Mouth")) {
//                this.instance.materials.get(i).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
//            }
//        }

        this.StandAnima = StandAnima;

        this.animaQueue = new LinkedList<>();
        this.transQueue = new LinkedList<>();
    }

    public void setDefaultState() {
        DefaultState = this.instance.transform.cpy();
    }

    public void update() {
        if ((this.animationController.current == null || this.animationController.current.time >= this.animationController.current.duration)) {
            if (!this.animaQueue.isEmpty()) {
                Objects.requireNonNull(this.animaQueue.poll()).accept(this.animationController);
            } else {
//                this.animationController.queue(this.StandAnima, -1, 1, null, 0.4F);
                this.animationController.queue(this.StandAnima, 1, 1, null, 0.0F);
            }
        }
        if (!this.transQueue.isEmpty()) {
            TransItem item = this.transQueue.peek();
            item.time -= Gdx.graphics.getDeltaTime();
            if (item.time < 0.0F) {
                item.accept(this.instance);
                this.transQueue.poll();
            }
        }
        animationController.update(Gdx.graphics.getDeltaTime());

        if (Math.abs(this.current_x - this.target_x) > MOVESCALE) {
            this.current_x += Math.signum(this.target_x - this.current_x) * MOVESCALE;
        } else {
            this.current_x = this.target_x;
        }
        if (Math.abs(this.current_y - this.target_y) > MOVESCALE) {
            this.current_y += Math.signum(this.target_y - this.current_y) * MOVESCALE;
        }else
        {
            this.current_y = this.target_y;
        }
        this.instance.transform.setTranslation(-0, current_y, current_x);
    }

    public void render(OrthographicCamera camera, Environment environment) {
        modelBatch.begin(camera);
        modelBatch.render(instance, environment);
        modelBatch.end();
    }

    public void resetPosition(float x, float y) {
        this.current_x = this.target_x = x;
        this.current_y = this.target_y = y;
        this.instance.transform.setTranslation(-500, current_y, current_x);
    }

    public void setCurrentPosition(float x, float y) {
        this.current_x = x;
        this.current_y = y;
    }

    public void moveCurrentPosition(float x, float y) {
        this.current_x += x;
        this.current_y += y;
    }

    public void clearQueue(AnimationController controller) {
        while (controller.current != null) {
            controller.setAnimation(null);
            this.animaQueue.clear();
            this.transQueue.clear();
            this.resetModel();
        }
    }

    private void resetModel() {
        this.instance.transform = DefaultState.cpy();
    }

    public void clearQueue() {
        while (this.animationController.current != null) {
            this.animationController.setAnimation(null);
            this.animaQueue.clear();
            this.transQueue.clear();
            this.resetModel();
        }
    }

    public void setAnimation(Consumer<AnimationController> animation) {
        this.clearQueue(animationController);
        resetPosition(this.target_x, this.target_y);
        this.animaQueue.add(animation);
    }

    public void queueAnimation(Consumer<AnimationController> animation) {
        this.animaQueue.add(animation);
    }

    public void setStandAnima(String anima) {
        if (this.animationController.current != null && this.animationController.current.animation.id.equals(this.StandAnima)) {
            this.clearQueue(this.animationController);
        }
        this.StandAnima = anima;
    }

    public String getCurrentAnima() {
        if (this.animationController.current == null) {
            return "";
        }
        return this.animationController.current.animation.id;
    }

    public void resetDefaultAnima() {
        this.clearQueue(this.animationController);
    }

    public Matrix4 getTransForm() {
        return this.instance.transform;
    }

    public Array<Animation> getAnimaMap() {
        return this.instance.animations;
    }

    public String getAnimaId(int i) {
        if (i < this.instance.animations.size)
            return this.instance.animations.get(i).id;
        else
            return this.instance.animations.get(0).id;
    }

    public ModelInstance getInstance() {
        return instance;
    }

    public boolean flip(boolean flip) {
        if (flip != this.fliped) {
            this.instance.transform.rotate(0, 1, 0, 180);
            this.fliped = flip;
            this.setDefaultState();
        }
        return this.fliped;
    }

    public void queueTrans(TransItem item) {
        this.transQueue.add(item);
    }

    public static class TransItem {
        float time;
        Consumer<ModelInstance> trans;

        public TransItem(float time, Consumer<ModelInstance> trans) {
            this.time = time;
            this.trans = trans;
        }

        public void accept(ModelInstance instance) {
            this.trans.accept(instance);
        }
    }

}
