package com.laurent.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.Tools.MyAssetManager;

import java.util.HashMap;

public class MutantAlienAssualtMobileZ extends Game {

	public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 225;
    public static final float PPM = 100; //Pixels per meter
    public SpriteBatch batch;
	private MyAssetManager ass_man;
	public static final short NOTHING_BIT = 0;
	public static final short DEFUALT_BIT = 1;
	public static final short RED_DROID_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
	private static HashMap files;

	@Override
	public void create () {
		batch = new SpriteBatch();


		ass_man = new MyAssetManager();
		ass_man.loadMusic();
		ass_man.loadSounds();
		ass_man.manager.finishLoading();

		setScreen(new PlayScreen(this));
		//img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		/*Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();*/

		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		ass_man.manager.dispose();
		//img.dispose();
	}

	public MyAssetManager getMyAssetManager() {
		return ass_man;
	}

}


///Convert the png to a tff