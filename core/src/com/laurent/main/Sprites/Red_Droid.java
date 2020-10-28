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
        UNARMED, PISTOL, ASSAULT_RIFLE, SHOT_GUN;

        private static final Weapon[] VALUES = values();
        private static final int SIZE = VALUES.length;
        private static final Random RANDOM = new Random();


        public static Weapon getRandomWeapon() { return VALUES[RANDOM.nextInt(SIZE)]; }
    }
    private Weapon weapon;

    public World world;
    public Body box_2d_body;
    private PlayScreen screen;
    private Sound sound;

    private Array<Vector2> bullet_start_positions;
    private Array<Vector2> bullet_velocities;

    private TextureRegion render_red_droid;
    private TextureRegion render_weapon;
    private Animation<TextureRegion> weapon_animation;

    // This map encapsulate the animations for less code
    private Map<String, Animation<TextureRegion>> animation_table_player;
    private Map<String, Animation<TextureRegion>> animation_table_weapon;

    //This is a 2 variable look up table for fast searches of animation
    private Map<Weapon, Map<WeaponState, Animation<TextureRegion>>> V2LUT_weapon_animation;

    private Sprite weapon_sprite = new Sprite();

    private boolean[] status_flags;
    boolean leftFalse_rightTrue;
    boolean red_droid_is_dead;
    boolean fire_weapon;

    private float state_timer;
    private float weapon_state_timer;
    private float weapon_position_x;
    private float weapon_position_y;
    private float android_position_w;
    private float android_position_h;

    private int android_position_x;
    private int android_position_y;
    private int ammo;
    private int rate_of_fire;

    public Red_Droid(PlayScreen screen, int x, int y){
        this.world = screen.getWorld();
        this.screen = screen;
        this.android_position_x = x;
        this.android_position_y = y;

        //Enumns
        weapon = Weapon.ASSAULT_RIFLE;
        current_state = State.IDLE;
        previous_state = State.IDLE;this.android_position_w = 0.9f;
        current_weapon_state = WeaponState.IDLE;
        previous_weapon_state = WeaponState.IDLE;

        //Floats
        state_timer = 0;
        weapon_state_timer = 0;
        android_position_w = 0.9f;
        android_position_h = 0.9f;

        //Ints
        ammo = 0;
        rate_of_fire = 1;

        //Boolean
        leftFalse_rightTrue = true;
        red_droid_is_dead = false;
        fire_weapon = false;
        status_flags = new boolean[MutantAlienAssualtMobileZ.Status.size];
        status_flags[MutantAlienAssualtMobileZ.Status.MIDAIR.value] = true;

        // Misc
        animation_table_player = new HashMap<String, Animation<TextureRegion>>();
        animation_table_weapon = new HashMap<String, Animation<TextureRegion>>();

        initBox2D();
        initRedDroidTextureRegions();
        initWeaponTextureRegions();
        initWeaponV2LUT();
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
            case DEAD: render_red_droid = animation_table_player.get("red_droid_dead").getKeyFrame(state_timer); break;
            case JUMPING: render_red_droid = animation_table_player.get("red_droid_jumping").getKeyFrame(state_timer); break;
            case RUNNING: render_red_droid = animation_table_player.get("red_droid_running").getKeyFrame(state_timer, true); break;
            case FALLING: render_red_droid = animation_table_player.get("red_droid_falling").getKeyFrame(state_timer, true); break;
            case IDLE:
            default: render_red_droid = animation_table_player.get("red_droid_idle").getKeyFrame(state_timer, true); break;
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
        configWeaponPosition();
        setWeaponFrame(dt);
    }

    public void setWeaponFrame(float dt){
        current_weapon_state = getWeaponState();

        switch(current_weapon_state){
            ///////////////////////////////////////////////
            //looping animations look up 2 variable table//
            ///////////////////////////////////////////////
            case FIRE:
                weapon_animation = V2LUT_weapon_animation
                        .get(weapon)
                        .get(current_weapon_state);
                render_weapon = weapon_animation.getKeyFrame(weapon_state_timer, true);
                break;
            ///////////////////////////////////////////////////
            //Non looping animations look up 2 variable table//
            ///////////////////////////////////////////////////
            default:
                weapon_animation = V2LUT_weapon_animation
                        .get(weapon)
                        .get(current_weapon_state);
                render_weapon = weapon_animation.getKeyFrame(weapon_state_timer);
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

        if((!fire_weapon) && (weapon_animation.isAnimationFinished(weapon_state_timer)))
            return WeaponState.IDLE;
        else  if((!fire_weapon) && (!weapon_animation.isAnimationFinished(weapon_state_timer))) {
            return current_weapon_state;
        }
        else if ((fire_weapon) && (current_weapon_state == previous_weapon_state))
            return WeaponState.FIRE;


        return previous_weapon_state;
    }


    //-----------------//
    // Actions Methods // Methods that perform actions/verbs in games
    //-----------------//
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

    public void fireWeapon(boolean fire_weapon){
        this.fire_weapon = fire_weapon;
        ammo-= rate_of_fire;
    }

    public void dispenseWeaponFromDepot(){
        weapon = Weapon.getRandomWeapon();
        configWeaponBounds();
    }

    //--------------------------------//
    // Run Time Configuration Methods // Mostly used to configure the weapon when it changes during run time
    //--------------------------------//
    private void configWeaponBounds(){
        /* This method set the correct configurations for displaying a weapon when it changes
         * during run time. This method is called whenever the weapon is changed which so far
         * only happens in dispenseWeaponFromDepot(). It is also called during initialization
         */

        switch(weapon) {
            case UNARMED: weapon_sprite.setBounds(0, 0, 0.1f, 0.1f); break;
            case PISTOL:  weapon_sprite.setBounds(0, 0, 2f, 1f); break;
            case ASSAULT_RIFLE: case SHOT_GUN:
                          weapon_sprite.setBounds(0, 0, 4f, 1f); break;
        }
    }
    private void configWeaponPosition(){
        /* This method position the weapon relative to the player position. It has this configuration for
         * each weapon because the weapons have different sizes.
         */

        switch(weapon){
            case UNARMED:
                    weapon_position_x = box_2d_body.getPosition().x;
                    weapon_position_y = box_2d_body.getPosition().y;
                break;

            case PISTOL:
                    weapon_position_x  = leftFalse_rightTrue
                            ? box_2d_body.getPosition().x + (android_position_w * 25 / 100)
                            : box_2d_body.getPosition().x - (android_position_w * 250 / 100);
                    weapon_position_y = box_2d_body.getPosition().y - (android_position_h * 50 / 100);
                break;

            case ASSAULT_RIFLE:
                    weapon_position_x = leftFalse_rightTrue
                            ? box_2d_body.getPosition().x - (android_position_w * 60 / 100)
                            : box_2d_body.getPosition().x - (android_position_w * 375 / 100);
                    weapon_position_y = box_2d_body.getPosition().y - (android_position_h * 70 / 100);
                break;

            case SHOT_GUN:
                    weapon_position_x = leftFalse_rightTrue
                            ? box_2d_body.getPosition().x - (android_position_w * 70 / 100)
                            : box_2d_body.getPosition().x - (android_position_w * 385 / 100);
                    weapon_position_y = box_2d_body.getPosition().y - (android_position_h * 70 / 100);
                break;
        }

        weapon_sprite.setPosition(weapon_position_x, weapon_position_y);
    }

    public boolean isDead(){
        return red_droid_is_dead;
    }

    public float getState_timer(){
        return state_timer;
    }

    public boolean isFire_weapon(){ return fire_weapon; }

    //------------------------//
    // Initialization Methods // These methods are only called in the constructor
    //------------------------//
    private void initBox2D(){
        /* This method create a bod2d object and inserts it into the world. It also creates additional
         * components of the player such as the feet used to detect collision with the ground.
         * */

        //-------------//
        // Declaration //
        //-------------//
        BodyDef body_def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        EdgeShape feet = new EdgeShape();

        //------------------------------//
        // Create new body in the world //
        //------------------------------//
        body_def.position.set(android_position_x, android_position_y);
        body_def.type = BodyDef.BodyType.DynamicBody;
        box_2d_body = world.createBody(body_def);

        //----------------------------------------------//
        // Define player fixture definition & collision //
        //----------------------------------------------//
        shape.setAsBox((android_position_w/2), (android_position_h/2));
        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.RED_DROID_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.DEFUALT_BIT |
                MutantAlienAssualtMobileZ.COIN_BIT |
                MutantAlienAssualtMobileZ.ENEMY_BIT |
                MutantAlienAssualtMobileZ.ITEM_BIT;
        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);

        //--------------------//
        // Define player feet //
        //--------------------//
        feet.set(new Vector2(-0.43f, -0.5f), new Vector2(0.43f, -0.5f));
        fdef.shape = feet;
        fdef.friction = 0.3f;
        fdef.density = 0f;
        //fdef.isSensor = true;
        AbstractMap.SimpleEntry<String, Red_Droid> pair = new AbstractMap.SimpleEntry("player_bottom", this);
        box_2d_body.createFixture(fdef).setUserData(pair);
    }
    private void initRedDroidTextureRegions() {
        /* This method initialization the texture regions used to render the player's droid. The texture regions are stored
         * in a hash map and are accessible by string name of the animations. It also initialization variables of the class
         * sprites with default values upon starting the game */

        //-------------//
        // Declaration //
        //-------------//
        Array<TextureRegion> frames = new Array<TextureRegion>();

        //-------------------------------//
        // Adding Texture Regions to Map //
        //-------------------------------//
        // ---- Idle Animation----/
        for (int e = 0; e < 1; e++)
            for (int i = 0; i < 5; i++)
                frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_idle_20x21"), i * 20, 0, 20, 21));
        for (int i = 4; i >= 0; i--) //Get frames in reverse
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_idle_20x21"), i * 20, 0, 20, 21));
        animation_table_player.put("red_droid_idle",  new Animation(0.25f, frames));
        frames.clear();

        // ----Running Animation---- //
        for (int i = 0; i < 6; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_run_21x21"), i * 21, 0, 21, 21));
        animation_table_player.put("red_droid_running", new Animation(0.1f, frames));
        frames.clear();

        // ----Jumping Animation---- //
        for (int i = 1; i < 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_jump_20x21"), i * 20, 0, 20, 21));
        animation_table_player.put("red_droid_jumping", new Animation(0.1f, frames));
        frames.clear();

        // ----Falling Animation---- //
        for (int i = 1; i < 2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_fall_20x21"), i * 20, 0, 20, 21));
        animation_table_player.put("red_droid_falling", new Animation(0.1f, frames));
        frames.clear();

        // ----Dead---- //
        frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_dead_20x21"), 0, 0, 20, 21));
        animation_table_player.put("red_droid_dead", new Animation(0.1f, frames));
        frames.clear();

        //----------------//
        // Initialization //
        //----------------//
        // The size to render the sprites. 1 meter high, 1 meter wide in ~my units
        setBounds(0, 0, 1, 1);
        render_red_droid = animation_table_player.get("red_droid_idle").getKeyFrame(state_timer, true);
        setRegion(render_red_droid);

        /*/ Idle Animation
        for (int e = 0; e < 1; e++)
            for (int i = 0; i < 5; i++)
                frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_idle_20x21"), i * 20, 0, 20, 21));
        //Get frames in reverse
        for (int i = 4; i >= 0; i--)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_idle_20x21"), i * 20, 0, 20, 21));
        red_droid_idle = new Animation(0.25f, frames);
        frames.clear();

        // Running Animation
        for (int i = 0; i < 6; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("red_droid_run_21x21"), i * 21, 0, 21, 21));
        red_droid_running = new Animation(0.1f, frames);
        frames.clear();
        red_droid_dead = new TextureRegion(screen.getAtlas().findRegion("red_droid_dead_20x21"), 0, 0, 20, 21);*/
    }
    private void initWeaponTextureRegions(){
        /* This method initialization the texture regions used to render the player's weapon. The texture regions are stored
         * in a hash map and are accessible by string name of the animations. It also initialization variables of the class
         * sprites with default values upon starting the game */

        //-------------//
        // Declaration //
        //-------------//
        Array<TextureRegion> frames = new Array<TextureRegion>();

        //-------------------------------//
        // Adding Texture Regions to Map //
        //-------------------------------//
        //---unarmed---//
        frames.add(new TextureRegion(screen.getAtlas().findRegion("pistol_32x16"), 0, 0, 1, 1));
        animation_table_weapon.put("unarmed", new Animation(0.0000001f, frames));
        frames.clear();

        //---pistol idle---//
        frames.add(new TextureRegion(screen.getAtlas().findRegion("pistol_32x16"), 0, 0, 32, 16));
        animation_table_weapon.put("pistol_idle", new Animation(0.0000001f, frames));
        frames.clear();

        //---pistol fire---//
        for (int i=0; i<4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("pistol_32x16"), i*32, 0, 32, 16));
        animation_table_weapon.put("pistol_fire", new Animation(0.1f, frames));
        frames.clear();

        //---assault rifle idle---//
        frames.add(new TextureRegion(screen.getAtlas().findRegion("assualt_rifle_64x16"), 0, 0, 64, 16));
        animation_table_weapon.put("assualt_rifle_idle", new Animation(0.0000001f, frames));
        frames.clear();

        //---assault rifle fire---//
        for (int i=0; i<5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("assualt_rifle_64x16"), i*64, 0, 64, 16));
        animation_table_weapon.put("assualt_rifle_fire", new Animation(0.02f, frames));
        frames.clear();

        //---shot gun idle---//
        frames.add(new TextureRegion(screen.getAtlas().findRegion("shot_gun_64x16"), 0, 0, 64, 16));
        animation_table_weapon.put("shot_gun_idle", new Animation(0.0000001f, frames));
        frames.clear();

        //---shot gun fire---//
        for (int i=0; i<5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("shot_gun_64x16"), i*64, 0, 64, 16));
        animation_table_weapon.put("shot_gun_fire", new Animation(0.08f, frames));
        frames.clear();

        //----------------//
        // Initialization //
        //----------------//
        configWeaponBounds();
        render_weapon = animation_table_weapon.get("unarmed").getKeyFrame(0, true);
        weapon_animation = animation_table_weapon.get("unarmed");
        weapon_sprite.setRegion(render_weapon);
    }
    private void initWeaponV2LUT(){
        /* This method initializes a two variable look up table for texture regions related to the weapon. This
         * is to make it easy to query the correct animation with the weapon currently equiped and the state/frame
         * it is displaying. This an automatic process.
        */

        //-------------//
        // Declaration //
        //-------------//
        V2LUT_weapon_animation = new HashMap<Weapon, Map<WeaponState, Animation<TextureRegion>>>();
        HashMap<WeaponState, Animation<TextureRegion>>  unarmed_table = new HashMap<WeaponState, Animation<TextureRegion>>();
        HashMap<WeaponState, Animation<TextureRegion>>  pistol_table = new HashMap<WeaponState, Animation<TextureRegion>>();
        HashMap<WeaponState, Animation<TextureRegion>>  assualt_rifle_table = new HashMap<WeaponState, Animation<TextureRegion>>();
        HashMap<WeaponState, Animation<TextureRegion>>  shot_gun_table = new HashMap<WeaponState, Animation<TextureRegion>>();

        //-----------------------------------//
        // Adding texture regions to the LUT //
        //-----------------------------------//
        //---unarmed---//
        unarmed_table.put(WeaponState.IDLE, animation_table_weapon.get("unarmed"));
        unarmed_table.put(WeaponState.FIRE, animation_table_weapon.get("unarmed"));
        unarmed_table.put(WeaponState.RELOAD, animation_table_weapon.get("unarmed"));
        unarmed_table.put(WeaponState.VIRTUAL_FIRE, animation_table_weapon.get("unarmed"));
        unarmed_table.put(WeaponState.VIRTUAL_IDLE, animation_table_weapon.get("unarmed"));
        unarmed_table.put(WeaponState.VIRTUAL_RELOAD, animation_table_weapon.get("unarmed"));

        //---pistol---//
        pistol_table.put(WeaponState.FIRE, animation_table_weapon.get("pistol_fire"));
        pistol_table.put(WeaponState.IDLE, animation_table_weapon.get("pistol_idle"));

        //---assualt rifle---//
        assualt_rifle_table.put(WeaponState.FIRE, animation_table_weapon.get("assualt_rifle_fire"));
        assualt_rifle_table.put(WeaponState.IDLE, animation_table_weapon.get("assualt_rifle_idle"));

        //---shot gun---//
        shot_gun_table.put(WeaponState.FIRE, animation_table_weapon.get("shot_gun_fire"));
        shot_gun_table.put(WeaponState.IDLE, animation_table_weapon.get("shot_gun_idle"));

        //----------------------------//
        // Build Weapon Animation LUT //
        //----------------------------//
        V2LUT_weapon_animation.put(Weapon.SHOT_GUN, shot_gun_table);
        V2LUT_weapon_animation.put(Weapon.PISTOL, pistol_table);
        V2LUT_weapon_animation.put(Weapon.ASSAULT_RIFLE, assualt_rifle_table);
        V2LUT_weapon_animation.put(Weapon.UNARMED, unarmed_table);
    }

    public void setStatusFlag(MutantAlienAssualtMobileZ.Status flag, boolean value){
        Gdx.app.log("red_droid", Float.toString(flag.value));
        switch(flag){
            case MIDAIR: status_flags[flag.value] = value;
                break;
        }
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
                weapon_sprite.setRegion(render_weapon);
                weapon_sprite.draw(batch);

                setRegion(render_red_droid);
                super.draw(batch);
                break;

            case ASSAULT_RIFLE: case SHOT_GUN:
                setRegion(render_red_droid);
                super.draw(batch);

                weapon_sprite.setRegion(render_weapon);
                weapon_sprite.draw(batch);
                break;
        }

        //super.draw(batch);
    }
}