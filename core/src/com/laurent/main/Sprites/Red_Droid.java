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
import com.laurent.main.Sprites.Items.Bullet;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Red_Droid extends Sprite {
    public enum AnimationState {FALLING, JUMPING, RUNNING, IDLE, DEAD};
    public AnimationState current_state;
    public AnimationState previous_state;

    private enum WeaponState {IDLE, FIRE, RELOAD, VIRTUAL_IDLE, VIRTUAL_FIRE, VIRTUAL_RELOAD};
    private WeaponState current_weapon_state;
    private WeaponState previous_weapon_state;

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
    private Random random;
    private Sprite weapon_sprite;

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


    private boolean[] status_flags;
    boolean leftFalse_rightTrue;
    boolean red_droid_is_dead;
    boolean fire_weapon;

    private float animation_state_timer;
    private float weapon_state_timer;
    private float weapon_position_x;
    private float weapon_position_y;
    private float android_position_w;
    private float android_position_h;
    private float rate_of_fire;
    private float rate_of_fire_limit;


    private int android_position_x;
    private int android_position_y;
    private int ammo;


    public Red_Droid(PlayScreen screen, int x, int y){
        this.world = screen.getWorld();
        this.screen = screen;
        this.android_position_x = x;
        this.android_position_y = y;

        //Enumns
        weapon = Weapon.PISTOL;
        current_state = AnimationState.IDLE;
        previous_state = AnimationState.IDLE;this.android_position_w = 0.9f;
        current_weapon_state = WeaponState.IDLE;
        previous_weapon_state = WeaponState.IDLE;

        //Floats
        animation_state_timer = 0;
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
        animation_table_player = new HashMap<>();
        animation_table_weapon = new HashMap<>();
        bullet_start_positions = new Array<Vector2>();
        bullet_velocities = new Array<Vector2>();
        random = new Random();
        weapon_sprite = new Sprite();

        initBox2D();
        initRedDroidTextureRegions();
        initWeaponTextureRegions();
        initWeaponV2LUT();
        initBulletPositionVelocity();
        configBulletSpawnPosition();
        configBulletVelocity();
    }

    public void update(float dt){
        setPosition(box_2d_body.getPosition().x - getWidth() / 2, box_2d_body.getPosition().y - getHeight() / 2);
        configRedDroidFrame(dt);
        weaponUpdate(dt);
        outOfBounds();

        if (box_2d_body.getLinearVelocity().x > 1)
            box_2d_body.applyForce(new Vector2(-8f, 0), box_2d_body.getWorldCenter(), true);
        else if (box_2d_body.getLinearVelocity().x < -1)
            box_2d_body.applyForce(new Vector2(8f, 0), box_2d_body.getWorldCenter(), true);
    }
    public void weaponUpdate(float dt){
        configWeaponPosition();
        configWeaponFrame(dt);
    }

    //-----------------//
    // Actions Methods // The various actiions of the red droid
    //-----------------//
    public void jump(){
        /* This method allows the player to jump only if their feet are not touching to ground.
         * the status variable is defined else where
         * */

        if ( status_flags[MutantAlienAssualtMobileZ.Status.MIDAIR.value] != true) {
            box_2d_body.applyLinearImpulse(new Vector2(0, 5f), box_2d_body.getWorldCenter(), true);
            current_state = AnimationState.JUMPING;
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
    }
    public void fireWeapon(boolean fire_weapon, float dt){
        /* This method set the fire_weapon boolean. It also increments the ammo lost after firing a shot.
         * This varies weapons to weapon.
         * */

        this.fire_weapon = fire_weapon;

        if((weapon != Weapon.UNARMED) && (this.fire_weapon)) {
            Bullet b = screen.getBulletPool().obtain();
            Array<Bullet> active_bullets = screen.getActiveBullets();

            configBulletSpawnPosition();
            configBulletVelocity();
            b.fireBullet(bullet_start_positions.get(random.nextInt(5)),
                         bullet_velocities.get(random.nextInt(5)),
                         weapon);

            active_bullets.add(b);

            ammo -= rate_of_fire;
        }
    }
    public void dispenseWeaponFromDepot(){
        /* This method sets the weapons variable from unarmed/pistol to a more powerful weapon.
         * It also resets the weapons sprites bounds, and placement to fire bullets from.
         * If the player already has a powerful weapon equiped. They are given more ammo instead.
         * */

        weapon = Weapon.getRandomWeapon();
        configBulletSpawnPosition();
        configBulletVelocity();
        configWeaponBounds();
        configRateOfFire();
    }
    public void onHitDamage(){
        /* This variable should first remove the player's weapon when hit by an enemies. Secondly it should kill
         * the player if they get hit without a weapon.
         * */

        if(!red_droid_is_dead) {
            red_droid_is_dead = true;
            sound = screen.getAssMan().manager.get(screen.getAssMan().SOUND_DAMAGE);
            sound.play();
        }
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
    private void configRateOfFire(){
        /* This method configure the rate_of_fire & rate_of_fire limit. The rate_of_fire is the
         * the fire rate of the weapon when the fire button is held down measured in millisecond.
         * The rate_of_fire_limit define the maximum bullet rate weapon even if the player
         * mashes the button
         */

        switch(weapon) {
            case UNARMED:       rate_of_fire = 0;       rate_of_fire_limit = 0; break;
            case PISTOL:        rate_of_fire = 0.1f;    rate_of_fire_limit = 0.05f; break;
            case ASSAULT_RIFLE: rate_of_fire = 0.02f;   rate_of_fire_limit = 0.02f; break;
            case SHOT_GUN:      rate_of_fire = 0.1f;    rate_of_fire_limit = 0.1f; break;
        }
    }
    private void configRedDroidFrame(float dt){
        /* This method configure the current/previous animation state of the red droid.
         * It also determines if a sprites is pointing left or right.
         * */

        //------------------------------------------------//
        // Determine the current state of the red android //
        //------------------------------------------------//
        current_state = configAnimationState();

        //----------------------------------------------------------------------------------//
        // Use the newly calculated current state in a Look table to get the correct frames //
        //----------------------------------------------------------------------------------//
        switch(current_state){
            case DEAD: render_red_droid = animation_table_player.get("red_droid_dead").getKeyFrame(animation_state_timer); break;
            case JUMPING: render_red_droid = animation_table_player.get("red_droid_jumping").getKeyFrame(animation_state_timer); break;
            case RUNNING: render_red_droid = animation_table_player.get("red_droid_running").getKeyFrame(animation_state_timer, true); break;
            case FALLING: render_red_droid = animation_table_player.get("red_droid_falling").getKeyFrame(animation_state_timer, true); break;
            case IDLE:
            default: render_red_droid = animation_table_player.get("red_droid_idle").getKeyFrame(animation_state_timer, true); break;
        }

        //--------------------------------------------//
        // Check which direction the player is facing //
        //--------------------------------------------//
        if ((box_2d_body.getLinearVelocity().x < 0 || !leftFalse_rightTrue) && !render_red_droid.isFlipX()){
            render_red_droid.flip(true, false);
            leftFalse_rightTrue = false;
        }
        else if ((box_2d_body.getLinearVelocity().x > 0 || leftFalse_rightTrue) && render_red_droid.isFlipX()){
            render_red_droid.flip(true, false);
            leftFalse_rightTrue = true;
        }

        animation_state_timer = current_state == previous_state ? animation_state_timer + dt : 0;
        previous_state = current_state;
    }
    private void configWeaponFrame(float dt){
        /* This method configure the current/previous animation state of the weapon.
         * It also determines if a sprites is pointing left or right.
         * */

        //-------------------------------------------//
        // Determine the current state of the weapon //
        //-------------------------------------------//
        current_weapon_state = configWeaponState();

        //----------------------------------------------------------------------------------//
        // Use the newly calculated current state in a Look table to get the correct frames //
        //----------------------------------------------------------------------------------//
        switch(current_weapon_state){
            //-----------------------------//
            // Animations that should loop //
            //-----------------------------//
            case FIRE:
                weapon_animation = V2LUT_weapon_animation
                        .get(weapon)
                        .get(current_weapon_state);
                render_weapon = weapon_animation.getKeyFrame(weapon_state_timer, true);
                break;

            //--------------------------------//
            // Animations that shouldn't loop //
            //--------------------------------//
            default:
                weapon_animation = V2LUT_weapon_animation
                        .get(weapon)
                        .get(current_weapon_state);
                render_weapon = weapon_animation.getKeyFrame(weapon_state_timer);
                break;
        }

        //--------------------------------------------//
        // Check which direction the player is facing //
        //--------------------------------------------//
        if (!leftFalse_rightTrue && !render_weapon.isFlipX())
            render_weapon.flip(true, false);
        else if (leftFalse_rightTrue && render_weapon.isFlipX())
            render_weapon.flip(true, false);

        weapon_state_timer = current_weapon_state == previous_weapon_state ? weapon_state_timer + dt : 0;
        previous_weapon_state = current_weapon_state;
    }
    private void configBulletSpawnPosition(){
        /* This method configure the positioning a bullet for each weapon. his is to
         * ensure the bullet is spawned at the correct location.
         * This method is called in the fireWeapon() method
         */

        switch(weapon) {
            case PISTOL:
                bullet_start_positions.get(0).y = weapon_position_y + (android_position_h * 45 / 100);
                bullet_start_positions.get(1).y = weapon_position_y + (android_position_h * 65 / 100);
                bullet_start_positions.get(2).y = weapon_position_y + (android_position_h * 75 / 100);
                bullet_start_positions.get(3).y = weapon_position_y + (android_position_h * 80 / 100);
                bullet_start_positions.get(4).y = weapon_position_y + (android_position_h * 50 / 100);

                for(int i=0; i<bullet_start_positions.size; i++) {
                    bullet_start_positions.get(i).x = leftFalse_rightTrue
                            ? weapon_position_x + (android_position_w * 120 / 100) //right
                            : weapon_position_x + (android_position_w * 100 / 100); //left
                }
                break;

            case ASSAULT_RIFLE:
            case SHOT_GUN:
                for(Vector2 bullet_px : bullet_start_positions) {
                    bullet_px.x = weapon_position_x;
                    bullet_px.y = weapon_position_y;
                }
                break;
        }
    }
    private void configBulletVelocity(){
        /* This method configure the velocity of a bullet for each weapon.
         * his is to ensure the bullet moves in the correct direction.
         * This method is called when the player fire their weapon
         */

        switch(weapon) {
            case PISTOL:
                bullet_velocities.get(0).y = -0.1f;
                bullet_velocities.get(1).y = -0.2f;
                bullet_velocities.get(2).y = 0.0f;
                bullet_velocities.get(3).y = 0.2f;
                bullet_velocities.get(4).y = 0.1f;

                for(int i=0; i<bullet_start_positions.size; i++) {
                    bullet_velocities.get(i).x = leftFalse_rightTrue
                            ? 14.0f
                            : -14.0f;
                }
                break;

            case ASSAULT_RIFLE:
            case SHOT_GUN:
                for(Vector2 bullet_px : bullet_start_positions) {
                    bullet_px.x = weapon_position_x;
                    bullet_px.y = weapon_position_y;
                }
                break;
        }
    }
    private WeaponState configWeaponState(){
        /* This method configure the animation state of the current weapon equiped using a
         * variety of variables
         * */

        if((!fire_weapon) && (weapon_animation.isAnimationFinished(weapon_state_timer)))
            return WeaponState.IDLE;

        else  if((!fire_weapon) && (!weapon_animation.isAnimationFinished(weapon_state_timer)))
            return current_weapon_state;

        else if ((fire_weapon) && (current_weapon_state == previous_weapon_state))
            return WeaponState.FIRE;


        return previous_weapon_state;
    }
    private AnimationState configAnimationState(){
        /* This animation configure the animation state using a variety of variables
         * */

        if (red_droid_is_dead)
            return AnimationState.DEAD;

        else if((box_2d_body.getLinearVelocity().y > 0 && current_state == AnimationState.JUMPING)
                || (box_2d_body.getLinearVelocity().y > 0 && previous_state == AnimationState.JUMPING))
            return AnimationState.JUMPING;

        else if (box_2d_body.getLinearVelocity().y < 0)
            return AnimationState.FALLING;

        else if (box_2d_body.getLinearVelocity().x != 0)
            return AnimationState.RUNNING;

        else
            return AnimationState.IDLE;

    }

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
        render_red_droid = animation_table_player.get("red_droid_idle").getKeyFrame(animation_state_timer, true);
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
    private void initBulletPositionVelocity(){
        /* Initialize 5 bullet position and velocities. these 2 variables can be selected randomly
         * to scatter bullets. This leads to potentially 5x5 combination for how a bullet is fired.
         */

        bullet_start_positions.add( new Vector2(0,0));
        bullet_start_positions.add( new Vector2(0,0));
        bullet_start_positions.add( new Vector2(0,0));
        bullet_start_positions.add( new Vector2(0,0));
        bullet_start_positions.add( new Vector2(0,0));

        bullet_velocities.add( new Vector2(0,0));
        bullet_velocities.add( new Vector2(0,0));
        bullet_velocities.add( new Vector2(0,0));
        bullet_velocities.add( new Vector2(0,0));
        bullet_velocities.add( new Vector2(0,0));
    }

    //----------------//
    // Getter Methods // .................................................................... They get stuff.
    //----------------//
    public boolean isDead(){
        return red_droid_is_dead;
    }
    public boolean isFire_weapon(){ return fire_weapon; }
    public float getAnimationStateTimer(){
        return animation_state_timer;
    }

    //----------------//
    // Setter Methods // ...................................................................
    //----------------//
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
            onHitDamage();
        if((box_2d_body.getPosition().y < 0) || (box_2d_body.getPosition().y > screen.getLevelHeight()))
            onHitDamage();
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