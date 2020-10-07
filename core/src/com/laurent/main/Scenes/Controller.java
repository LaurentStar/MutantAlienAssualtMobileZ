package com.laurent.main.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.laurent.main.MutantAlienAssualtMobileZ;

public class Controller {

    Viewport viewport;
    Stage stage;
    private Boolean move_left, move_right, fire_weapon, jump;
    OrthographicCamera cam;

    public Controller(SpriteBatch sb) {
        Stack stack = new Stack();
        cam = new OrthographicCamera();
        viewport = new FitViewport(MutantAlienAssualtMobileZ.V_WIDTH, MutantAlienAssualtMobileZ.V_HEIGHT, cam);
        stage = new Stage(viewport, sb);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.left().bottom();

        Table table_graphic = new Table();
        table_graphic.left().bottom();

        move_left = false;
        move_right = false;
        fire_weapon = false;
        jump = false;

        // Left button
        Image left_button = new Image();
        //left_image.setSize(50, 50);
       // left_image.setColor(1f, 1f, 1f, 0f);
        left_button.addListener(new InputListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor toActor){
                move_left = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
                move_left = false;
            }
        });


        // Right button
        Image right_button = new Image();
        //right_image.setSize(50, 50);
        //right_image.setColor(1f, 1f, 1f, 0f);
        right_button.addListener(new InputListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor toActor){
                move_right = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
                move_right = false;
            }
        });

        // Fire button
        Image fire_button = new Image();
        //fire_image.setSize(50, 50);
        //fire_image.setColor(1f, 1f, 1f, 0f);
        fire_button.addListener(new InputListener(){

            /*@Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fire_weapon = true;
                return true;
                //return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                fire_weapon = false;
                //super.touchUp(event, x, y, pointer, button);
            }*/
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor toActor){
                fire_weapon = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
                fire_weapon = false;
            }
        });


        // Fire button
        Image jump_button = new Image();
       // jump_image.setSize(50, 50);
        //jump_image.setColor(1f, 1f, 1f, 0f);
        jump_button.addListener(new InputListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor toActor){
                jump = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
                jump = false;
            }
            /*@Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                jump = true;
                return true;
                //return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                jump = false;
                //super.touchUp(event, x, y, pointer, button);
            }*/
        });


        table.add(left_button).size(MutantAlienAssualtMobileZ.V_WIDTH *15/100, MutantAlienAssualtMobileZ.V_HEIGHT);
        table.add(right_button).size(MutantAlienAssualtMobileZ.V_WIDTH *35/100, MutantAlienAssualtMobileZ.V_HEIGHT);
        table.add(fire_button).size(MutantAlienAssualtMobileZ.V_WIDTH *30/100, MutantAlienAssualtMobileZ.V_HEIGHT);
        table.add(jump_button).size(MutantAlienAssualtMobileZ.V_WIDTH *15/100, MutantAlienAssualtMobileZ.V_HEIGHT);
        table.add().size(MutantAlienAssualtMobileZ.V_WIDTH *5/100, MutantAlienAssualtMobileZ.V_HEIGHT);
       // stage.addActor(table);


        Image jump_image = new Image(new Texture("button_jump.png"));
        jump_image.setColor(1f, 1f, 1f, 0.5f);
        Image fire_image = new Image(new Texture("button_fire.png"));
        fire_image.setColor(1f, 1f, 1f, 0.5f);
        Image right_image = new Image(new Texture("button_right.png"));
        right_image.setColor(1f, 1f, 1f, 0.5f);
        Image left_image = new Image(new Texture("button_left.png"));
        left_image.setColor(1f, 1f, 1f, 0.5f);

        table_graphic.add(left_image).size(MutantAlienAssualtMobileZ.V_WIDTH *15/100, MutantAlienAssualtMobileZ.V_HEIGHT/3);
        table_graphic.add(right_image).size(MutantAlienAssualtMobileZ.V_WIDTH *35/100, MutantAlienAssualtMobileZ.V_HEIGHT/3);
        table_graphic.add(fire_image).size(MutantAlienAssualtMobileZ.V_WIDTH *30/100, MutantAlienAssualtMobileZ.V_HEIGHT/3);
        table_graphic.add(jump_image).size(MutantAlienAssualtMobileZ.V_WIDTH *15/100, MutantAlienAssualtMobileZ.V_HEIGHT/3);
        table_graphic.add().size(MutantAlienAssualtMobileZ.V_WIDTH *5/100, MutantAlienAssualtMobileZ.V_HEIGHT/3);



        stack.add(table_graphic);
        stack.add(table);
        stage.addActor(stack);

    }

    public void draw(){
        stage.act();
        stage.draw();
    }

    public Boolean isLeftPressed() {
        return move_left;
    }

    public Boolean isRightPressed(){
        return move_right;
    }

    public Boolean isFireWeaponPressed() {
        return fire_weapon;
    }

    public Boolean isJumpPressed() {
        return jump;
    }

    public void resize(int width, int height){
        viewport.update(width, height);
    }
}
