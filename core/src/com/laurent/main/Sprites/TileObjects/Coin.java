package com.laurent.main.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Scenes.Hud;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.Sprites.Items.Globe;
import com.laurent.main.Sprites.Items.ItemDefintion;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tile_set;
    private final int BLANK_COIN = 478;
    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tile_set = map.getTileSets().getTileSet("assualt_tile_map");
        fixture.setUserData(this);
        setCategoryFilter(MutantAlienAssualtMobileZ.COIN_BIT);
        sound = screen.getAssMan().manager.get(screen.getAssMan().SOUND_SWIFT_MOVEMENT);
    }

    @Override
    public void onHeadHit() {
        if (getCell().getTile().getId() !=  BLANK_COIN) {
            Gdx.app.log("coin", "collision");
            getCell().setTile(tile_set.getTile(BLANK_COIN));
            sound.play();
            Hud.addScore(100);
        }
        else{
            setCategoryFilter(MutantAlienAssualtMobileZ.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(1000);
            if(object.getProperties().containsKey("globe")) {
                screen.spawnItem(new ItemDefintion(
                        new Vector2(body.getPosition().x, body.getPosition().y + 16 / MutantAlienAssualtMobileZ.PPM), Globe.class));

            }
        }
    }
}