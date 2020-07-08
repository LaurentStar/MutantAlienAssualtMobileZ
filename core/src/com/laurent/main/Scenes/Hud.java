package com.laurent.main.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.laurent.main.MutantAlienAssualtMobileZ;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private Integer world_timer;
    private static Integer score;
    private float time_count;

    Label countdown_label;
    private static Label score_label;
    Label time_label;
    Label level_label;
    Label world_label;
    Label redbar_droid_label;

    public Hud(SpriteBatch sb){
        world_timer = 300;
        time_count = 0;
        score = 0;

        viewport = new FitViewport( MutantAlienAssualtMobileZ.V_WIDTH,
                                    MutantAlienAssualtMobileZ.V_HEIGHT,
                                    new OrthographicCamera());

        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdown_label = new Label(String.format("%03d", world_timer), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("assualt_font.fnt")), Color.WHITE));
        score_label = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        time_label = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        level_label = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        world_label = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        redbar_droid_label = new Label("REDBAR", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(redbar_droid_label).expandX().padTop(10);
        table.add(world_label).expandX().padTop(10);
        table.add(time_label).expandX().padTop(10);
        table.add(redbar_droid_label).expandX().padTop(10);
        table.row();
        table.add(score_label).expandX();
        table.add(level_label).expandX();
        table.add(countdown_label).expandX();

        stage.addActor(table);
    }

    public void update(float dt){
        time_count += dt;
        if(time_count >= 1){
            world_timer--;
            countdown_label.setText(String.format("%03d", world_timer));
            time_count = 0;
        }
    }

    public static void addScore(int value){
        score += value;
        score_label.setText(String.format("%03d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
