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
import com.badlogic.gdx.utils.Array;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.Sprites.TileObjects.Brick;
import com.laurent.main.Sprites.TileObjects.Coin;
import com.laurent.main.Sprites.Enemies.GreenRam;

public class Box2dWorldcCreator {
    private Array<GreenRam> green_rams;
    public Box2dWorldcCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
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

        //create object bodies/fixtures
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            body_def.type = BodyDef.BodyType.StaticBody;
            body_def.position.set((rect.getX() + rect.getWidth()/2) / MutantAlienAssualtMobileZ.PPM,
                    (rect.getY() + rect.getHeight()/2) / MutantAlienAssualtMobileZ.PPM);

            body = world.createBody(body_def);
            shape.setAsBox(rect.getWidth() / 2 / MutantAlienAssualtMobileZ.PPM,
                    rect.getHeight() / 2 / MutantAlienAssualtMobileZ.PPM);
            fixture_def.shape = shape;
            fixture_def.filter.categoryBits = MutantAlienAssualtMobileZ.OBJECT_BIT;
            body.createFixture(fixture_def);
        }


        // Platform object generations
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Brick(screen, rect);
        }

        // Coin object generations
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }

        //Green_rams
        green_rams = new Array<GreenRam>();
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            green_rams.add(new GreenRam(screen,
                    rect.getX()/MutantAlienAssualtMobileZ.PPM,
                    rect.getY()/MutantAlienAssualtMobileZ.PPM));
        }


    }

    public Array<GreenRam> getGreenRams() {
        return green_rams;
    }
}
