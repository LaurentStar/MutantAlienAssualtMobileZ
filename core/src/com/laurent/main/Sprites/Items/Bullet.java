package com.laurent.main.Sprites.Items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.Sprites.Red_Droid;

import java.util.HashMap;
import java.util.Map;

public class Bullet extends Sprite implements Poolable {

    // fields for bullets position and direction
    public Vector2 position;
    public Vector2 width_length;
    public Vector2 direction;
    public Body box_2d_body;
    public World world;
    private PlayScreen screen;
    private Red_Droid.Weapon weapon_type;
    private Map<String, Animation<TextureRegion>> animation_table_bullet;
    private TextureRegion render_bullet;
    private int damage;
    private float animation_state_timer;
    private float alive_timer;
    public boolean alive;

    Bullet(PlayScreen screen){

        position = new Vector2(0,0);
        direction = new Vector2(0,0);
        width_length = new Vector2(0,0);
        weapon_type = Red_Droid.Weapon.UNARMED;
        this.damage = 0;
        this.alive_timer = 0;
        this.animation_state_timer = 0;
        this.world = screen.getWorld();
        this.screen = screen;
        this.alive = false;
        animation_table_bullet = new HashMap<String, Animation<TextureRegion>>();
        initBox2D();
        initBulletTextureRegions();
    }


    @Override
    public void reset() {
        //called when bullet is freed
        this.position.set(0,0);
        this.direction.set(0,0);
        this.alive = false;
        this.alive_timer = 0;
        this.box_2d_body.setLinearVelocity(direction);
//        System.out.println("Bullet is reset");
    }

    public void update(float dt){

        alive_timer += dt;

        if (alive_timer > 2) {
            alive = false;
        }

        setPosition(box_2d_body.getPosition().x - getWidth() / 2, box_2d_body.getPosition().y - getHeight() / 2);


//            System.out.println("Bullet direction is applied");
//            System.out.println(this.direction);
        configBulletFrame();


    }


    //-----------------//
    // Actions Methods // Methods that perform actions/verbs in games
    //-----------------//
    public void fireBullet(Vector2 pos, Vector2 dir, Red_Droid.Weapon weapon_type){
        this.alive = true;
        this.position = pos;
        this.direction = dir;
        this.weapon_type = weapon_type;

        box_2d_body.setTransform(position, 0);
        box_2d_body.setLinearVelocity(direction);
        configBulletWidthLength();
        configBulletDamage();
    }
//    private void ReshapeBullet(){
//        if(previous_type != type) {
//            FixtureDef fdef = new FixtureDef();
//            PolygonShape shape = new PolygonShape();
//
//            shape.setAsBox((width_length.x / 2), (width_length.y / 2));
//            fdef.filter.categoryBits = MutantAlienAssualtMobileZ.BULLET_BIT;
//            fdef.shape = shape;
//            box_2d_body.createFixture(fdef).setUserData(this);
//            previous_type = type;
//        }
//    }

    //--------------------------------//
    // Run Time Configuration Methods // ...................................................
    //--------------------------------//
    private void configBulletDamage(){
        switch(weapon_type){
            case UNARMED: damage = 0; break;
            case PISTOL: damage = 10; break;
            case ASSAULT_RIFLE: damage = 3; break;
            case SHOT_GUN: damage = 10; break;
        }
    }
    private void configBulletWidthLength(){
        switch(weapon_type){
            case UNARMED:
                width_length.x = 0;
                width_length.y = 0;
                break;
            case PISTOL:
            case ASSAULT_RIFLE:
                width_length.x = 0.5f;
                width_length.y = 0.25f;
                break;
            case SHOT_GUN:
                width_length.x = 0.1875f;
                width_length.y = 0.1875f;
                break;
        }
    }
    private void configBulletFrame(){
        switch(weapon_type){
            case UNARMED: case ASSAULT_RIFLE: case PISTOL:
                render_bullet = animation_table_bullet.get("blue_bullet").getKeyFrame(0);
                break;
            case SHOT_GUN:
                render_bullet = animation_table_bullet.get("blue_shell").getKeyFrame(0);
                break;

        }
    }

    //------------------------//
    // Initialization Methods // These methods are only called in the constructor
    //------------------------//
    private void initBox2D(){
        /* This method create a bod2d object and inserts it into the world. It also creates additional
         * components of the bullet
         * */

        //-------------//
        // Declaration //
        //-------------//
        BodyDef body_def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        //------------------------------//
        // Create new body in the world //
        //------------------------------//
        body_def.position.set(position.x, position.y);
        body_def.type = BodyDef.BodyType.KinematicBody;
        box_2d_body = world.createBody(body_def);

        //----------------------------------------------//
        // Define bullet fixture definition & collision //
        //----------------------------------------------//
        configBulletWidthLength();
        shape.setAsBox((width_length.x/2), (width_length.y/2));
        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.BULLET_BIT;
        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);
    }
    private void initBulletTextureRegions() {
        /* This method initialization the texture regions used to render the Bullets. The texture regions are stored
         * in a hash map and are accessible by string name of the animations. It also initialization variables of the class
         * sprites with default values upon starting the game */

        //-------------//
        // Declaration //
        //-------------//
        Array<TextureRegion> frames = new Array<TextureRegion>();

        //------------------------------------//
        // Single Frame Bullets or Projectile //
        //------------------------------------/
        // ---Blue Bullet--- //
        frames.add(new TextureRegion(screen.getAtlas().findRegion("bullets_8x8"),0 * 8,  0, 8, 8));
        animation_table_bullet.put("blue_bullet",  new Animation(1f, frames));
        frames.clear();

        // ----Blue Shell---- //
        frames.add(new TextureRegion(screen.getAtlas().findRegion("bullets_8x8"), 1 * 8, 0, 8, 81));
        animation_table_bullet.put("blue_shell",  new Animation(1f, frames));
        frames.clear();

        //----------------//
        // Initialization //
        //----------------//
        // The size to render the sprites. 1 meter high, 1 meter wide in ~my units
        setBounds(0, 0, 0.5f, 0.5f);
        render_bullet = animation_table_bullet.get("blue_bullet").getKeyFrame(animation_state_timer, true);
        setRegion(render_bullet);
    }

    public void draw(Batch batch){
        setRegion(render_bullet);
        super.draw(batch);
    }

    //----------------//
    // Getter Methods //
    //----------------//
    public boolean getAlive(){
        return alive;
    }
}
