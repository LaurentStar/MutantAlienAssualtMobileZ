package com.laurent.main.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
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

        float h = ((bounds.getHeight() / MutantAlienAssualtMobileZ.UNIT_SCALE));
        float w = ((bounds.getWidth() / MutantAlienAssualtMobileZ.UNIT_SCALE));

        BodyDef body_def = new BodyDef();
        FixtureDef fixture_def = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        body_def.type = BodyDef.BodyType.StaticBody;
        body_def.position.set((bounds.getX() + bounds.getWidth()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE,
                (bounds.getY() + bounds.getHeight()/2) / MutantAlienAssualtMobileZ.UNIT_SCALE);

        body = world.createBody(body_def);

        shape.setAsBox(w/2, h/2);
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

        float h = ((bounds.getHeight()/ MutantAlienAssualtMobileZ.UNIT_SCALE));
        float w = ((bounds.getWidth() / MutantAlienAssualtMobileZ.UNIT_SCALE));

        body_def.type = BodyDef.BodyType.StaticBody;
        body_def.position.set((bounds.getX() + w/2), (bounds.getY() + h/2));

        body = world.createBody(body_def);

        shape.setAsBox(w/2 , h/2);
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
        Gdx.app.log("coin", Float.toString((body.getPosition().x))+Float.toString((body.getPosition().y)));

        return layer.getCell((int)(body.getPosition().x), (int)(body.getPosition().y));
    }


    public abstract void onHeadHit();
}
