package com.laurent.main.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.Sprites.Red_Droid;

public class Globe extends Item {


    public Globe(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("walk"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f,0);
    }

    @Override
    public void defineItem() {
        BodyDef body_def = new BodyDef();
        body_def.position.set(getX(), getY());
        body_def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(body_def);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(13 / 2 / MutantAlienAssualtMobileZ.PPM,
                13 / 2 / MutantAlienAssualtMobileZ.PPM);

        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.ITEM_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.DEFUALT_BIT |
                                MutantAlienAssualtMobileZ.BRICK_BIT |
                                MutantAlienAssualtMobileZ.OBJECT_BIT |
                                MutantAlienAssualtMobileZ.RED_DROID_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

    }

    @Override
    public void use(Red_Droid red_droid) {
        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
