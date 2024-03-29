package com.laurent.main.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.laurent.main.MutantAlienAssualtMobileZ;
import com.laurent.main.Scenes.Controller;
import com.laurent.main.Scenes.Hud;
import com.laurent.main.Sprites.BackGroundTrigger.WeaponDepot;
import com.laurent.main.Sprites.Enemies.Enemy;
import com.laurent.main.Sprites.Items.Bullet;
import com.laurent.main.Sprites.Items.BulletPool;
import com.laurent.main.Sprites.Items.Globe;
import com.laurent.main.Sprites.Items.Item;
import com.laurent.main.Sprites.Items.ItemDefintion;
import com.laurent.main.Sprites.Red_Droid;
import com.laurent.main.Tools.Box2dWorldCreator;
import com.laurent.main.Tools.MyAssetManager;
import com.laurent.main.Tools.MyQueryCallback;
import com.laurent.main.Tools.WorldContactListener;
//https://www.gamedevelopment.blog/libgdx-object-pooling/
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private Music music;
    private MyAssetManager ass_man;
    private Red_Droid player;
    private MutantAlienAssualtMobileZ game;
    private OrthographicCamera game_cam;
    private Viewport game_port;
    private Hud hud;
    private Controller controller;
    private TmxMapLoader map_loader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private int level_width;
    private int level_height;
    private World world;
    private Box2DDebugRenderer box_2d_debug_renderer;
    private Box2dWorldCreator creator;
    private TextureAtlas atlas;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDefintion> items_to_spawn;
    private final Array<Bullet> active_bullets = new Array<Bullet>();
    private final BulletPool bullet_pool;

    public PlayScreen(MutantAlienAssualtMobileZ game){
        atlas = new TextureAtlas("all_characters_pack_one.pack");
        this.game = game;



        //[Old] Please remove after changes
//        game_cam = new OrthographicCamera(MutantAlienAssualtMobileZ.V_WIDTH / MutantAlienAssualtMobileZ.PPM,
//                MutantAlienAssualtMobileZ.V_HEIGHT / MutantAlienAssualtMobileZ.PPM );
//
//
//
//        game_port = new StretchViewport(MutantAlienAssualtMobileZ.V_WIDTH / MutantAlienAssualtMobileZ.PPM,
//                MutantAlienAssualtMobileZ.V_HEIGHT / MutantAlienAssualtMobileZ.PPM,
//                game_cam);

        //[New] Fit the camera to the world not the world to the camera
        game_cam = new OrthographicCamera(MutantAlienAssualtMobileZ.V_WIDTH, MutantAlienAssualtMobileZ.V_HEIGHT);
        game_port = new StretchViewport(MutantAlienAssualtMobileZ.V_WIDTH, MutantAlienAssualtMobileZ.V_HEIGHT, game_cam);



        map_loader = new TmxMapLoader();
        map = map_loader.load("test_level.tmx");

        level_height = map.getProperties().get("height", Integer.class);
        level_width = map.getProperties().get("width", Integer.class);
        //[Old] Please remove after changes
//        renderer = new OrthogonalTiledMapRenderer(map, 1/MutantAlienAssualtMobileZ.PPM);
        //[New]
        renderer = new OrthogonalTiledMapRenderer(map, 1/16f);

        game_cam.position.set(game_port.getWorldWidth()/2, game_port.getWorldHeight()/2, 0);
        world = new World(new Vector2(0, -30), true);
        box_2d_debug_renderer = new Box2DDebugRenderer();

        hud = new Hud(game.batch);

        MyQueryCallback myQueryCallback = new MyQueryCallback();

        controller = new Controller(game.batch);


        ass_man = game.getMyAssetManager();
        music = ass_man.manager.get(ass_man.MUSIC_SAVING_THE_WORLD, Music.class);
        music.play();
        music.setLooping(true);


        creator = new Box2dWorldCreator(this);
        player = creator.getPlayer(); //new Red_Droid(this);

        world.setContactListener(new WorldContactListener());

        items = new Array<Item>();
        items_to_spawn = new LinkedBlockingQueue<ItemDefintion>();

        bullet_pool =  new BulletPool(16, 30, this);
    }

    public void spawnItem(ItemDefintion item_def){
        items_to_spawn.add(item_def);
    }

    public void handleSpawningItems(){
        if(!items_to_spawn.isEmpty()){
            if(!world.isLocked()) {
                ItemDefintion item_def = items_to_spawn.poll();
                if (item_def.type == Globe.class) {
                    items.add(new Globe(this, item_def.position.x, item_def.position.y));
                }
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {
    }

    public void handleInput(float dt){
        if(player.current_state != Red_Droid.AnimationState.DEAD) {
            if (controller.isJumpPressed()) {
                player.jump();
            }

            player.fireWeapon(controller.isFireWeaponPressed(), dt);

            if (controller.isRightPressed() && player.box_2d_body.getLinearVelocity().x <= MutantAlienAssualtMobileZ.MAX_INPUT_SPEED)
                player.box_2d_body.applyLinearImpulse(new Vector2(0.6f, 0), player.box_2d_body.getWorldCenter(), true);
            else if (controller.isLeftPressed() && player.box_2d_body.getLinearVelocity().x >= -MutantAlienAssualtMobileZ.MAX_INPUT_SPEED)
                player.box_2d_body.applyLinearImpulse(new Vector2(-0.6f, 0), player.box_2d_body.getWorldCenter(), true);
        }
    }

    public void update(float dt){
        handleInput(dt);


        world.step(1/60f, 6, 2);


        handleSpawningItems();

        player.update(dt);
        for (Enemy enemy : creator.getGreenRams()) {
            enemy.update(dt);
            if (!enemy.isDestroyed() && enemy.getX() < player.getX() + MutantAlienAssualtMobileZ.V_WIDTH){
                if(!world.isLocked() && !enemy.box_2d_body.isActive()) {
                    enemy.box_2d_body.setActive(true);
                }
            }
        }
        for (Item item: items)
            item.update(dt);
        for(WeaponDepot weapon_depot : creator.getWeaponDepots())
            weapon_depot.update(dt);
        for(Bullet bullet: active_bullets)
            bullet.update(dt);
        for(Bullet bullet: active_bullets) {
            if (bullet.getAlive() == false) {
                bullet_pool.free(bullet); // place back in pool
                active_bullets.removeValue(bullet, true); // remove bullet
            }
        }

        // Print how many bullet are currently active
        System.out.println("active bullet count\n");
        System.out.println(active_bullets.size);


        hud.update(dt);

        game_cam.position.x = player.box_2d_body.getPosition().x;
        game_cam.position.y = player.box_2d_body.getPosition().y;
        keepCamInBound();

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
            for(WeaponDepot weapon_depot : creator.getWeaponDepots()) {
                weapon_depot.draw(game.batch);
            }

            for (Enemy enemy : creator.getGreenRams())
                enemy.draw(game.batch);

            for (Item item : items)
                item.draw(game.batch);

            player.draw(game.batch);

            for(Bullet bullet: active_bullets)
                bullet.draw(game.batch);
        game.batch.end();



        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        controller.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

        game_port.update(width, height);
        //hud.resize(width, height);
        controller.resize(width, height);

    }


    //----------------//
    // Getter Methods // .................................................................... They get stuff.
    //----------------//
    public int getLevelHeight(){
        return level_height;
    }
    public int getLevelWidth(){
        return level_width;
    }
    public World getWorld(){
        return world;
    }
    public TiledMap getMap(){
        return map;
    }
    public MyAssetManager getAssMan(){
        return  ass_man;
    }
    public Array<Bullet> getActiveBullets(){return active_bullets;}
    public BulletPool getBulletPool(){return bullet_pool;}



    public void keepCamInBound(){
        game_cam.position.x = (game_cam.position.x < 0) ? 0 : game_cam.position.x;
        game_cam.position.x = (game_cam.position.x > level_width) ? level_width : game_cam.position.x;

        game_cam.position.y = (game_cam.position.y < 0) ? 0 : game_cam.position.y;
        game_cam.position.y = (game_cam.position.y > level_width) ? level_width : game_cam.position.y;
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

    public boolean gameOver(){
        if(player.current_state == Red_Droid.AnimationState.DEAD && player.getAnimationStateTimer() > 3){
            return true;
        }
        return false;
    }
}
