package com.laurent.main.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.laurent.main.MutantAlienAssualtMobileZ;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private Integer world_timer;
    private static Integer score;
    private float time_count;
    private int ammo;

    Label countdown_label;
    private static Label medal_label;
    Label time_label;
    Label level_label;
    Label ammo_label;
    Label ammo_count;
    OrthographicCamera cam;
    private Container<?> container;


    public Hud(SpriteBatch sb){

        ammo = 78;
        Stack stack = new Stack();
        cam = new OrthographicCamera();
        viewport = new StretchViewport(MutantAlienAssualtMobileZ.V_WIDTH, MutantAlienAssualtMobileZ.V_HEIGHT, cam);
        stage = new Stage(viewport, sb);

        world_timer = 300;
        time_count = 0;
        score = 0;

        Table root = new Table();
        root.setFillParent(true);

        Table table_bg = new Table();

        Table table = new Table();
        //table.setFillParent(true);

        /*countdown_label = new Label(String.format("%03d", world_timer), new Label.LabelStyle(new BitmapFont(Gdx.files.internal("assualt_font.fnt")), Color.WHITE));
        score_label = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        time_label = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        level_label = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        world_label = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        redbar_droid_label = new Label("REDBAR", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(redbar_droid_label).size(MutantAlienAssualtMobileZ.V_WIDTH *15/100, MutantAlienAssualtMobileZ.V_HEIGHT*15/100);
        table.add(world_label).expandX().padTop(10);
        table.add(time_label).expandX().padTop(10);
        table.add(redbar_droid_label).expandX().padTop(10);
        table.row();
        table.add(score_label).expandX();
        table.add(level_label).expandX();
        table.add(countdown_label).expandX();*/

        Image bg = new Image(new Texture("solid_background.png"));
        bg.setColor(0f, 0f, 0f, 1f);
        table_bg.add(bg).size(MutantAlienAssualtMobileZ.V_WIDTH, 2f);

        medal_label = new Label("Medal", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("pixel_unicode.fnt")), Color.WHITE));
        container = new Container<Label>(medal_label);
        container.setTransform(true);   // for enabling scaling and rotation
        container.size(MutantAlienAssualtMobileZ.V_WIDTH*20/100, 0f);
        container.setScale(0.05f);
        container.setOrigin(0, container.getHeight());
        container.setPosition(0,0);

        ammo_label = new Label("Ammo", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("pixel_unicode.fnt")), Color.WHITE));
        Container container_ammo = new Container<Label>(ammo_label);
        container_ammo .setTransform(true);   // for enabling scaling and rotation
        container_ammo .size(MutantAlienAssualtMobileZ.V_WIDTH*20/100, 0f);
        container_ammo .setScale(0.05f);

        ammo_count = new Label("(1234567890)[\\]", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("Pixel_lcd_machine.fnt")), Color.WHITE));
        Container container_ammo_count = new Container<Label>(ammo_count);
        container_ammo_count.setTransform(true);   // for enabling scaling and rotation
        container_ammo_count.size(MutantAlienAssualtMobileZ.V_WIDTH *20/100, 0f);
        container_ammo_count.setScale(0.05f);


        table.add(container);
        table.add(container_ammo);
        table.add(container_ammo_count).left().center().expand();

        stack.add(table_bg);
        stack.add(table);

        root.add(stack).left().top().expand();

        stage.addActor(root);
    }

    public void update(float dt){
        /*time_count += dt;
        if(time_count >= 1){
            world_timer--;
            countdown_label.setText(String.format("%03d", world_timer));
            time_count = 0;
        }*/
    }

    public void draw(){
        //stage.act();
        stage.draw();
    }

    public static void addScore(int value){
        score += value;
        //score_label.setText(String.format("%03d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void resize(int width, int height){
        viewport.update(width, height);
    }
}

