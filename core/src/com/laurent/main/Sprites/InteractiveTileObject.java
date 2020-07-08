package com.laurent.main.Sprites;

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


public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;

    public InteractiveTileObject(World world, TiledMap map, Rectangle bounds){
        this.world = world;
        this.map = map;
        this.bounds = bounds;

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
