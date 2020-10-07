package com.laurent.main.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Tools.MyAssetManager;

public class TitleScreen implements Screen {

    private Music music;
    private MyAssetManager ass_man;
    private Viewport viewport;
    private Stage stage;
    private MutantAlienAssualtMobileZ game;

    public TitleScreen(MutantAlienAssualtMobileZ game){
        this.game = game;

        viewport = new StretchViewport(MutantAlienAssualtMobileZ.V_WIDTH, MutantAlienAssualtMobileZ.V_HEIGHT,  new OrthographicCamera());

        stage = new Stage(viewport, ((MutantAlienAssualtMobileZ) game).batch);

        Image title_bg = new Image(new Texture("title_screen_bg.png"));

        Table table = new Table();

        table.left().bottom();

        table.add(title_bg).size(MutantAlienAssualtMobileZ.V_WIDTH, MutantAlienAssualtMobileZ.V_HEIGHT);

        stage.addActor(table);

        ass_man = game.getMyAssetManager();
        music = ass_man.manager.get(ass_man.MUSIC_E_VS_S_TITLE, Music.class);
        music.play();
        music.setLooping(true);
    }



    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()) {
            music.stop();
            game.setScreen(new PlayScreen((MutantAlienAssualtMobileZ) game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();

    }
}
