package com.Archer.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class Main implements ApplicationListener {
	
	public static class GameObject extends ModelInstance {
        public final Vector3 center = new Vector3();
        public final Vector3 dimensions = new Vector3();
        public final float radius;

        private final static BoundingBox bounds = new BoundingBox();

        public GameObject(Model model, String rootNode, boolean mergeTransform) {
        	super(model, rootNode, mergeTransform);
            calculateBoundingBox(bounds);
            bounds.getCenter(center);
            bounds.getDimensions(dimensions);
            radius = dimensions.len() / 2f;
        }
    }
	
	protected PerspectiveCamera cam;
	protected ModelBatch modelBatch;
	protected AssetManager assets;
	protected Array<ModelInstance> instances = new Array<ModelInstance>();
	protected Environment environment;
	protected FPSCameraController camController;
	protected boolean loading;
	
	protected Array<GameObject> blocks = new Array<GameObject>();
	protected Array<GameObject> invaders = new Array<GameObject>();
	protected ModelInstance ship;
	protected ModelInstance space;
    
    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;
    
    public Model box;
    public ModelInstance instance;
	@Override
	public void create() {
		
		stage = new Stage();
        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(label);
        stringBuilder = new StringBuilder();
        
		modelBatch = new ModelBatch();
		environment = new Environment();
	    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
	    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	        
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10.0f, 5f);
        cam.lookAt(0,10.0f,0);
        cam.near = 1.0f;
        cam.far = 500.0f;
        cam.update();
        
        ModelBuilder modelBuilder = new ModelBuilder();
        box = modelBuilder.createBox(5f, 5f, 5f, 
            new Material(ColorAttribute.createDiffuse(Color.RED)),
            Usage.Position | Usage.Normal);
        instance = new ModelInstance(box);
        
        camController = new FPSCameraController(cam);
        Gdx.input.setInputProcessor(camController);
        Gdx.input.setCursorCatched(true);
        
        assets = new AssetManager();
        assets.load("landscape.g3dj", Model.class);
        loading = true;
		
	}
	
	private void doneLoading() {
		Model model = assets.get("landscape.g3dj", Model.class);
        for (int i = 0; i < model.nodes.size; i++) {
            String id = model.nodes.get(i).id;
            
            ModelInstance instance = new ModelInstance(model);
            
            System.out.println("Drawing: " + id);
            instances.add(instance);

        }
        
        loading = false;
    }

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		
	}
	//private Vector3 position = new Vector3();
	protected boolean isVisible(final Camera cam, final ModelInstance instance) {
		return true;
    }
	
	private int visibleCount;
	@Override
	public void render() {
		if (loading && assets.update()) {
			doneLoading();
		}
		camController.update();
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        visibleCount = 0;
        for (final ModelInstance instance : instances) {
            if (isVisible(cam, instance)) {
                modelBatch.render(instance, environment);
                visibleCount++;
            }
        }
        if (space != null) {
            modelBatch.render(space);
        }
        ModelInstance boxInstance = new ModelInstance(this.box);
        modelBatch.render(boxInstance);
        modelBatch.end();
        
        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Visible: ").append(visibleCount);
        label.setText(stringBuilder);
        stage.draw();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
        instances.clear();
        assets.dispose();
        box.dispose();
	}
	
	
}
