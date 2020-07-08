package com.laurent.main.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Scenes.Hud;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tile_set;
    private final int BLACK_COIN = 478;
    public Coin(World world, TiledMap map, Rectangle bounds){
        super(world, map, bounds);
        tile_set = map.getTileSets().getTileSet("assualt_tile_map");
        fixture.setUserData(this);
        setCategoryFilter(MutantAlienAssualtMobileZ.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        if (getCell().getTile().getId() !=  BLACK_COIN) {
            Gdx.app.log("coin", "collision");
            getCell().setTile(tile_set.getTile(BLACK_COIN));
            Hud.addScore(100);
        }
        else{
            setCategoryFilter(MutantAlienAssualtMobileZ.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(1000);
        }

    }
}
