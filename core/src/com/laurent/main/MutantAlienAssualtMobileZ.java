package com.laurent.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.laurent.main.Screens.TitleScreen;
import com.laurent.main.Tools.MyAssetManager;

public class MutantAlienAssualtMobileZ extends Game {

    public static enum Status{
        MIDAIR(0), MEDIUM(1), HIGH(2);
		public static final int size = 3;
        public int value;
        Status(int value) {
            this.value = value;
        }
    }

	public static enum Speed{
		SLOW(1), SWIFT(10), FAST(15),
		HIGH_SPEED(80), SONIC(100), SUPER_SONIC(500);
		public static final int size = 3;
		public int value;
		Speed(int value) {
			this.value = value;
		}
	}

	//[Old] Keep until new replaces functionality completely
	public static final int UNIT_SCALE = 16;

	public static final int V_WIDTH = 25;
    public static final int V_HEIGHT = 14;

    public static final float ONE_METER_WIDTH = 1/V_WIDTH;
    public static final float ONE_METER_HEIGHT = 1/V_HEIGHT;

    public static final float PPM = 100; //Pixels per meter


    public static final int MAX_INPUT_SPEED = 9;


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
    public static final short BACKGROUND_MACHINE_BIT = 128;
    public static final short ITEM_BIT = 256;


	@Override
	public void create () {
		batch = new SpriteBatch();


		ass_man = new MyAssetManager();
		ass_man.loadMusic();
		ass_man.loadSounds();
		ass_man.manager.finishLoading();

		setScreen(new TitleScreen(this));
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