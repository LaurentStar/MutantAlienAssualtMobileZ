package com.laurent.main.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;
import java.lang.Math;

public abstract class Enemy extends Sprite{

    public enum State {FALLING, JUMPING, RUNNING, IDLE, DEAD};

    protected World world;
    protected PlayScreen screen;
    protected boolean status_flags[];
    protected float state_time;
    protected float state_timer;
    protected boolean leftFalse_rightTrue;
    protected boolean set_to_destroy;
    public Body box_2d_body;
    public Vector2 velocity;

    protected int health;


    Enemy(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);

        defineEnemy();
        velocity = new Vector2(1.5f, 0);
        box_2d_body.setActive(false);
    }

    protected abstract void defineEnemy();
    public abstract void update(float dt);
    public abstract State getState();
    public abstract void onHitDamage(int damage);
    public abstract boolean isDestroyed();
    public abstract TextureRegion getFrame(float dt);
    public void setStatusFlag(MutantAlienAssualtMobileZ.Status flag, boolean value){
        switch(flag){
            case MIDAIR:
            case MEDIUM:   status_flags[flag.value] = value; break;
        }

    }

    public void reverseVelocity(boolean x, boolean y){
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;

        box_2d_body.setLinearVelocity(velocity);
    }
    public void moveLeftFalseOrRightTrue(boolean  leftFalse_rightTrue){
        velocity.x = leftFalse_rightTrue
                ?  Math.abs(velocity.x) //right
                : -Math.abs(velocity.x); //left
        box_2d_body.setLinearVelocity(velocity);
    }

    public void outOfBounds(){
        // Destory all objects that leave the level boundaries
        if((box_2d_body.getPosition().x < 0) || (box_2d_body.getPosition().x > screen.getLevelWidth()))
            set_to_destroy =  true;
        if((box_2d_body.getPosition().y < 0) || (box_2d_body.getPosition().y > screen.getLevelHeight()))
            set_to_destroy =  true;
    }
}
