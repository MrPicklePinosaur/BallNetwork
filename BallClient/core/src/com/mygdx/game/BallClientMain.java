/*
 __    __   ______   __   __   __
/\ "-./  \ /\  __ \ /\ \ /\ "-.\ \
\ \ \-./\ \\ \  __ \\ \ \\ \ \-.  \
 \ \_\ \ \_\\ \_\ \_\\ \_\\ \_\\"\_\
  \/_/  \/_/ \/_/\/_/ \/_/ \/_/ \/_/

 */

package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class BallClientMain extends Game {

	@Override
	public void create () {
		//Init calls
		AssetManager.loadAnimations("spritesheet_lib.txt");
		Gdx.graphics.setWindowedMode(Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);

		Particle.load_particles("particle_lib.txt");

		//Init server
		Global.server_socket = new BallClient("127.0.0.1",5000);
		Thread.currentThread().setName("Main");
		if (Global.server_socket.start_connection() == false) {
			 //client goes back to main screen
			Gdx.app.exit(); //for now the game just closes
		}

		Global.server_socket.send_msg(MT.CHECKCREDS,"daniel,password");

		MainmenuScreen main_menu = new MainmenuScreen();
		this.setScreen(main_menu);

		GameScreen game_screen = new GameScreen();
		Global.chatlog = new ChatLog(game_screen.getStage());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}
	
	@Override
	public void dispose () {
		//server_socket.close_connection(); //this line causes nullPointer on serverside for some reason
        Gdx.app.exit();
	}


}
