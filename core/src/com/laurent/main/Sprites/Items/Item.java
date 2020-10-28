package com.laurent.main.Sprites.Items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.laurent.main.Screens.PlayScreen;

public abstract class Item extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected Body box_2d_body;
    protected TextureRegion render_item;
    protected boolean to_destroy;
    protected boolean destroyed;
    protected float x_position;
    protected float y_position;


    public Item(PlayScreen screen, float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 16, 16);
        to_destroy = false;
        destroyed = false;
        defineItem();
    }

    public abstract void defineItem();
    //public abstract void use(Red_Droid red_droid);


    public void update(float dt){
        if(to_destroy && !destroyed){
            world.destroyBody(box_2d_body);
            destroyed = true;
        }
    }

    public void destroy(){
        to_destroy = true;
    }

    public void draw(Batch batch){
        if(!destroyed)
            super.draw(batch);
    }

    public void reverseVelocity(boolean x, boolean y){
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }
}
