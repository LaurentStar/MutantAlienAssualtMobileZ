package com.laurent.main.Tools;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class MyAssetManager {
    public final AssetManager manager = new AssetManager();
    public final String MUSIC_SAVING_THE_WORLD = "audio_music_saving_the_world_revamped.ogg";
    public final String SOUND_SWIFT_MOVEMENT = "audio_fx_swift_movement.wav";
    public final String SOUND_JUMP = "audio_fx_jump.wav";
    public final String SOUND_METAL_CLICK = "audio_fx_metal_click.ogg";
    public final String SOUND_DAMAGE = "audio_fx_damage.wav";



    public void loadMusic(){
        manager.load(MUSIC_SAVING_THE_WORLD, Music.class);
    }

    public void loadSounds(){
        manager.load(SOUND_SWIFT_MOVEMENT , Sound.class);
        manager.load(SOUND_JUMP, Sound.class);
        manager.load(SOUND_METAL_CLICK, Sound.class);
        manager.load(SOUND_DAMAGE, Sound.class);

    }
}
