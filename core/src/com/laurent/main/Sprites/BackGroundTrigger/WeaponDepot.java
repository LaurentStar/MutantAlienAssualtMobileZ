package com.laurent.main.Sprites.BackGroundTrigger;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.utils.Array;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.Sprites.Red_Droid;

public class WeaponDepot extends BackGroundTrigger{

    public enum State {CHARGING, READY, POWER_UP, OFF};
    public State current_state;
    public State previous_state;


    private QueryCallback queryCallback;
    private Animation<TextureRegion> machine_charging;
    private TextureRegion machine_off;
    private TextureRegion machine_ready;
    private TextureRegion machine_power_up;
    private boolean ready_to_disburse;
    private float x;
    private float y;

    public  WeaponDepot(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);


        current_state = State.CHARGING;
        previous_state = State.CHARGING;
        ready_to_disburse = false;


        Array<TextureRegion> frames = new Array<TextureRegion>();

        // Charging up
        for (int i=0; i<7; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("weapon_depot_33x44"), i*33, 0, 33, 44));
        machine_charging = new Animation(5f, frames);
        frames.clear();

        // weapon is ready.
        machine_ready = new TextureRegion(screen.getAtlas().findRegion("weapon_depot_33x44"), 33*7, 0, 33, 44);

        //Super Charged Your weapon
        machine_power_up = new TextureRegion(screen.getAtlas().findRegion("weapon_depot_33x44"), 33*8, 0, 33, 44);

        //No power
        machine_off = new TextureRegion(screen.getAtlas().findRegion("weapon_depot_33x44"), 33, 0, 33, 44);

        setBounds(bounds.getX()/MutantAlienAssualtMobileZ.UNIT_SCALE,
                bounds.getY()/MutantAlienAssualtMobileZ.UNIT_SCALE,
                2,
                3);

        box_2d_body.setActive(true);

        setRegion(machine_charging.getKeyFrame(state_timer));

        //Customer world query
        queryCallback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if(fixture.getUserData() != null && Red_Droid.class.isAssignableFrom(fixture.getUserData().getClass())){
                    if(((Red_Droid)fixture.getUserData()).isFire_weapon()){
                        if (ready_to_disburse){
                            disburseWeapon();
                            ((Red_Droid)fixture.getUserData()).dispenseWeaponFromDepot();
                            return true;
                        }
                    }
                }
                return true;
            }
        };
    }

    public void update(float dt){
        setRegion(getFrame(dt));
        world.QueryAABB(queryCallback, x, y, box_2d_body.getPosition().x + w, box_2d_body.getPosition().y + h);
    }

    public void disburseWeapon(){
        ready_to_disburse = false;
    }

    public TextureRegion getFrame(float dt){
        current_state = getState();

        TextureRegion region;
        switch(current_state){
            case CHARGING: region =  machine_charging.getKeyFrame(state_timer); break;
            case READY: region = machine_ready; break;
            case POWER_UP: region = machine_power_up; break;
            default: region = machine_off; break;
        }

        state_timer = current_state == previous_state ? state_timer + dt : 0;
        previous_state = current_state;
        return region;
    }

    public State getState(){

        if (current_state == State.OFF || previous_state == State.OFF)
            return State.OFF;
        else if(current_state == State.READY && ready_to_disburse == false)
            return State.CHARGING;
        else if(machine_charging.isAnimationFinished(state_timer)) {
            ready_to_disburse = true;
            return State.READY;
        }
        else
            return current_state;
    }

    public boolean getReadyToDisburse(){
        return ready_to_disburse;
    }
    @Override
    public void defineBackGroundObject(Rectangle rect){




        BodyDef body_def = new BodyDef();
        body_def.position.set(
                (rect.getX() + rect.getWidth()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE,
                (rect.getY() + rect.getHeight()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE);

        body_def.type = BodyDef.BodyType.StaticBody;
        box_2d_body = world.createBody(body_def);

        FixtureDef fdef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/2, h/2);

        //EdgeShape shape = new EdgeShape();
        //shape.set(new Vector2(-w/2f, (-h/2f)), new Vector2(w/2f, -h/2f));

        fdef.isSensor = true;
        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.BACKGROUND_MACHINE_BIT;
        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);

        x = box_2d_body.getPosition().x - w/2;
        y = box_2d_body.getPosition().y - h/2;
    }

    public float tmpw(){ return w; }
    public float tmph(){ return h; }
    public float tmpx(){ return x; }
    public float tmpy(){ return y;}

}
