package com.laurent.main.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;

public class Red_Droid extends Sprite {
    public enum State {FALLING, JUMPING, RUNNING, IDLE, DEAD};
    public State current_state;
    public State previous_state;

    public World world;
    public Body box_2d_body;
    private PlayScreen screen;
    private Sound sound;
    private float state_timer;
    //private TextureRegion red_droid_idle;
    private TextureRegion red_droid_dead;
    private Animation<TextureRegion> red_droid_idle;
    private Animation<TextureRegion> red_droid_running;
    private Animation<TextureRegion> red_droid_jumping;
    private Animation<TextureRegion> red_droid_falling;

    boolean leftFalse_rightTrue;
    boolean red_droid_is_dead;

    public Red_Droid(PlayScreen screen){
        //super (screen.getAtlas().findRegion("red_droid_idle_20x21"));
        this.world = screen.getWorld();
        this.screen = screen;

        current_state = State.IDLE;
        previous_state = State.IDLE;
        state_timer = 0;
        leftFalse_rightTrue = true;
        red_droid_is_dead = false;

        Array<TextureRegion> frames = new Array<TextureRegion>();


        // Idle Animation
        for (int e=0; e<1; e++)
            for (int i=0; i<5; i++)
                frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_idle_20x21"), i*20, 0, 20, 21));
            //Get frames in reverse
            for (int i=4; i>=0; i--)
                frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_idle_20x21"), i*20, 0, 20, 21));
        red_droid_idle = new Animation(0.25f, frames);
        frames.clear();


        // Running Animation
        for (int i=0; i<6; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_run_21x20"), i*21, 0, 21, 20));
        red_droid_running = new Animation(0.1f, frames);
        frames.clear();


        // Jumping Animation
        for (int i=1; i<5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_jump_20x21"), i*20, 0, 20, 21));
        red_droid_jumping = new Animation(0.1f, frames);
        frames.clear();

        // Falling Animation
        for (int i=1; i<2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_fall_20x21"), i*20, 0, 20, 21));
        red_droid_falling = new Animation(0.1f, frames);
        frames.clear();

        //Dead
        red_droid_dead = new TextureRegion(screen.getAtlas().findRegion("red_droid_dead_20x21"), 0, 0, 20, 21);


        defineRedDroid();


        setBounds(0, 0, 16/MutantAlienAssualtMobileZ.PPM, 16/MutantAlienAssualtMobileZ.PPM);

        setRegion(red_droid_idle.getKeyFrame(state_timer, true));
    }

    public void update(float dt){
        setPosition(box_2d_body.getPosition().x - getWidth() / 2,
                box_2d_body.getPosition().y - getHeight() / 2);

        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        current_state = getState();

        TextureRegion region;
        switch(current_state){
            case DEAD: region = red_droid_dead; break;
            case JUMPING: region =  red_droid_jumping.getKeyFrame(state_timer); break;
            case RUNNING: region = red_droid_running.getKeyFrame(state_timer, true); break;
            case FALLING: region = red_droid_falling.getKeyFrame(state_timer, true); break;
            case IDLE:
            default: region = red_droid_idle.getKeyFrame(state_timer, true);; break;
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

    public State getState(){

        if (red_droid_is_dead)
            return State.DEAD;
        
        else if((box_2d_body.getLinearVelocity().y > 0 && current_state == State.JUMPING)
                || (box_2d_body.getLinearVelocity().y < 0 && previous_state == State.JUMPING))
            return State.JUMPING;

        else if (box_2d_body.getLinearVelocity().y < 0)
            return State.FALLING;

        else if (box_2d_body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.IDLE;

    }

    public void jump(){
        if ( current_state != State.JUMPING && current_state != State.FALLING) {
            box_2d_body.applyLinearImpulse(new Vector2(0, 2.4f), box_2d_body.getWorldCenter(), true);
            current_state = State.JUMPING;
            sound = screen.getAssMan().manager.get(screen.getAssMan().SOUND_JUMP);
            sound.play();
        }
        else if (current_state == State.JUMPING && box_2d_body.getLinearVelocity().y > 0){
            box_2d_body.applyForce(new Vector2(0, 7.4f), box_2d_body.getWorldCenter(), true);
        }
    }

    public boolean isDead(){
        return red_droid_is_dead;
    }

    public float getState_timer(){
        return state_timer;
    }

    public void defineRedDroid(){
        BodyDef body_def = new BodyDef();
        body_def.position.set(32 / MutantAlienAssualtMobileZ.PPM, 32 / MutantAlienAssualtMobileZ.PPM);

        body_def.type = BodyDef.BodyType.DynamicBody;
        box_2d_body = world.createBody(body_def);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(13 / 2 / MutantAlienAssualtMobileZ.PPM,
                13 / 2 / MutantAlienAssualtMobileZ.PPM);//.setRadius(6 / MutantAlienAssualtMobileZ.PPM);

        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.RED_DROID_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.DEFUALT_BIT |
                                MutantAlienAssualtMobileZ.BRICK_BIT |
                                MutantAlienAssualtMobileZ.COIN_BIT |
                                MutantAlienAssualtMobileZ.ENEMY_BIT |
                                MutantAlienAssualtMobileZ.ENEMY_HEAD_BIT |
                                MutantAlienAssualtMobileZ.ITEM_BIT;

        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MutantAlienAssualtMobileZ.PPM, 7 / MutantAlienAssualtMobileZ.PPM),
                new Vector2(2 / MutantAlienAssualtMobileZ.PPM, 7 / MutantAlienAssualtMobileZ.PPM));

        fdef.shape = head;
        fdef.isSensor = true;
        box_2d_body.createFixture(fdef).setUserData("head");
    }

    public void hit(){
        if(!red_droid_is_dead) {
            red_droid_is_dead = true;
            sound = screen.getAssMan().manager.get(screen.getAssMan().SOUND_DAMAGE);
            sound.play();
        }

    }
}
