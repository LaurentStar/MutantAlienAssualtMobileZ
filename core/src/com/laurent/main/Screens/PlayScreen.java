package com.laurent.main.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Scenes.Controller;
import com.laurent.main.Scenes.Hud;
import com.laurent.main.Sprites.Red_Droid;
import com.laurent.main.Tools.Box2dWorldcCreator;
import com.laurent.main.Tools.WorldContactListener;

public class PlayScreen implements Screen {

    private Red_Droid player;
    private MutantAlienAssualtMobileZ game;
    private OrthographicCamera game_cam;
    private Viewport game_port;
    private Hud hud;
    private Controller controller;

    private TmxMapLoader map_loader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer box_2d_debug_renderer;
    private TextureAtlas atlas;




    //Texture texture;

    public PlayScreen(MutantAlienAssualtMobileZ game){
        atlas = new TextureAtlas("redDroid_and_enemies.pack");
        this.game = game;
        game_cam = new OrthographicCamera();
        game_port = new StretchViewport(MutantAlienAssualtMobileZ.V_WIDTH / MutantAlienAssualtMobileZ.PPM,
                MutantAlienAssualtMobileZ.V_HEIGHT / MutantAlienAssualtMobileZ.PPM,
                game_cam);

        hud = new Hud(game.batch);
        map_loader = new TmxMapLoader();
        map = map_loader.load("test_level.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/MutantAlienAssualtMobileZ.PPM);
        game_cam.position.set(game_port.getWorldWidth()/2, game_port.getWorldHeight()/2, 0);
        world = new World(new Vector2(0, -10), true);
        box_2d_debug_renderer = new Box2DDebugRenderer();

        controller = new Controller(game.batch);

        new Box2dWorldcCreator(world, map);
        player = new Red_Droid(world, this);


        world.setContactListener(new WorldContactListener());

    }


    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {
    }

    public void handleInput(float dt){

        if (controller.isJumpPressed()) {
            player.jump();
        }
        if(controller.isRightPressed() && player.box_2d_body.getLinearVelocity().x <= 2)
            player.box_2d_body.applyLinearImpulse(new Vector2(0.1f, 0), player.box_2d_body.getWorldCenter(), true);
        else if (controller.isLeftPressed()  && player.box_2d_body.getLinearVelocity().x >= -2)
            player.box_2d_body.applyLinearImpulse(new Vector2(-0.1f, 0), player.box_2d_body.getWorldCenter(), true);

    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f, 6, 2);
        player.update(dt);
        hud.update(dt);

        game_cam.position.x = player.box_2d_body.getPosition().x;
        game_cam.position.y = player.box_2d_body.getPosition().y;

        game_cam.update();
        renderer.setView(game_cam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();

        box_2d_debug_renderer.render(world, game_cam.combined);

        game.batch.setProjectionMatrix(game_cam.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();


        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        controller.draw();
    }

    @Override
    public void resize(int width, int height) {
        game_port.update(width, height);
        controller.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        box_2d_debug_renderer.dispose();
        hud.dispose();
    }
}
