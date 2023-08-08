package com.laurent.main.Sprites.Items;

import com.badlogic.gdx.utils.Pool;
import com.laurent.main.Screens.PlayScreen;

public class BulletPool extends Pool<Bullet> {
    private PlayScreen screen;
    // constructor with initial object count and max object count
    // max is the maximum of object held in the pool and not the
    // maximum amount of objects that can be created by the pool
    public BulletPool(int init, int max, PlayScreen screen) {
        super(init, max);
        this.screen = screen;
    }

    // make pool with default 16 initial objects and no max
    public BulletPool() {
        super();
    }

    // method to create a single object
    @Override
    protected Bullet newObject() {
        return new Bullet(screen);
    }
}
