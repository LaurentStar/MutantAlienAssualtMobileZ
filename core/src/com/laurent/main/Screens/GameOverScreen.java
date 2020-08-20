package com.laurent.main.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.laurent.main.MutantAlienAssualtMobileZ;

public class GameOverScreen implements Screen {

    private Viewport viewport;
    private Stage stage;
    private Game game;

    public GameOverScreen(Game game){
        this.game = game;
        viewport = new StretchViewport(MutantAlienAssualtMobileZ.V_WIDTH,
                MutantAlienAssualtMobileZ.V_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, ((MutantAlienAssualtMobileZ) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label game_over_label = new Label("GAME OVER", font);
        Label try_again_label = new Label("TRY AGAIN..................................................", font);

        table.add(game_over_label).expandX();
        table.add();
        table.add(try_again_label).padTop(10f);


        stage.addActor(table);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()) {
            game.setScreen(new PlayScreen((MutantAlienAssualtMobileZ) game));
            dispose();
        }
        Gdx.gl.glClearColor(1, 0, 0, 1);
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
