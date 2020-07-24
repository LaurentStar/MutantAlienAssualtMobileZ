package com.laurent.main.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Screens.PlayScreen;


public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected Sound sound;
    protected PlayScreen screen;
    protected MapObject object;

    public InteractiveTileObject(PlayScreen screen, MapObject object){
        this.object = object;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();
        this.screen = screen;

        BodyDef body_def = new BodyDef();
        FixtureDef fixture_def = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        body_def.type = BodyDef.BodyType.StaticBody;
        body_def.position.set((bounds.getX() + bounds.getWidth()/2) / MutantAlienAssualtMobileZ.PPM,
                (bounds.getY() + bounds.getHeight()/2) / MutantAlienAssualtMobileZ.PPM);

        body = world.createBody(body_def);

        shape.setAsBox(bounds.getWidth() / 2 / MutantAlienAssualtMobileZ.PPM,
                bounds.getHeight() / 2 / MutantAlienAssualtMobileZ.PPM);
        fixture_def.shape = shape;
        fixture = body.createFixture(fixture_def);
    }

    public InteractiveTileObject(PlayScreen screen, Rectangle bounds){
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = bounds;
        this.screen = screen;

        BodyDef body_def = new BodyDef();
        FixtureDef fixture_def = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        body_def.type = BodyDef.BodyType.StaticBody;
        body_def.position.set((bounds.getX() + bounds.getWidth()/2) / MutantAlienAssualtMobileZ.PPM,
                (bounds.getY() + bounds.getHeight()/2) / MutantAlienAssualtMobileZ.PPM);

        body = world.createBody(body_def);

        shape.setAsBox(bounds.getWidth() / 2 / MutantAlienAssualtMobileZ.PPM,
                bounds.getHeight() / 2 / MutantAlienAssualtMobileZ.PPM);
        fixture_def.shape = shape;
        fixture = body.createFixture(fixture_def);
    }


    public void setCategoryFilter(short filter_bit){
        Filter filter = new Filter();
        filter.categoryBits = filter_bit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        return layer.getCell((int)(body.getPosition().x * MutantAlienAssualtMobileZ.PPM/16),
                (int)(body.getPosition().y * MutantAlienAssualtMobileZ.PPM/16));
    }


    public abstract void onHeadHit();
}
