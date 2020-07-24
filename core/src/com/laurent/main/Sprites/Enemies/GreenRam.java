package com.laurent.main.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;



public class GreenRam extends Enemy {
    private float state_time;
    private Animation<TextureRegion> walk_animation;
    private Array<TextureRegion> frames;
    private Sound sound;
    private boolean set_to_destroy;
    private boolean destroyed;

    public GreenRam(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i=0; i<5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("green_ram_gallope_20x21"), i*20, 0, 20, 21));
        walk_animation = new Animation(0.1f, frames);
        state_time = 0;
        setBounds(getX(), getY(), 16/MutantAlienAssualtMobileZ.PPM, 16/MutantAlienAssualtMobileZ.PPM);
        set_to_destroy = false;
        destroyed = false;
    }

    public void update(float dt){
        state_time += dt;
        if(set_to_destroy && destroyed == false ) {
            safeDestroy();
        }
        else if (destroyed == false) {
            setPosition(box_2d_body.getPosition().x - getWidth() / 2, box_2d_body.getPosition().y - getHeight() / 2);
            setRegion(walk_animation.getKeyFrame(state_time, true));
            box_2d_body.setLinearVelocity(velocity);
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef body_def = new BodyDef();
        body_def.position.set(getX(), getY());

        body_def.type = BodyDef.BodyType.DynamicBody;
        box_2d_body = world.createBody(body_def);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(13 / 2 / MutantAlienAssualtMobileZ.PPM,
                13 / 2 / MutantAlienAssualtMobileZ.PPM);//.setRadius(6 / MutantAlienAssualtMobileZ.PPM);

        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.ENEMY_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.DEFUALT_BIT |
                                MutantAlienAssualtMobileZ.BRICK_BIT |
                                MutantAlienAssualtMobileZ.COIN_BIT |
                                MutantAlienAssualtMobileZ.ENEMY_BIT |
                                MutantAlienAssualtMobileZ.OBJECT_BIT |
                                MutantAlienAssualtMobileZ.RED_DROID_BIT;

        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);


        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1/MutantAlienAssualtMobileZ.PPM);
        vertice[1] = new Vector2(5, 8).scl(1/MutantAlienAssualtMobileZ.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1/MutantAlienAssualtMobileZ.PPM);
        vertice[3] = new Vector2(3, 3).scl(1/MutantAlienAssualtMobileZ.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.ENEMY_HEAD_BIT;
        box_2d_body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void hitOnHead() {
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
