package com.laurent.main.Sprites.BackGroundTrigger;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.laurent.main.Screens.PlayScreen;
import com.laurent.main.MutantAlienAssualtMobileZ;

/**
 * The BackGroundTrigger class is a template used for all objects that the player/AI
 * must toggle an interactions. The player/AI should move through the box2d freely
 * but collision and toggling should still be detected.
 * @author  Laurent Mundell
 * @version 1.0
 * @since   2020-21-09
 */
public abstract  class BackGroundTrigger extends Sprite {
    protected World world;
    protected Rectangle bounds;
    protected Body box_2d_body;
    protected Sound sound;
    protected PlayScreen screen;
    protected float state_time;
    protected float state_timer;
    protected float h;
    protected float w;

    public BackGroundTrigger(PlayScreen screen, Rectangle bounds){
        this.world = screen.getWorld();
        this.bounds = bounds;
        this.screen = screen;

        state_timer = 0;
        state_time = 0;

        h = ((bounds.getHeight() / MutantAlienAssualtMobileZ.UNIT_SCALE));
        w = ((bounds.getWidth() / MutantAlienAssualtMobileZ.UNIT_SCALE));

        defineBackGroundObject(bounds);
    }


    protected abstract void defineBackGroundObject(Rectangle rect);
}
