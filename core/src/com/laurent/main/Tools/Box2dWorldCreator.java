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
import com.laurent.main.Sprites.BackGroundTrigger.WeaponDepot;
import com.laurent.main.Sprites.Enemies.GreenRam;
import com.laurent.main.Sprites.Red_Droid;
import com.laurent.main.Sprites.TileObjects.Brick;
import com.laurent.main.Sprites.TileObjects.Coin;

public class Box2dWorldCreator {
    private Array<GreenRam> green_rams;
    private Array<WeaponDepot> weapon_depots;
    private Red_Droid player;
    public Box2dWorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef body_def = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixture_def = new FixtureDef();
        Body body;

        // Ground object generations
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            body_def.type = BodyDef.BodyType.StaticBody;

            float h = ((rect.getHeight()/MutantAlienAssualtMobileZ.UNIT_SCALE));
            float w = ((rect.getWidth()/MutantAlienAssualtMobileZ.UNIT_SCALE));

            body_def.position.set((rect.getX() + rect.getWidth()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE,
                    (rect.getY() + rect.getHeight()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE);

            body = world.createBody(body_def);
            shape.setAsBox( w/2, h/2);
            fixture_def.shape = shape;
            //Gdx.app.log("OBJECT_CREATION", Float.toString(rect.getHeight()));
            body.createFixture(fixture_def);
        }

        //create object bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            body_def.type = BodyDef.BodyType.StaticBody;
            float h = ((rect.getHeight()/MutantAlienAssualtMobileZ.UNIT_SCALE));
            float w = ((rect.getWidth()/MutantAlienAssualtMobileZ.UNIT_SCALE));

            body_def.position.set((rect.getX() + rect.getWidth()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE,
                    (rect.getY() + rect.getHeight()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE);

            body = world.createBody(body_def);
            shape.setAsBox( w/2, h/2);

            fixture_def.shape = shape;
            fixture_def.filter.categoryBits = MutantAlienAssualtMobileZ.OBJECT_BIT;
            body.createFixture(fixture_def);
        }


        // Platform object generations
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            new Brick(screen, rect);
        }

        // Coin object generations
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }

        //Green_rams
        green_rams = new Array<GreenRam>();
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            green_rams.add(new GreenRam(screen, rect.getX(), rect.getY()));
        }

        //Player
        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            player = new Red_Droid(screen,
                    (int)(rect.getX() + rect.getWidth()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE,
                    (int)(rect.getY() + rect.getHeight()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE);
        }

        //Weapon Depot
        weapon_depots = new Array<WeaponDepot>();
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            weapon_depots.add(new WeaponDepot(screen, rect));
        }
    }

    public Array<GreenRam> getGreenRams() {
        return green_rams;
    }
    public Array<WeaponDepot> getWeaponDepots() {
        return weapon_depots;
    }

    public Red_Droid getPlayer() {
        return player;
    }
}
