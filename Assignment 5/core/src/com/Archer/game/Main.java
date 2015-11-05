package com.Archer.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
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
		public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {
			if(colObj0.getUserValue() == 1337) {
				// Is a target
				GameObject target = targets.get(colObj0.getUserIndex());
				
				System.out.println(colObj0.getUserIndex());
				target.doRender = false;
				targetsHit++;
				if(targetsHit < targetsToHit) {
					spawnTarget();
				}
				
			}
		  }
    }
	
	static class GameObject extends ModelInstance implements Disposable {
        public final btRigidBody body;
        public final MyMotionState motionState;
        public boolean doRender;
        
        public GameObject (Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
            super(model, node);
            motionState = new MyMotionState();
            motionState.transform = transform;
            body = new btRigidBody(constructionInfo);
            body.setMotionState(motionState);
            body.setRestitution(0.6f);
            body.setRollingFriction(100);
            this.doRender = true;
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
    protected Label infoText;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;
	boolean collision;
	float spawnTimer;
	
    Model model;
    
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    
    Array<GameObject> instances;
    Array<GameObject> targets;
    
    ArrayMap<String, GameObject.Constructor> constructors;
    ArrayMap<String, GameObject.Constructor> staticObjects;

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
	
	Texture woodTex;
	
	int score;
	
	int targetsHit;
	int targetsToHit;
	
	boolean paused;
	
	float timeLeft;
	
	boolean level1Finished;
    
    
	@Override
	public void create() {
		Bullet.init();
		
		paused = true;
		targetsHit = 0;
		targetsToHit = 5;
		
		timeLeft = 0.5f * 60;
		
		level1Finished = false;
        
		// Set 2D stage for 2D UI
		stage = new Stage();
        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
        
        infoText = new Label("Welcome to my physics simulator."
        					+ " \n Use the mouse to look around,"
			        		+ " \n WASD keys to move around,"
			        		+ " \n and the left mouse button to shoot.\n "
			        		+ " \n Shoot five the targets within the allotted time."
			        		+ " \n Press SpaceBar to start the game",
			        		new Label.LabelStyle(font, Color.WHITE));
        
        infoText.setPosition(Gdx.graphics.getWidth()/2 - infoText.getWidth() / 2, Gdx.graphics.getHeight() / 2 + Gdx.graphics.getHeight() / 6);
        
        stage.addActor(label);
        stage.addActor(infoText);
        
        stringBuilder = new StringBuilder();
        
        // Our model batch that we will render
		modelBatch = new ModelBatch();
		
		instances = new Array<GameObject>();
		targets = new Array<GameObject>();
		
		// Create our environment and lighting
		environment = new Environment();
	    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
	    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0, 50f, 0));
	    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0, -50f, 0));
	    
	    // Create the perspective camera
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 0f);
        cam.lookAt(1,10f,0);
        cam.near = 1.0f;
        cam.far = 2000.0f;
        cam.update();
        
        // Set the camera controller
        camController = new FPSCameraController(cam);
        
        Gdx.input.setCursorCatched(true);
        
        
        //final Texture texture = new Texture(Gdx.files.internal("grass.jpg"));
        final Texture ironTex = new Texture(Gdx.files.internal("iron.jpg"));
        woodTex = new Texture(Gdx.files.internal("gameWood.jpg"));
        
        // Build some models
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
            .sphere(1f, 1f, 1f, 20, 20);
        
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(ironTex),ColorAttribute.createSpecular(1,1,1,1), FloatAttribute.createShininess(8f)))
            .box(2.5f, 2.5f, 2.5f);
        
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
            .cone(1f, 2f, 1f, 10);
        
        mb.node().id = "character";
        mb.part("character", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(ironTex),ColorAttribute.createSpecular(1,1,1,1), FloatAttribute.createShininess(8f)))
            .capsule(5f, 20f, 16);
        
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
            .capsule(.5f, 2f, 10);
        
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
            .cylinder(1f, 2f, 1f, 10);
        
        mb.node().id = "target";
        mb.part("target", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
            .cylinder(5f, 1f, 5f, 30);
        
        model = mb.end();
        staticObjects = new ArrayMap<String, GameObject.Constructor>(String.class, GameObject.Constructor.class);
        constructors = new ArrayMap<String, GameObject.Constructor>(String.class, GameObject.Constructor.class);
        constructors.put("sphere", new GameObject.Constructor(model, "sphere", new btSphereShape(0.5f), 1f));
        constructors.put("box", new GameObject.Constructor(model, "box", new btBoxShape(new Vector3(1.25f, 1.25f, 1.25f)), 1f));
        constructors.put("cone", new GameObject.Constructor(model, "cone", new btConeShape(0.5f, 2f), 1f));
        constructors.put("capsule", new GameObject.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f), 1f));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10, 0));
        
        contactListener = new MyContactListener();
        
        staticObjects.put("target", new GameObject.Constructor(model, "target", new btCylinderShape(new Vector3(2.5f, 0.5f, 2.5f)), 0));
        
        
        
		staticObjects.put("character", new GameObject.Constructor(model, "character",  new btCapsuleShape(5f, 10f), 1f));
		character = staticObjects.get("character").construct();
		character.transform.trn(0, 8f, 0);
		characterTransform = character.transform;
		instances.add(character);
		
		// Create the physics representation of the character
		ghostObject = new btPairCachingGhostObject();
		ghostObject.setWorldTransform(characterTransform);
		ghostShape = new btCapsuleShape(5f, 10f);
		ghostObject.setCollisionShape(ghostShape);
		ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
		//dynamicsWorld.addCollisionObject(ghostObject);
		
		
		camController.characterTransform = characterTransform;

        assets = new AssetManager();
        assets.load("scene.g3dj", Model.class);
        loading = true;
        
        // Setup cross hair
        int w = Gdx.graphics.getWidth() / 50;
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
		
		spawnTarget();
        
        score = 0;
	}
	
	private void doneLoading () {
		Model landscape = assets.get("scene.g3dj", Model.class);
        
		for (int i = 0; i < landscape.nodes.size; i++) {
			String id = landscape.nodes.get(i).id;
			
			float x = landscape.nodes.get(i).scale.x;
			float z = landscape.nodes.get(i).scale.y;
			float y = landscape.nodes.get(i).scale.z;
			
			if(id.equals("Grid")) {
				staticObjects.put(id, new GameObject.Constructor(landscape, id, new btBoxShape(new Vector3(x, y / 2, z)),  0f));
				GameObject gridObject = staticObjects.get(id).construct();
		        instances.add(gridObject);
		        dynamicsWorld.addRigidBody(gridObject.body);
		        
			} else if(id.equals("Sphere")) {
				staticObjects.put(id, new GameObject.Constructor(landscape, id, new btSphereShape(50),  0f));
				GameObject sphereObject = staticObjects.get(id).construct();
		        instances.add(sphereObject);
		        
			} else if(id.equals("roof")) {
				staticObjects.put(id, new GameObject.Constructor(landscape, id, new btBoxShape(new Vector3(x, y, z)),  0f));
				GameObject roofObject = staticObjects.get(id).construct();
		        instances.add(roofObject);
		        
		        roofObject.transform.trn(0, 21f, 0);
		        roofObject.transform.rotate(0, 0, 1, 10);
		        roofObject.body.proceedToTransform(roofObject.transform);
		        
		        dynamicsWorld.addRigidBody(roofObject.body);
		        
			} else if(id.equals("floor")) {
				staticObjects.put(id, new GameObject.Constructor(landscape, id, new btBoxShape(new Vector3(x, y, z)),  0f));
				GameObject roofObject = staticObjects.get(id).construct();
		        instances.add(roofObject);
		        
		        roofObject.transform.trn(0, 0.1f, 0);
		        roofObject.body.proceedToTransform(roofObject.transform);
		        
			}
		}
		
		ModelBuilder mb = new ModelBuilder();
		mb.begin();
		
        mb.node().id = "wall";
        mb.part("wall", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(woodTex),ColorAttribute.createSpecular(0,0,0,0), FloatAttribute.createShininess(0f)))
            .box(40f, 40f, 1f);
        
        mb.node().id = "wall1";
        mb.part("wall1", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(woodTex),ColorAttribute.createSpecular(0,0,0,0), FloatAttribute.createShininess(0f)))
            .box(20f, 20f, 1f);
        
        mb.node().id = "wall2";
        mb.part("wall2", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, new Material(TextureAttribute.createDiffuse(woodTex),ColorAttribute.createSpecular(0,0,0,0), FloatAttribute.createShininess(0f)))
            .box(40f, 10, 1f);
        
        Model model = mb.end();
        
        staticObjects.put("wall", new GameObject.Constructor(model, "wall", new btBoxShape(new Vector3(20, 20f, 0.5f)),  0f));
		GameObject wall = staticObjects.get("wall").construct();
        instances.add(wall);
        
        wall.transform.trn(-10,0,0);
        wall.transform.rotate(0, 1, 0, 90);
        wall.body.proceedToTransform(wall.transform);
        
        dynamicsWorld.addRigidBody(wall.body);
        
        staticObjects.put("wall1", new GameObject.Constructor(model, "wall1", new btBoxShape(new Vector3(10, 10f, 0.5f)),  0f));
		wall = staticObjects.get("wall1").construct();
        instances.add(wall);
        
        wall.transform.trn(0,0,20);
        wall.body.proceedToTransform(wall.transform);
        
        dynamicsWorld.addRigidBody(wall.body);
        
        staticObjects.put("wall1", new GameObject.Constructor(model, "wall1", new btBoxShape(new Vector3(10, 10f, 0.5f)),  0f));
		wall = staticObjects.get("wall1").construct();
        instances.add(wall);
        
        wall.transform.trn(0,0,-20);
        wall.body.proceedToTransform(wall.transform);
        
        dynamicsWorld.addRigidBody(wall.body);
        
        staticObjects.put("wall2", new GameObject.Constructor(model, "wall2", new btBoxShape(new Vector3(20, 5f, 0.5f)),  0f));
		wall = staticObjects.get("wall2").construct();
        instances.add(wall);
        
        wall.transform.trn(10,0,0);
        wall.transform.rotate(0, 1, 0, 90);
        wall.body.proceedToTransform(wall.transform);
        
        dynamicsWorld.addRigidBody(wall.body);
		
		loading = false;
	}
	
	public void shoot(float x, float y, float power) {
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
		
		timeLeft -= delta;
		
		//int minutes = ((int)timeLeft) / 60;
	    int seconds = ((int)timeLeft) % 60;
	    
	    if(seconds <= 0 || targetsHit >= targetsToHit) {
	    	seconds = 0;
    		// Next level, give score
    		if(!level1Finished) {
    			score += 1000;
    		}
    		level1Finished = true;
	    }
	    
		dynamicsWorld.stepSimulation(delta, 5, 1f/60f);
		
        if ((spawnTimer -= delta) < 0) {
            //spawn();
            spawnTimer = 0.5f;
        }
        
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        for(GameObject target : targets) {
        	if(target.doRender) {
        		modelBatch.render(target, environment);
        	} else {
        		dynamicsWorld.removeRigidBody(target.body);
        	}
        }
        
        modelBatch.end();
        
        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Score: ").append(score);
        stringBuilder.append(" Time: ").append(seconds);
        label.setText(stringBuilder);
        stage.draw();
        
        update();
        input();
	}
	
	public void input() {
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			if(paused) {
				infoText.setText(" ");
				Gdx.input.setInputProcessor(camController);
				paused = !paused;
			} else {
				infoText.setText("Paused!");
				infoText.setPosition(Gdx.graphics.getWidth()/2 - infoText.getWidth() / 2, Gdx.graphics.getHeight() / 2 + Gdx.graphics.getHeight() / 4);
				Gdx.input.setInputProcessor(null);
				paused = !paused;
			}
			
		}
		
		if(!paused) {
			if(Gdx.input.justTouched()) {
				shoot(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 60);
			}
		}
	}
	
	public void update () {
		camController.update();
		//ghostObject.setWorldTransform(characterTransform);
	}
	
	public void spawn() {
		GameObject obj = constructors.values[MathUtils.random(constructors.size - 1)].construct();
         
		obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-90.0f, 90.0f), 50f, MathUtils.random(-90.0f, 90.0f));
        obj.body.proceedToTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        
        instances.add(obj);
        dynamicsWorld.addRigidBody(obj.body);
    }
	
	public void spawnTarget() {
		GameObject target = staticObjects.get("target").construct();
		
        target.transform.trn(MathUtils.random(100.0f, 50.0f), MathUtils.random(10.0f, 25.0f), MathUtils.random(-20.0f, 20.0f));
        target.transform.rotate(0, 0, 1, 90);
        target.body.proceedToTransform(target.transform);
        target.body.setUserValue(1337);
        target.body.setCollisionFlags(target.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        target.body.setUserIndex(targets.size);
        
        targets.add(target);
        dynamicsWorld.addRigidBody(target.body);
	}

	@Override
	public void dispose() {
		for (GameObject obj : instances)
	        obj.dispose();
	    instances.clear();
	    
	    for (GameObject obj : targets)
	        obj.dispose();
	    targets.clear();
	    
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
