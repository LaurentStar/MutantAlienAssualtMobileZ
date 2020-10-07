package com.laurent.main.Tools;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

public class MyQueryCallback implements QueryCallback {

    public MyQueryCallback(){};
    public boolean reportFixture(Fixture fixture) {

        return true;
    }
}
