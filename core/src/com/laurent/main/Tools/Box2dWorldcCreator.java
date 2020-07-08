package com.laurent.main.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Sprites.Brick;
import com.laurent.main.Sprites.Coin;

public class Box2dWorldcCreator {

    public Box2dWorldcCreator(World world, TiledMap map){
        BodyDef body_def = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixture_def = new FixtureDef();
        Body body;

        // Ground object generations
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            body_def.type = BodyDef.BodyType.StaticBody;
            body_def.position.set((rect.getX() + rect.getWidth()/2) / MutantAlienAssualtMobileZ.PPM,
                    (rect.getY() + rect.getHeight()/2) / MutantAlienAssualtMobileZ.PPM);

            body = world.createBody(body_def);
            shape.setAsBox(rect.getWidth() / 2 / MutantAlienAssualtMobileZ.PPM,
                    rect.getHeight() / 2 / MutantAlienAssualtMobileZ.PPM);
            fixture_def.shape = shape;
            body.createFixture(fixture_def);
        }

        // Platform object generations
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Brick(world, map, rect);
        }

        // Coin object generations
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Coin(world, map, rect);
        }

    }
}
