package com.laurent.main.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;

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
        box_2d_body = world.createBody(body_def);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.13f / 2, 0.13f / 2);

        fdef.filter.categoryBits = MutantAlienAssualtMobileZ.ITEM_BIT;
        fdef.filter.maskBits = MutantAlienAssualtMobileZ.DEFUALT_BIT |
                                MutantAlienAssualtMobileZ.BRICK_BIT |
                                MutantAlienAssualtMobileZ.OBJECT_BIT |
                                MutantAlienAssualtMobileZ.RED_DROID_BIT;

        fdef.shape = shape;
        box_2d_body.createFixture(fdef).setUserData(this);

    }

   // @Override
    //public void use(Red_Droid red_droid) {
    //    destroy();
    //}

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(box_2d_body.getPosition().x - getWidth()/2, box_2d_body.getPosition().y - getHeight()/2);
        velocity.y = box_2d_body.getLinearVelocity().y;
        box_2d_body.setLinearVelocity(velocity);
    }
}
