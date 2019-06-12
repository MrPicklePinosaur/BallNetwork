/*
 ______   __       ______   ______   ______   __
/\  ___\ /\ \     /\  __ \ /\  == \ /\  __ \ /\ \
\ \ \__ \\ \ \____\ \ \/\ \\ \  __< \ \  __ \\ \ \____
 \ \_____\\ \_____\\ \_____\\ \_____\\ \_\ \_\\ \_____\
  \/_____/ \/_____/ \/_____/ \/_____/ \/_/\/_/ \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;

class Global {

	public static Skin skin = new Skin(Gdx.files.internal("gdx-skins/level-plane/skin/level-plane-ui.json"));
	public static BallClientMain game;
	public static Json json = new Json();

	public static UserData user_data;
	public static ChatLog chatlog;
	public static Camera camera;

	//Important vars
	public static BallClient server_socket;
	public static String server_ip = "127.0.0.1";
	public static int server_port = 5000;

	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 800;
	public static final float RESOLUTION = (float)SCREEN_HEIGHT/SCREEN_WIDTH;
	public static int PPM = 100;

	public static final int SPRITESIZE = 40;
	public static final float WEAPONSCALE = 0.8f;

	public static float m_x = 0;
	public static float m_y = 0;
	public static float m_angle = 0;

	public static void updateInput() {
		Global.m_x = Gdx.input.getX();
		Global.m_y = Gdx.input.getY();
		Global.m_angle = -1*MathUtils.atan2(m_y-SCREEN_HEIGHT/2f,m_x-SCREEN_WIDTH/2f);
	}

}

enum MT {
	//Message types - output
	USIN, CHATMSG, CMD, RESPAWN,

	CHECKCREDS, REGISTER, STARTGAME, LEAVEGAME,

	//Message types (MT) - input
	UPDATEENTITY, KILLENTITY, LOADMAP, SENDCHAT, BINDCAM, UPDATEPARTICLE, CHOOSECLASS,

	CREDSACCEPTED, CREDSDENIED, REGISTERSUCCESS, REGISTERFAILED,
}

enum ET {
	PLAYER, ENEMY, PROJECTILE, STATIC, WEAPON
}
