package com.laurent.main.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Sprites.Enemies.Enemy;
import com.laurent.main.Sprites.Items.Item;
import com.laurent.main.Sprites.Red_Droid;
import com.laurent.main.Sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {


    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int collision_def = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // Could potential rewrite...maybe obsolete
        if (fixA.getUserData() == "head" || fixB.getUserData() == "head"){
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;

            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())){
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
            }
        }

        switch(collision_def){
            case MutantAlienAssualtMobileZ.ENEMY_HEAD_BIT | MutantAlienAssualtMobileZ.RED_DROID_BIT:
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
                if(fixA.getFilterData().categoryBits == MutantAlienAssualtMobileZ.RED_DROID_BIT)
                    ((Red_Droid)fixA.getUserData()).hit();
                else
                    ((Red_Droid)fixB.getUserData()).hit();
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

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
