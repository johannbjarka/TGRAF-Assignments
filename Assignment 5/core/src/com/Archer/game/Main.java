package com.Archer.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

public class Main implements ApplicationListener {
	
	static class MyMotionState extends btMotionState {
        Matrix4 transform;
        @Override
        public void getWorldTransform (Matrix4 worldTrans) {
            worldTrans.set(transform);
        }
        @Override
        public void setWorldTransform (Matrix4 worldTrans) {
            transform.set(worldTrans);
        }
    }
	
	class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded (int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
        	/*
        	if (userValue1 == 0)
                ((ColorAttribute)instances.get(userValue0).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            if (userValue0 == 0)
                ((ColorAttribute)instances.get(userValue1).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
            */return true;
        }
    }
	
	static class GameObject extends ModelInstance implements Disposable {
        public final btRigidBody body;
        public final MyMotionState motionState;
        
        public GameObject (Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
            super(model, node);
            motionState = new MyMotionState();
            motionState.transform = transform;
            body = new btRigidBody(constructionInfo);
            body.setMotionState(motionState);
            body.setRestitution(0.5f);
        }
		
		@Override
		public void dispose () {
			body.dispose();
            motionState.dispose();
		}
		
        static class Constructor implements Disposable {
            public final Model model;
            public final String node;
            public final btCollisionShape shape;
            public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
            private static Vector3 localInertia = new Vector3();
            
            public Constructor (Model model, String node, btCollisionShape shape, float mass) {
                this.model = model;
                this.node = node;
                this.shape = shape;
                if (mass > 0f)
                    shape.calculateLocalInertia(mass, localInertia);
                else
                    localInertia.set(0, 0, 0);
                this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
            }

            public GameObject construct() {
                return new GameObject(model, node, constructionInfo);
            }

            @Override
            public void dispose () {
                shape.dispose();
                constructionInfo.dispose();
            }
        }
    }
	
	protected PerspectiveCamera cam;
	protected ModelBatch modelBatch;
	protected AssetManager assets;

	protected Environment environment;
	protected FPSCameraController camController;
	protected boolean loading;
    
    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;
    private int visibleCount;
	boolean collision;
	float spawnTimer;
	
    Model model;
    
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    
    Array<GameObject> instances;
    ArrayMap<String, GameObject.Constructor> constructors;

    MyContactListener contactListener;
    btBroadphaseInterface broadphase;
    btDynamicsWorld dynamicsWorld;
    btConstraintSolver constraintSolver;

	GameObject character;
	btPairCachingGhostObject ghostObject;
	btConvexShape ghostShape;
	Matrix4 characterTransform;
	Vector3 characterDirection = new Vector3();
	Vector3 walkDirection = new Vector3();
	
	Sprite sprite;
    
    
	@Override
	public void create() {
		Bullet.init();
        
		// Set 2D stage for 2D UI
		stage = new Stage();
        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(label);
        stringBuilder = new StringBuilder();
        
        // Our model batch that we will render
		modelBatch = new ModelBatch();
		
		// Create our environment and lighting
		environment = new Environment();
	    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
	    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	    
	    // Create the perspective camera
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 5f, 0.5f);
        cam.lookAt(0,5f,0);
        cam.near = 0.1f;
        cam.far = 500.0f;
        cam.update();
        
        // Set the camera controller
        camController = new FPSCameraController(cam);
        Gdx.input.setInputProcessor(camController);
        Gdx.input.setCursorCatched(true);
        
        
        final Texture texture = new Texture(Gdx.files.internal("grass.jpg"));
        final Texture ironTex = new Texture(Gdx.files.internal("iron.jpg"));
        
        // Build some models
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
            .sphere(0.5f, 0.5f, 0.5f, 10, 10);
        
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(ironTex),ColorAttribute.createSpecular(1,1,1,1), FloatAttribute.createShininess(8f)))
            .box(2.5f, 2.5f, 2.5f);
        
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
            .cone(1f, 2f, 1f, 10);
        
        mb.node().id = "character";
        mb.part("character", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(texture),ColorAttribute.createSpecular(1,1,1,1), FloatAttribute.createShininess(8f)))
            .capsule(1f, 8f, 16);
        
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
            .capsule(.5f, 2f, 10);
        
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
            .cylinder(1f, 2f, 1f, 10);
        
        model = mb.end();
        
        constructors = new ArrayMap<String, GameObject.Constructor>(String.class, GameObject.Constructor.class);
        constructors.put("sphere", new GameObject.Constructor(model, "sphere", new btSphereShape(0.25f), 5f));
        constructors.put("box", new GameObject.Constructor(model, "box", new btBoxShape(new Vector3(1.25f, 1.25f, 1.25f)), 10f));
        constructors.put("cone", new GameObject.Constructor(model, "cone", new btConeShape(0.5f, 2f), 10f));
        constructors.put("capsule", new GameObject.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f), 10f));
        
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10, 0));
        
        contactListener = new MyContactListener();

        instances = new Array<GameObject>();

		constructors.insert(0, "character", new GameObject.Constructor(model, "character",  new btCapsuleShape(.5f, 4f), 1f));
		character = constructors.get("character").construct();
		character.transform.trn(0, 3.5f, 0);
		characterTransform = character.transform; // Set by reference
		instances.add(character);
		
		// Create the physics representation of the character
		ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(characterTransform);
		ghostShape = new btCapsuleShape(.5f, 4f);
		ghostObject.setCollisionShape(ghostShape);
		ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		//dynamicsWorld.addCollisionObject(ghostObject);

		camController.characterTransform = characterTransform;

        assets = new AssetManager();
        assets.load("landscape.g3dj", Model.class);
        loading = true;
        
        // Setup cross hair
        int w = Gdx.graphics.getWidth() / 60;
		int h = Gdx.graphics.getHeight() / 50;
		
		Pixmap pix = new Pixmap(w, h, Pixmap.Format.RGBA8888);
		pix.setColor(1f, 1f, 1f, 0.8f);
		pix.drawLine(w/2, 0, w/2, h);
		pix.drawLine(0, h/2, w, h/2);
		pix.setColor(0f, 0f, 0f, 0f);
		pix.drawPixel(w/2, h/2);
		
		Texture tex = new Texture(pix);
		this.sprite = new Sprite(tex);
		
		SpriteDrawable crossDraw = new SpriteDrawable(this.sprite);
		Image crosshair = new Image(crossDraw);
		crosshair.setPosition( Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
		
		stage.addActor(crosshair);
	}
	
	private void doneLoading () {
		Model model = assets.get("landscape.g3dj", Model.class);
        
		for (int i = 0; i < model.nodes.size; i++) {
			String id = model.nodes.get(i).id;
			
			float x = model.nodes.get(i).scale.x;
			float z = model.nodes.get(i).scale.y;
			float y = model.nodes.get(i).scale.z / 2;
			
			if(id.equals("Grid")) {
				constructors.insert(1, id, new GameObject.Constructor(model, id, new btBoxShape(new Vector3(x, y, z)),  0f));
				GameObject gridObject = constructors.get(id).construct();
		        instances.add(gridObject);
		        dynamicsWorld.addRigidBody(gridObject.body);
		        
			} else if(id.equals("Sphere")) {
				constructors.insert(2, id, new GameObject.Constructor(model, id, new btSphereShape(50),  0f));
				GameObject gridObject = constructors.get(id).construct();
		        instances.add(gridObject);
			}
		}
		loading = false;
	}
	
	public void shoot(float x, float y, float power) {
		System.out.println("Shooting!");
		Ray ray = cam.getPickRay(x, y);
		
		GameObject bullet = constructors.get("sphere").construct();
		instances.add(bullet);
		
		bullet.transform.trn(ray.origin.x, ray.origin.y, ray.origin.z);
		bullet.body.proceedToTransform(bullet.transform);
		bullet.body.setUserValue(instances.size);
		bullet.body.setCollisionFlags(bullet.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        
		((btRigidBody)bullet.body).applyCentralImpulse(ray.direction.scl(power));
		dynamicsWorld.addRigidBody(bullet.body);
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		
	}
	
	@Override
	public void render() {
		if (loading && assets.update()) {
			doneLoading();
		}
        
		final float delta = Math.min(1f/30f, Gdx.graphics.getDeltaTime());
		
		dynamicsWorld.stepSimulation(delta, 5, 1f/60f);
		
        if ((spawnTimer -= delta) < 0) {
            spawn();
            spawnTimer = 0.05f;
        }
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        visibleCount = 0;
        modelBatch.render(instances, environment);
        modelBatch.end();
        
        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Visible: ").append(visibleCount);
        label.setText(stringBuilder);
        stage.draw();
        
        update();
        input();
	}
	
	public void input() {
		if(Gdx.input.justTouched()) {
			shoot(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 500);
		}
	}
	
	public void update () {
		camController.update();
		ghostObject.setWorldTransform(characterTransform);
	}
	
	public void spawn() {
		GameObject obj = constructors.values[3 + MathUtils.random(constructors.size - 4)].construct();
        
		obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-90.0f, 90.0f), 20f, MathUtils.random(-90.0f, 90.0f));
        obj.body.proceedToTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        
        instances.add(obj);
        dynamicsWorld.addRigidBody(obj.body);
    }

	@Override
	public void dispose() {
		for (GameObject obj : instances)
	        obj.dispose();
	    instances.clear();
	
	    for (GameObject.Constructor ctor : constructors.values())
	        ctor.dispose();
	    constructors.clear();

        dispatcher.dispose();
        collisionConfig.dispose();
        
        contactListener.dispose();
        
		modelBatch.dispose();
        instances.clear();
        assets.dispose();
        model.dispose();
        
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        
        broadphase.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	
}
