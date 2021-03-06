/* Shinobi Network
 __    __   ______   __   __   __
/\ "-./  \ /\  __ \ /\ \ /\ "-.\ \
\ \ \-./\ \\ \  __ \\ \ \\ \ \-.  \
 \ \_\ \ \_\\ \_\ \_\\ \_\\ \_\\"\_\
  \/_/  \/_/ \/_/\/_/ \/_/ \/_/ \/_/

	The place where everything happens :)
 */

package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.io.IOException;

public class BallServerMain extends ApplicationAdapter {

	//heavy lifters
	BallServer server;
	Box2DDebugRenderer debugRenderer;
	OrthographicCamera cam; //TODO: WE SHOULD NOT BE USING THIS
	OrthogonalTiledMapRenderer tiledMapRenderer;

	@Override
	public void create () {
		Thread.currentThread().setName("Main");

		//init assets
		AssetManager.load_all_json();

		//Connect to database
		Global.db = new Database();

		//init game world
		Global.game = new FFAGame();
		Global.world = new World(new Vector2(0,0),true);
		Global.world.setContactListener(new CollisionListener());

		//choose a map
		Global.map = new GameMap("maps/mt_ffa.tmx");

		//init heavy lifres
		debugRenderer = new Box2DDebugRenderer();

		//Visual debugging
		cam = new OrthographicCamera((float) 1400/Global.PPM,(float) 1400/Global.PPM);
		cam.zoom = 1.2f;
		cam.position.x = (float)1500/Global.PPM;
		cam.position.y = (float)1500/Global.PPM;
		cam.update();

		tiledMapRenderer = new OrthogonalTiledMapRenderer(Global.map.getTiledMap(),(float) 1/Global.PPM);
		tiledMapRenderer.setView(cam);

		//Init server and such
		server = new BallServer(5000);
		server.start_server();

		//Thread that listens for connecting users
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("AwaitClient");
				try {
					//keep waiting for new clients to show up
					while (true) {
						BallClientHandler client = new BallClientHandler(server.getServerSocket().accept());
						client.start_connection();
					}
				} catch(IOException ex) {
					System.out.println(ex+" aka, something went wrong when connecting the player.");
				}
			}
		}).start(); //auto start thread

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//periodically send client position of all entities
		Entity.send_all();
		Particle.send_particles();

		//draw stuff (TESTING ONLY)
		tiledMapRenderer.render();
		debugRenderer.render(Global.world,cam.combined);

		//update instances
		Ability.updateAll(Global.deltatime);
		ActiveEffect.updateAll(Global.deltatime);
		Player.updateAll(Global.deltatime);
		Projectile.updateAll();

		//update world
		Global.world.step(Global.deltatime,6,2); //step physics simulation
		AssetManager.sweepBodies();
		AssetManager.moveBodies();

	}
	
	@Override
	public void dispose () {
		Ability.dispose();
		server.close_server();
		tiledMapRenderer.dispose();
		debugRenderer.dispose();
		Global.disposeGlobals();
		Global.db.closeConnection();
		Gdx.app.exit();
	}
}
