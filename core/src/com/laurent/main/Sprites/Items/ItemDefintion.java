package com.laurent.main.Sprites.Items;

import com.badlogic.gdx.math.Vector2;

public class ItemDefintion {
    public Vector2 position;
    public Class<?> type;


    public ItemDefintion(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }
}
