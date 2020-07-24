package com.laurent.main.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Scenes.Hud;
import com.laurent.main.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MutantAlienAssualtMobileZ.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("brick", "collision");
        setCategoryFilter(MutantAlienAssualtMobileZ.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);
    }
}