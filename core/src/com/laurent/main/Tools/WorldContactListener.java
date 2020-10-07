package com.laurent.main.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Sprites.Enemies.Enemy;
import com.laurent.main.Sprites.Red_Droid;
import com.laurent.main.Sprites.TileObjects.InteractiveTileObject;

import java.util.AbstractMap;


public class WorldContactListener implements ContactListener {


    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int collision_def = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // Could potential rewrite...maybe obsolete
        if (fixA.getUserData() == "head" || fixB.getUserData() == "head") {
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }

        handleSensorFixtureBeginContact(fixA);
        handleSensorFixtureBeginContact(fixB);

        handleFixtureBeginContact(fixA, fixB);


        /*if (fixA != null && fixB != null ){
            switch (collision_def) {
            /*case MutantAlienAssualtMobileZ.ENEMY_HEAD_BIT | MutantAlienAssualtMobileZ.RED_DROID_BIT:
                if(fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else
                    ((Enemy)fixB.getUserData()).hitOnHead();
                break;
            case MutantAlienAssualtMobileZ.ENEMY_BIT | MutantAlienAssualtMobileZ.DEFUALT_BIT:
                if(fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
                case MutantAlienAssualtMobileZ.RED_DROID_BIT | MutantAlienAssualtMobileZ.ENEMY_BIT:

                    if( !AbstractMap.SimpleEntry.class.isAssignableFrom(fixA.getUserData().getClass())
                            && !AbstractMap.SimpleEntry.class.isAssignableFrom(fixB.getUserData().getClass())) {

                        if (fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.ENEMY_BIT)
                            //((Red_Droid)fixA.getUserData()).hit();
                            ((Enemy) fixB.getUserData()).hitOnHead();
                        else
                            // ((Red_Droid)fixB.getUserData()).hit();
                            ((Enemy) fixB.getUserData()).hitOnHead();
                    }
                    break;
            case MutantAlienAssualtMobileZ.ENEMY_BIT | MutantAlienAssualtMobileZ.ENEMY_BIT:
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                break;
            case MutantAlienAssualtMobileZ.ITEM_BIT | MutantAlienAssualtMobileZ.DEFUALT_BIT:
                if(fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MutantAlienAssualtMobileZ.ITEM_BIT | MutantAlienAssualtMobileZ.RED_DROID_BIT:
                if(fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Red_Droid) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Red_Droid) fixA.getUserData());
                break;
                }
        }*/
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        handleSensorFixtureEndContact(fixA);
        handleSensorFixtureEndContact(fixB);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        handleSensorFixtureBeginContact(fixA);
        handleSensorFixtureBeginContact(fixB);
    }


    protected void handleSensorFixtureBeginContact(Fixture fix){
        if (fix.getUserData() != null && AbstractMap.SimpleEntry.class.isAssignableFrom(fix.getUserData().getClass())){

            AbstractMap.SimpleEntry<String, Class<?>> pair = (AbstractMap.SimpleEntry) fix.getUserData();
            String str = (String) pair.getKey();

            switch (str) {
                case "player_bottom":
                    {AbstractMap.SimpleEntry<String, Red_Droid> tmp = ((AbstractMap.SimpleEntry) fix.getUserData());
                    tmp.getValue().setStatusFlag(MutantAlienAssualtMobileZ.Status.MIDAIR, false);} break;
                case "enemy_right":
                case "enemy_left":
                {AbstractMap.SimpleEntry<String, Enemy> tmp = ((AbstractMap.SimpleEntry) fix.getUserData());
                    tmp.getValue().reverseVelocity(true, false);} break;

            }
        }
    }
    protected void handleSensorFixtureEndContact(Fixture fix){
        if (fix.getUserData() != null && AbstractMap.SimpleEntry.class.isAssignableFrom(fix.getUserData().getClass())){

            AbstractMap.SimpleEntry<String, Class<?>> pair = (AbstractMap.SimpleEntry) fix.getUserData();
            String str = (String) pair.getKey();

            switch (str) {
                case "player_bottom":
                    AbstractMap.SimpleEntry<String, Red_Droid> tmp = ((AbstractMap.SimpleEntry) fix.getUserData());
                    tmp.getValue().setStatusFlag(MutantAlienAssualtMobileZ.Status.MIDAIR, true);
            }
        }
    }

    protected void handleFixtureBeginContact(Fixture fixA, Fixture fixB){
        int collision_def = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        if (fixA.getUserData() != null && fixB.getUserData() != null){
            if(!AbstractMap.SimpleEntry.class.isAssignableFrom(fixA.getUserData().getClass())
                    && !AbstractMap.SimpleEntry.class.isAssignableFrom(fixB.getUserData().getClass())) {

                switch (collision_def) {
                    case MutantAlienAssualtMobileZ.RED_DROID_BIT | MutantAlienAssualtMobileZ.ENEMY_BIT:
                        if (fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.RED_DROID_BIT)
                            ((Red_Droid)fixA.getUserData()).hit();
                        else
                            ((Red_Droid)fixB.getUserData()).hit();
                        break;
                    /*case MutantAlienAssualtMobileZ.RED_DROID_BIT | MutantAlienAssualtMobileZ.BACKGROUND_MACHINE_BIT:
                        //if the player fires presses the fire trigger while in contact with the machine
                        Fixture fixP = (fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.RED_DROID_BIT) ? fixA : fixB;
                        Fixture fixM = (fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.BACKGROUND_MACHINE_BIT) ? fixA : fixB;

                        if(((Red_Droid)fixP.getUserData()).isFire_weapon()){
                            if(((WeaponDepot)fixP.getUserData()).getReadyToDisburse()){
                                ((WeaponDepot)fixM.getUserData()).disburseWeapon();
                                ((Red_Droid)fixP.getUserData()).getWeaponFromDepot();
                            }
                        }*/


                }
            }
        }
    }
}
