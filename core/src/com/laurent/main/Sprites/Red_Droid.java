package com.laurent.main.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Red_Droid extends Sprite {
    public enum State {FALLING, JUMPING, RUNNING, IDLE, DEAD};
    public State current_state;
    public State previous_state;

    public enum WeaponState {IDLE, FIRE, RELOAD, VIRTUAL_IDLE, VIRTUAL_FIRE, VIRTUAL_RELOAD};
    public WeaponState current_weapon_state;
    public WeaponState previous_weapon_state;

    public enum Weapon {
        UNARMED, PISTOL, ASSAULT_RIFLE;

        private static final Weapon[] VALUES = values();
        private static final int SIZE = VALUES.length;
        private static final Random RANDOM = new Random();


        public static Weapon getRandomWeapon() { return VALUES[RANDOM.nextInt(SIZE)]; }
    }
    private Weapon weapon;

    private Map<Weapon, Map<WeaponState, Animation<TextureRegion>>> LUT_weapon_animation;
    private Map<Weapon, Map<WeaponState, TextureRegion>> LUT_weapon_frame;
    public World world;
    public Body box_2d_body;
    private PlayScreen screen;
    private Sound sound;

    private TextureRegion render_red_droid;
    private TextureRegion render_weapon;

    private TextureRegion red_droid_dead;
    private Animation<TextureRegion> red_droid_idle;
    private Animation<TextureRegion> red_droid_running;
    private Animation<TextureRegion> red_droid_jumping;
    private Animation<TextureRegion> red_droid_falling;

    private TextureRegion pistol_idle;
    private Animation<TextureRegion> pistol_fire;

    private Sprite weapon_sprite = new Sprite();

    private boolean[] status_flags;
    boolean leftFalse_rightTrue;
    boolean red_droid_is_dead;
    boolean fire_weapon;

    private float state_timer;
    private float weapon_state_timer;
    private float weapon_position_x;
    private float weapon_position_y;
    int position_x;
    int position_y;
    private int ammo;
    private int rate_of_fire;

    public Red_Droid(PlayScreen screen, int x, int y){
        //super (screen.getAtlas().findRegion("red_droid_idle_20x21"));
        this.world = screen.getWorld();
        this.screen = screen;
        this.position_x = x;
        this.position_y = y;

        weapon = Weapon.PISTOL;
        current_state = State.IDLE;
        previous_state = State.IDLE;
        current_weapon_state = WeaponState.IDLE;
        previous_weapon_state = WeaponState.IDLE;
        state_timer = 0;
        weapon_state_timer = 0;
        ammo = 0;
        rate_of_fire = 1;

        leftFalse_rightTrue = true;
        red_droid_is_dead = false;
        fire_weapon = false;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        status_flags = new boolean[MutantAlienAssualtMobileZ.Status.size];
        status_flags[MutantAlienAssualtMobileZ.Status.MIDAIR.value] = true;

        //////////////////////
        // Red Droid Assets //
        //////////////////////

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
        setBounds(0, 0, 1, 1);
        render_red_droid = red_droid_idle.getKeyFrame(state_timer, true);
        setRegion(render_red_droid);


        ////////////////////
        // Weapons Assets //
        ////////////////////
        defineWeaponLUT();
        pistol_idle = new TextureRegion(screen.getAtlas().findRegion("pistol_32x16"), 0, 0, 32, 16);
        weapon_sprite.setBounds(0, 0, 2, 1);
        render_weapon = pistol_idle;
    }

    public void update(float dt){
        setPosition(box_2d_body.getPosition().x - getWidth() / 2,
                box_2d_body.getPosition().y - getHeight() / 2);

        //setRegion(getFrame(dt));
        setFrame(dt); //Originally getFrame returned a Texture region


        weaponUpdate(dt);

        outOfBounds();




        if (box_2d_body.getLinearVelocity().x > 1)
            box_2d_body.applyForce(new Vector2(-8f, 0), box_2d_body.getWorldCenter(), true);
        else if (box_2d_body.getLinearVelocity().x < -1)
            box_2d_body.applyForce(new Vector2(8f, 0), box_2d_body.getWorldCenter(), true);

    }

    public void setFrame(float dt){
        current_state = getState();

        //TextureRegion region;
        switch(current_state){
            case DEAD: render_red_droid = red_droid_dead; break;
            case JUMPING: render_red_droid=  red_droid_jumping.getKeyFrame(state_timer); break;
            case RUNNING: render_red_droid = red_droid_running.getKeyFrame(state_timer, true); break;
            case FALLING: render_red_droid = red_droid_falling.getKeyFrame(state_timer, true); break;
            case IDLE:
            default: render_red_droid = red_droid_idle.getKeyFrame(state_timer, true);; break;
        }

        if ((box_2d_body.getLinearVelocity().x < 0 || !leftFalse_rightTrue) && !render_red_droid.isFlipX()){
            render_red_droid.flip(true, false);
            leftFalse_rightTrue = false;
        }
        else if ((box_2d_body.getLinearVelocity().x > 0 || leftFalse_rightTrue) && render_red_droid.isFlipX()){
            render_red_droid.flip(true, false);
            leftFalse_rightTrue = true;
        }

        state_timer = current_state == previous_state ? state_timer + dt : 0;
        previous_state = current_state;
        //return render_red_droid;
    }

    public State getState(){

        if (red_droid_is_dead)
            return State.DEAD;
        else if((box_2d_body.getLinearVelocity().y > 0 && current_state == State.JUMPING)
                || (box_2d_body.getLinearVelocity().y > 0 && previous_state == State.JUMPING))
            return State.JUMPING;

        else if (box_2d_body.getLinearVelocity().y < 0)
            return State.FALLING;

        else if (box_2d_body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.IDLE;

    }

    public void weaponUpdate(float dt){
        positionWeapon();
        setWeaponFrame(dt);
    }

    public void positionWeapon(){
        /*  The weapon relative positioning depends on the weapon. This method selectively positions the weapon
            in my units based on the weapon class
        */
        switch(weapon){
            case UNARMED:   weapon_position_x = box_2d_body.getPosition().x;
                            weapon_position_y = box_2d_body.getPosition().y;
                            break;

            case PISTOL:    weapon_position_x = box_2d_body.getPosition().x;
                            weapon_position_y = box_2d_body.getPosition().y;
                            break;

            case ASSAULT_RIFLE:
                            weapon_position_x = box_2d_body.getPosition().x;
                            weapon_position_y = box_2d_body.getPosition().y;
                            break;
        }

        weapon_sprite.setPosition(weapon_position_x, weapon_position_y);
    }

    public void setWeaponFrame(float dt){
        current_weapon_state = getWeaponState();

        switch(current_weapon_state){
            //////////////////////////////
            //Single frames no animation//
            //////////////////////////////
            case IDLE: break;

            ///////////////////////////////////////////////
            //Looping animations look up 2 variable table//
            ///////////////////////////////////////////////
            case FIRE:
                /* render_weapon = LUT_weapon_animation
                        .get(weapon)
                        .get(current_weapon_state)
                        .getKeyFrame(state_timer, true);*/
                break;

            ///////////////////////////////////////////////////
            //Non looping animations look up 2 variable table//
            ///////////////////////////////////////////////////
            default:
                render_weapon = LUT_weapon_animation
                        .get(weapon)
                        .get(current_weapon_state)
                        .getKeyFrame(state_timer);
                break;
        }


        if ((box_2d_body.getLinearVelocity().x < 0 || !leftFalse_rightTrue) && !render_weapon.isFlipX()){
            render_weapon.flip(true, false);
            leftFalse_rightTrue = false;
        }
        else if ((box_2d_body.getLinearVelocity().x > 0 || leftFalse_rightTrue) && render_weapon.isFlipX()){
            render_weapon.flip(true, false);
            leftFalse_rightTrue = true;
        }

        weapon_state_timer = current_weapon_state == previous_weapon_state ? weapon_state_timer + dt : 0;
        previous_weapon_state = current_weapon_state;
    }

    public WeaponState getWeaponState(){

        if (red_droid_is_dead)
            return WeaponState.IDLE;
        else if (fire_weapon)
            return WeaponState.FIRE;
        else
            return WeaponState.IDLE;
    }

    public void jump(){
        /*if ( current_state != State.JUMPING && current_state != State.FALLING) {
            box_2d_body.applyLinearImpulse(new Vector2(0, 9f), box_2d_body.getWorldCenter(), true);
            current_state = State.JUMPING;
            sound = screen.getAssMan().manager.get(screen.getAssMan().SOUND_JUMP);
            sound.play();
        }
        else if (current_state == State.JUMPING && box_2d_body.getLinearVelocity().y > 0){
            if(box_2d_body.getLinearVelocity().y > 18) {
                box_2d_body.applyForce(new Vector2(0, 28.4f), box_2d_body.getWorldCenter(), true);
            }
            else if (box_2d_body.getLinearVelocity().y < 18){
                box_2d_body.applyForce(new Vector2(0,24.4f), box_2d_body.getWorldCenter(), true);
            }
        }*/
        if ( status_flags[MutantAlienAssualtMobileZ.Status.MIDAIR.value] != true) {
            box_2d_body.applyLinearImpulse(new Vector2(0, 5f), box_2d_body.getWorldCenter(), true);
            current_state = State.JUMPING;
            //status_flags[MutantAlienAssualtMobileZ.Status.MIDAIR.value] = true;
            sound = screen.getAssMan().manager.get(screen.getAssMan().SOUND_JUMP);
            sound.play();
        }
        else if (status_flags[MutantAlienAssualtMobileZ.Status.MIDAIR.value] && box_2d_body.getLinearVelocity().y > 2){
            if(box_2d_body.getLinearVelocity().y > 18) {
                box_2d_body.applyForce(new Vector2(0, 28.4f), box_2d_body.getWorldCenter(), true);
            }
            else if (box_2d_body.getLinearVelocity().y < 18){
                box_2d_body.applyForce(new Vector2(0,24.4f), box_2d_body.getWorldCenter(), true);
            }
        }
    }

    public void hit(){
        if(!red_droid_is_dead) {
            red_droid_is_dead = true;
            sound = screen.getAssMan().manager.get(screen.getAssMan().SOUND_DAMAGE);
            sound.play();
        }
    }

    public void dispenseWeaponFromDepot(){ weapon = Weapon.getRandomWeapon(); }

    public boolean isDead(){
        return red_droid_is_dead;
    }

    public float getState_timer(){
        return state_timer;
    }

    public boolean isFire_weapon(){ return fire_weapon; }

    public void defineRedDroid(){
        BodyDef body_def = new BodyDef();
        body_def.position.set(position_x, position_y);

        body_def.type = BodyDef.BodyType.DynamicBody;
        box_2d_body = world.createBody(body_def);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((0.9f/2), (0.9f/2) );



        // Player Body
        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.RED_DROID_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.DEFUALT_BIT |
                MutantAlienAssualtMobileZ.COIN_BIT |
                MutantAlienAssualtMobileZ.ENEMY_BIT |
                MutantAlienAssualtMobileZ.ITEM_BIT;

        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);


        // Player feet sensor
        EdgeShape feet = new EdgeShape();
        feet.set(new Vector2(-0.43f, -0.5f), new Vector2(0.43f, -0.5f));
        fdef.shape = feet;
        //fdef.isSensor = true;
        fdef.friction = 0.3f;
        fdef.density = 0f;

        AbstractMap.SimpleEntry<String, Red_Droid> pair = new AbstractMap.SimpleEntry("player_bottom", this);
        box_2d_body.createFixture(fdef).setUserData(pair);


        // Gun Placement (Likely to move)/

        CircleShape gun_point = new CircleShape();
        gun_point.setRadius(0.10f);
        fdef.shape = gun_point;
        fdef.isSensor = true;
        fdef.friction = 0.3f;
        fdef.density = 0f;
        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.NOTHING_BIT;
        box_2d_body.createFixture(fdef).setUserData(this);
    }

    protected void defineAnimationFrame(){

    }

    protected void defineWeaponLUT(){
        /* This method defines the look up table for rendering the weapons animations/frames.
           Called inn constructor onces. All animations and frames will be easily quiered with
           the lookup table.
        */

        //-----------//
        // Frame LUT //
        //-----------//
        LUT_weapon_frame = new HashMap<Weapon, Map<WeaponState, TextureRegion>>();

        //pistol//
        HashMap<WeaponState, TextureRegion>  pistol_frame = new HashMap<WeaponState, TextureRegion>();
        pistol_frame.put(WeaponState.IDLE, pistol_idle);



        //------------------------//
        // Animated animation LUT //
        //------------------------//
        LUT_weapon_animation = new HashMap<Weapon, Map<WeaponState, Animation<TextureRegion>>>();

        //pistol//
        HashMap<WeaponState, Animation<TextureRegion>>  pistol_animate = new HashMap<WeaponState, Animation<TextureRegion>>();
        pistol_animate.put(WeaponState.FIRE, pistol_fire);



        //------------------------//
        // Build Weapon Frame LUT //
        //------------------------//
        LUT_weapon_frame.put(Weapon.UNARMED, pistol_frame); // [!!!!TESTING!!!!]



        //----------------------------//
        // Build Weapon Animation LUT //
        //----------------------------//
        LUT_weapon_animation.put(Weapon.PISTOL, pistol_animate);
    }


    public void setStatusFlag(MutantAlienAssualtMobileZ.Status flag, boolean value){
        Gdx.app.log("red_droid", Float.toString(flag.value));
        switch(flag){
            case MIDAIR: status_flags[flag.value] = value;
                break;
        }
    }

    public void fireWeapon(boolean fire_weapon){
        this.fire_weapon = fire_weapon;
        ammo-= rate_of_fire;
    }

    public void outOfBounds(){
        // Destory all objects that leave the level boundaries
        if((box_2d_body.getPosition().x < 0) || (box_2d_body.getPosition().x > screen.getLevelWidth()))
            hit();
        if((box_2d_body.getPosition().y < 0) || (box_2d_body.getPosition().y > screen.getLevelHeight()))
            hit();
    }

    public void draw(Batch batch){

        switch(weapon) {
            case UNARMED:
                setRegion(render_red_droid);
                super.draw(batch);
                break;

            case PISTOL:
                //setRegion(render_gun);
                //super.draw(batch);

               // setRegion(render_red_droid);
                //super.draw(batch);
               // break;

            case ASSAULT_RIFLE:
                setRegion(render_red_droid);
                super.draw(batch);

                weapon_sprite.setRegion(render_weapon);
                weapon_sprite.draw(batch);
                break;
        }

        //super.draw(batch);
    }
}
