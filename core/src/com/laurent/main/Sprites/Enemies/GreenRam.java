package com.laurent.main.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.Sprites.Red_Droid;

import java.util.AbstractMap;


public class GreenRam extends Enemy {

    public State current_state;
    public State previous_state;


    private Animation<TextureRegion> walk_animation;
    //private Array<TextureRegion> frames;
    private Sound sound;

    private boolean destroyed;

    public GreenRam(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        Array<TextureRegion> frames = new Array<TextureRegion>();
        //frames = new Array<TextureRegion>();
        for (int i=0; i<5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("green_ram_gallope_20x21"), i*20, 0, 20, 21));
        walk_animation = new Animation(0.1f, frames);
        frames.clear();

        state_timer = 0;
        state_time = 0;
        setBounds(getX(), getY(), 1, 1);
        set_to_destroy = false;
        destroyed = false;
        leftFalse_rightTrue = true;

    }

    public void update(float dt){
        state_time += dt;
        if(set_to_destroy && destroyed == false ) {
            safeDestroy();
        }
        else if (destroyed == false) {

            outOfBounds();
            setPosition(box_2d_body.getPosition().x - getWidth() / 2, box_2d_body.getPosition().y - getHeight() / 2);
            setRegion(getFrame(dt));

            if(box_2d_body.getLinearVelocity().x <= MutantAlienAssualtMobileZ.Speed.SLOW.value
                    && box_2d_body.getLinearVelocity().x >= -MutantAlienAssualtMobileZ.Speed.SLOW.value)
                box_2d_body.applyLinearImpulse(velocity, box_2d_body.getWorldCenter(), true);
        }
    }

    @Override
    public State getState(){


        if((box_2d_body.getLinearVelocity().y > 0 && current_state == State.JUMPING)
                || (box_2d_body.getLinearVelocity().y < 0 && previous_state == State.JUMPING))
            return State.JUMPING;

        else if (box_2d_body.getLinearVelocity().y < 0)
            return State.FALLING;

        else if (box_2d_body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.IDLE;

    }

    @Override
    public TextureRegion getFrame(float dt){
        current_state = getState();

        TextureRegion region;
        switch(current_state){
            //case DEAD: region = walk_animation; break;
            case JUMPING: region =  walk_animation.getKeyFrame(state_timer); break;
            case RUNNING: region = walk_animation.getKeyFrame(state_timer, true); break;
            case FALLING: region = walk_animation.getKeyFrame(state_timer, true); break;
            case IDLE:
            default: region = walk_animation.getKeyFrame(state_timer, true);; break;
        }

        if ((box_2d_body.getLinearVelocity().x < 0 || !leftFalse_rightTrue) && !region.isFlipX()){
            region.flip(true, false);
            leftFalse_rightTrue = false;
        }
        else if ((box_2d_body.getLinearVelocity().x > 0 || leftFalse_rightTrue) && region.isFlipX()){
            region.flip(true, false);
            leftFalse_rightTrue = true;
        }

        state_timer = current_state == previous_state ? state_timer + dt : 0;
        previous_state = current_state;
        return region;
    }

    @Override
    public void reverseVelocity(boolean x, boolean y) {
        super.reverseVelocity(x, y);
    }

    @Override
    public void moveLeftFalseOrRightTrue(boolean leftFalse_rightTrue){
        super.moveLeftFalseOrRightTrue(leftFalse_rightTrue);
    }

    @Override
    protected void defineEnemy() {
        BodyDef body_def = new BodyDef();
        body_def.position.set((getX() + getWidth()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE,
                (getY() + getHeight()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE);

        body_def.type = BodyDef.BodyType.DynamicBody;
        box_2d_body = world.createBody(body_def);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((0.9f/2), (0.9f/2) );

        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.ENEMY_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.DEFUALT_BIT |
                                MutantAlienAssualtMobileZ.BRICK_BIT |
                                MutantAlienAssualtMobileZ.COIN_BIT |
                                MutantAlienAssualtMobileZ.ENEMY_BIT |
                                MutantAlienAssualtMobileZ.OBJECT_BIT |
                                MutantAlienAssualtMobileZ.RED_DROID_BIT;
        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);

        EdgeShape sensor = new EdgeShape();
        sensor.set(new Vector2(0.55f, 0.40f), new Vector2(0.55f, -0.40f));
        fdef.shape = sensor;
        fdef.isSensor = true;
        AbstractMap.SimpleEntry<String, GreenRam> pair = new AbstractMap.SimpleEntry("enemy_right", this);
        box_2d_body.createFixture(fdef).setUserData(pair);

        sensor.set(new Vector2(-0.55f, 0.40f), new Vector2(-0.55f, -0.40f));
        fdef.shape = sensor;
        //fdef.isSensor = true;
        AbstractMap.SimpleEntry<String, GreenRam> pair_left = new AbstractMap.SimpleEntry("enemy_left", this);
        box_2d_body.createFixture(fdef).setUserData(pair_left);

        /*PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-0.5f, 0.8f);
        vertice[1] = new Vector2(0.5f, 0.8f);
        vertice[2] = new Vector2(-0.3f, 0.3f);
        vertice[3] = new Vector2(0.3f, 0.3f);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.ENEMY_HEAD_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.RED_DROID_BIT;
        box_2d_body.createFixture(fdef).setUserData(this);*/
    }

    @Override
    public void onHitDamage(int damage) {
        health -= damage;
        if(health==0)
            set_to_destroy = true;
        sound = screen.getAssMan().manager.get(screen.getAssMan().SOUND_METAL_CLICK);
        sound.play();
    }

    public void draw(Batch batch){
        if(!destroyed || state_time < 1){
            super.draw(batch);
        }
    }

    private void safeDestroy(){
        if(!world.isLocked()) {
            world.destroyBody(box_2d_body);
            destroyed = true;
            setRegion(screen.getAtlas().findRegion("green_ram_gallope_20x21"), 0, 0, 20, 21);
            state_time = 0;
            box_2d_body.setActive(false);
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
