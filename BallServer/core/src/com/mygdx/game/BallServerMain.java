package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.IOException;

public class BallServerMain extends ApplicationAdapter {

	BallServer server;
	
	@Override
	public void create () {

		//Init server and such
		server = new BallServer(5000);
		server.start_server();

		//Thread that listens for connecting users
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					//keep waiting for new clients to show up
					while (true) { new BallClientHandler(server.getServerSocket().accept()); }
				} catch(IOException ex) { System.out.println(ex); }
			}
		}).start(); //auto start thread

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	}
	
	@Override
	public void dispose () {

	}
}
