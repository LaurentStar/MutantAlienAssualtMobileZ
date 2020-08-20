package com.laurent.main.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
        cam = new OrthographicCamera();
        viewport = new FitViewport(MutantAlienAssualtMobileZ.V_WIDTH, MutantAlienAssualtMobileZ.V_HEIGHT, cam);
        stage = new Stage(viewport, sb);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();

        table.left().bottom();

        move_left = false;
        move_right = false;
        fire_weapon = false;
        jump = false;

        // Left button
        Image left_image = new Image(new Texture("button_left.png"));
        left_image.setSize(50, 50);
        left_image.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                move_left = true;
                return true;
                //return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                move_left = false;
                //super.touchUp(event, x, y, pointer, button);
            }
        });


        // Right button
        Image right_image = new Image(new Texture("button_right.png"));
        right_image.setSize(50, 50);
        right_image.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                move_right = true;
                return true;
                //return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                move_right = false;
                //super.touchUp(event, x, y, pointer, button);
            }
        });

        // Fire button
        Image fire_image = new Image(new Texture("button_fire.png"));
        fire_image.setSize(50, 50);
        fire_image.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                fire_weapon = true;
                return true;
                //return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                fire_weapon = false;
                //super.touchUp(event, x, y, pointer, button);
            }
        });


        // Fire button
        Image jump_image = new Image(new Texture("button_jump.png"));
        jump_image.setSize(50, 50);
        jump_image.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                jump = true;
                return true;
                //return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                jump = false;
                //super.touchUp(event, x, y, pointer, button);
            }
        });


        table.add(left_image).size(MutantAlienAssualtMobileZ.V_WIDTH *15/100, left_image.getHeight());
        table.add(right_image).size(MutantAlienAssualtMobileZ.V_WIDTH *35/100, right_image.getHeight());
        table.add(fire_image).size(MutantAlienAssualtMobileZ.V_WIDTH *30/100, fire_image.getHeight());
        table.add(jump_image).size(MutantAlienAssualtMobileZ.V_WIDTH *15/100, jump_image.getHeight());
        table.add().size(MutantAlienAssualtMobileZ.V_WIDTH *5/100, jump_image.getHeight());
        stage.addActor(table);
    }

    public void draw(){
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
