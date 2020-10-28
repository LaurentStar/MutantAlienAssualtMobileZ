package com.laurent.main.Sprites.Items;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.Sprites.Red_Droid;

public class Bullet extends Item {

    private int damage;

    public Bullet(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("walk"), 0, 0, 16, 16);
        damage = 1;

    }

    @Override
    public void defineItem() {
        BodyDef body_def = new BodyDef();
        body_def.position.set(getX(), getY());
        body_def.type = BodyDef.BodyType.KinematicBody;
        box_2d_body = world.createBody(body_def);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f / 2, 0.5f / 2);

        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.BULLET_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.DEFUALT_BIT |
                MutantAlienAssualtMobileZ.BRICK_BIT |
                MutantAlienAssualtMobileZ.OBJECT_BIT |
                MutantAlienAssualtMobileZ.ENEMY_BIT;

        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);
    }

    public int hitDamage(){
        /* This method puts a bullet to sleep and returns the damage inflicted by it*/
        box_2d_body.setAwake(false);
        return damage;
    }

    public void setterFireBullet(float x, float y, Red_Droid.Weapon weapon, boolean direction_lf_rt) {
        /*This method is used to initialize a bullet starting position, velocity, type and damage.
        * It can only be called when the bullet body is sleeping*/
        x_position = x;
        y_position = y;

        switch(weapon) {
            default:
                damage = 8;
                // Graphic to use

        }


        if(direction_lf_rt){
            velocity.x = velocity.x > 0 ? -velocity.x : velocity.x;
        }
        else{
            velocity.x = velocity.x < 0 ? -velocity.x : velocity.x;
        }


        if ((box_2d_body.getLinearVelocity().x < 0 || !direction_lf_rt) && !render_item.isFlipX()){
            render_item.flip(true, false);
            direction_lf_rt = false;
        }
        else if ((box_2d_body.getLinearVelocity().x > 0 || direction_lf_rt) && render_item.isFlipX()){
            render_item.flip(true, false);
            direction_lf_rt = true;
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(box_2d_body.getPosition().x - getWidth()/2, box_2d_body.getPosition().y - getHeight()/2);
        velocity.y = box_2d_body.getLinearVelocity().y;
        box_2d_body.setLinearVelocity(velocity);
    }
}
