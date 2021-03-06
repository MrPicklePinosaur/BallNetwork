/* Shinobi Network
 ______   ______   ______   ______   ______   __   __   ______
/\  ___\ /\  ___\ /\  == \ /\  ___\ /\  ___\ /\ "-.\ \ /\  ___\
\ \___  \\ \ \____\ \  __< \ \  __\ \ \  __\ \ \ \-.  \\ \___  \
 \/\_____\\ \_____\\ \_\ \_\\ \_____\\ \_____\\ \_\\"\_\\/\_____\
  \/_____/ \/_____/ \/_/ /_/ \/_____/ \/_____/ \/_/ \/_/ \/_____/

    All the screens in the game that need to be rendererd
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import sun.swing.plaf.GTKKeybindings;

import java.util.ArrayList;

class MainmenuScreen implements Screen {

    private Stage stage;
    private Table rootTable;
    private SpriteBatch batch = new SpriteBatch();
    private Label title;

    public MainmenuScreen() {

        TextButton play_button = new TextButton("Play",Global.skin);
        play_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.game.setScreen(Global.game.game_screen);
                Global.server_socket.send_msg(MT.STARTGAME,"");
                Global.server_socket.enableGIP();
                AudioPlayer.play_sound("button_click");
            }
        });

        TextButton inventory_button = new TextButton("Inventory",Global.skin);
        inventory_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                AudioPlayer.play_sound("button_click");
                Global.game.setScreen(Global.game.inventory_screen);
            }
        });

        TextButton options_button = new TextButton("Options",Global.skin);
        options_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                AudioPlayer.play_sound("button_click");
                Global.game.setScreen(Global.game.options_screen);
            }
        });


        TextButton quit_button = new TextButton("Exit Game",Global.skin);
        quit_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                AudioPlayer.play_sound("button_click");
                Gdx.app.exit();
            }
        });

        Table stats = new Table();
        float kdr = Global.user_data.getTotalKills();
        if (Global.user_data.getTotalKills() != 0) { //make sure theres mp divison by zero
            kdr = Global.user_data.getTotalKills() / Global.user_data.getTotalDeaths();
        }

        Label kd = new Label("KDR: "+kdr,Global.skin);
        Label kills = new Label("Kills: "+Global.user_data.getTotalKills(),Global.skin);
        Label deaths = new Label("Deaths: "+Global.user_data.getTotalDeaths(),Global.skin);
        Label damage = new Label("Damage Dealt: "+Global.user_data.getTotalDamage(),Global.skin);
        kd.setStyle(Global.labelStyle);
        kills.setStyle(Global.labelStyle);
        deaths.setStyle(Global.labelStyle);
        damage.setStyle(Global.labelStyle);

        stats.add(kd).expandY().right().padRight(10f);
        stats.row();
        stats.add(kills).expandY().right().padRight(10f);
        stats.row();
        stats.add(deaths).expandY().right().padRight(10f);
        stats.row();
        stats.add(damage).expandY().right().padRight(10f);

        rootTable = new Table();
        rootTable.setFillParent(true);
        Table buttonTable = new Table();

        this.stage = new Stage();
        stage.addActor(play_button);
        stage.addActor(inventory_button);
        stage.addActor(options_button);
        stage.addActor(quit_button);
        stage.addActor(stats);
        stage.addActor(rootTable);
        stage.addActor(buttonTable);

        Pixmap menuPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        menuPixmap.setColor(Color.valueOf("4c4c4c80"));
        menuPixmap.fill();
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(menuPixmap))));

        buttonTable.add(play_button).pad(10f).width(play_button.getWidth()*2).height(play_button.getHeight()*2);
        buttonTable.row();
        buttonTable.add(inventory_button).center().pad(10f).width(inventory_button.getWidth()*2).height(inventory_button.getHeight()*2);
        buttonTable.row();
        buttonTable.add(options_button).center().pad(10f).width(options_button.getWidth()*2).height(options_button.getHeight()*2);
        buttonTable.row();
        buttonTable.add(quit_button).center().pad(10f).width(quit_button.getWidth()*2).height(quit_button.getHeight()*2);
        buttonTable.center();

        rootTable.add(buttonTable);
        rootTable.add(stats).expandX().right().padRight(10f).fillY();
        rootTable.bottom();

        title = new Label("Shinobi Network!",Global.skin);
        title.setStyle(Global.labelStyle);
        title.setPosition(title.getWidth()*2.5f,Global.SCREEN_HEIGHT/2f-title.getHeight()/2f);
        title.setFontScale(1.5f);

    }

    @Override public void render(float delta) {
        this.stage.act(delta);
        this.stage.draw();

        batch.begin();
        title.draw(batch,1f);
        batch.end();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() {
        this.stage.clear();
        this.stage.dispose();
    }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class GameScreen implements Screen {
    //init sprites (REMOVE LATER)
    //static Sprite background = new Sprite(new Texture("mountain_temple.png"));
    static Sprite bg_bot =new Sprite(new Texture("map_bot.png"));
    static Sprite bg_top =new Sprite(new Texture("map_top.png"));
    //big bois
    private Stage stage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    //input
    private InputMultiplexer inputMultiplexer;
    private InputHandler input_handler;

    private boolean show_menu = false;
    private Table pause_menu;
    private Table respawn_menu;
    private Inventory inv;
    private Options options_menu;
    //static Leaderboard lb;

    public GameScreen() {
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        //Pause menu table
        TextButton resume_button = new TextButton("Resume",Global.skin);
        resume_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                AudioPlayer.play_sound("button_click");
                Global.game.game_screen.hide_menu();
            }
        });
        TextButton inventory_button = new TextButton("Inventory",Global.skin);
        inventory_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                show_inv();
                AudioPlayer.play_sound("button_click");
            }
        });
        TextButton options_button = new TextButton("Options",Global.skin);
        options_button.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event,float x,float y) {
                show_options();
                AudioPlayer.play_sound("button_click");
            }
        });
        TextButton exit_button = new TextButton("Leave Game",Global.skin);
        exit_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                Global.server_socket.send_msg(MT.LEAVEGAME,"");
                Global.server_socket.disableGIP();
                AudioPlayer.play_sound("button_click");
                Global.game.setScreen(Global.game.mainmenu_screen);
            }
        });

        //add all the buttons to the table
        this.pause_menu = new Table();
        pause_menu.setBounds(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
        pause_menu.add(resume_button).pad(10f).width(resume_button.getWidth()*2).height(resume_button.getHeight()*2);
        pause_menu.row();
        pause_menu.add(inventory_button).center().pad(10f).width(inventory_button.getWidth()*2).height(inventory_button.getHeight()*2);
        pause_menu.row();
        pause_menu.add(options_button).center().pad(10f).width(options_button.getWidth()*2).height(exit_button.getHeight()*2);
        pause_menu.row();
        pause_menu.add(exit_button).center().pad(10f).width(exit_button.getWidth()*2).height(exit_button.getHeight()*2);
        pause_menu.center();
        this.pause_menu.setVisible(false);

        //Inventory
        this.inv = new Inventory(this.stage);
        this.inv.hide_inv();

        //Pause menu options
        this.options_menu = new Options(this.stage);

        //Leaderboard
        //this.leaderboard = new Leaderboard("FFA",this.stage);

        //Choose class menu
        this.respawn_menu = new Table();
        respawn_menu.setBounds(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);

        final String[] class_list = new String[]{"ninja","archer","warrior","wizard"};
        for (int i = 0; i < 4; i++) { //create four buttons, one for each class
            final int index = i;
            ImageButton choose = new ImageButton(new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("choose_class_up"))),new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("choose_class_down"))));
            Image class_image = new Image(AssetManager.getUIImage(class_list[index]));
            class_image.setFillParent(true);
            choose.addActor(class_image);
            choose.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event,float x,float y) {
                    //send server the loadout we want to play as
                    String[] load_out = Global.user_data.getLoadout(class_list[index]); //TODO: DONT TRUST USER WITH THIS DATA
                    String msg = class_list[index]+","+load_out[0]+","+load_out[1];
                    Global.server_socket.send_msg(MT.RESPAWN,msg);
                    hide_death_screen(); //after we choose the class, we can hide the window
                    AudioPlayer.play_sound("spawn_sound");
                }
            });
            respawn_menu.add(choose).pad(10);
        }
        respawn_menu.row();
        for (int i = 0; i < 4; i++) { //add labels with the name of each class
            Label class_name = new Label(class_list[i],Global.skin);
            class_name.setStyle(Global.labelStyle);
            class_name.setFontScale(0.5f);
            respawn_menu.add(class_name);
        }

        //add stuff to stage
        this.stage.addActor(pause_menu);
        this.stage.addActor(respawn_menu);

        //input stuff
        this.inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        this.input_handler = new InputHandler();
        inputMultiplexer.addProcessor(this.input_handler);
    }

    @Override public void render(float delta) {
        //simple draw calls
        batch.begin();
        bg_bot.draw(batch);
        Entity.drawAll(batch);
        Particle.draw_all(batch, Gdx.graphics.getDeltaTime());
        bg_top.draw(batch);
        HealthTracker.drawAll(batch);
        batch.end();

        //draw UI
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        //dim screen if menu is open
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (this.show_menu) { ScreenUtils.dimScreen(shapeRenderer,0.3f); } //dim screen if menu is open
        shapeRenderer.end();

        //update stuff
        float deltaTime = Gdx.graphics.getDeltaTime();
        Global.updateInput();
        this.input_handler.sendMouse();
        this.input_handler.handleInput();
        //if(Gdx.input.isKeyJustPressed(Input.Keys.TAB))
        Entity.stepFrameAll(deltaTime);

        //cam stuff
        Global.camera.moveCam();
        batch.setProjectionMatrix(Global.camera.getCam().combined);
        Global.camera.updateCam();
    }

    //toggles
    public void show_menu() {
        this.show_menu = true;
        this.pause_menu.setVisible(true);
    }
    public void hide_menu() {
        this.show_menu = false;
        this.pause_menu.setVisible(false);
        this.inv.hide_inv();
        this.options_menu.hide_options();
    }
    public void show_inv() {
        this.pause_menu.setVisible(false);
        this.inv.show_inv();
    }
    public void show_options(){
        this.pause_menu.setVisible(false);
        this.options_menu.show_options();
    }
    public void show_death_screen() {
        this.hide_menu();
        this.respawn_menu.setVisible(true);
    }
    public void hide_death_screen() {
        this.respawn_menu.setVisible(false);
    }

    @Override public void show() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.input.setInputProcessor(inputMultiplexer);
        this.options_menu.hide_options();
    }

    @Override public void hide() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
        this.hide_menu();
        this.hide_death_screen();
    }

    public boolean isRespawnMenuVisible() { return this.respawn_menu.isVisible(); }
    public boolean isMenuVisible() { return this.show_menu; }

    @Override public void dispose() {
        this.stage.clear();
        this.stage.dispose();
    }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }

}

class AwaitauthScreen implements Screen { //simple loading screen while server is authing creds (tight now its just puthc black

    private Stage stage;
    public AwaitauthScreen() {
        this.stage = new Stage();
    }

    @Override public void render(float delta) { }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() {
        this.stage.clear();
        this.stage.dispose();
    }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class RetryconnectionScreen implements Screen { //if server connection failed, this screen wwhos

    private Stage stage;

    private boolean connected = false;
    private TextButton retry_button;
    private TextButton.TextButtonStyle tbs;

    public RetryconnectionScreen() {
        this.stage = new Stage();

        //make a cool looking button
        this.tbs = new TextButton.TextButtonStyle();
        Drawable db = Global.skin.newDrawable("buttonColor",Color.GRAY);
        db.setLeftWidth(Global.SCREEN_WIDTH/75f);
        db.setRightWidth(Global.SCREEN_WIDTH/100f);
        db.setTopHeight(Global.SCREEN_HEIGHT/55f);
        db.setBottomHeight(Global.SCREEN_HEIGHT/100f);
        this.tbs.up = db;
        db = Global.skin.newDrawable("buttonOverColor",Color.BLUE);
        this.tbs.over = db;
        db = Global.skin.newDrawable("buttonDownColor",Color.GREEN);
        this.tbs.down = db;
        this.tbs.font = Global.skin.getFont("PixelFont");
        this.tbs.overFontColor = Color.BLACK;
        this.tbs.fontColor = Color.WHITE;
        this.tbs.pressedOffsetX = 3;
        this.tbs.pressedOffsetY = -3;
        Global.skin.add("PixelFontStyle",tbs);
        this.retry_button = new TextButton("Retry connection",Global.skin,"PixelFontStyle");
        this.retry_button.setStyle(this.tbs);
        this.retry_button.setPosition(Global.SCREEN_WIDTH/2f-this.retry_button.getWidth()/2f,Global.SCREEN_HEIGHT/2f-this.retry_button.getHeight()/2f);
        retry_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                //if the retry connection button is pressed, attempt to connect to server
                connected = Global.game.attempt_connection(Global.server_ip, Global.server_port);
                AudioPlayer.play_sound("button_click");
            }
        });
        stage.addActor(retry_button);
    }

    @Override public void render(float delta) {
        stage.act(delta);
        stage.draw();

        if (this.connected) { Global.game.setScreen(Global.game.login_screen); } //if user connects, take them to login screen
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void hide() { }

    @Override public void dispose() {
        this.stage.clear();
        this.stage.dispose();
    }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class Inventory {

    private Stage stage;
    private Table inventory_grid;
    private Table loadout_inv;
    private Table tabs;
    private Label inv_name;

    //inventory pages
    private Table page;
    private ImageButton left;
    private ImageButton right;
    private Label page_label;

    //item descip
    private Table item_info;
    private Label item_name;
    private Label stat_text;
    private Label special_text;
    private String selected_item;
    private TextButton equip_button;

    //vars
    private String current_tab;
    private int page_num;
    private static final int grid_x = 4;
    private static final int grid_y = 6;

    public Inventory(Stage screen_stage) {
        this.stage = screen_stage;

        //Add a cool label
        this.inv_name = new Label(Global.user_data.getUsername()+"'s Inventory",Global.skin);
        inv_name.setStyle(Global.labelStyle);
        inv_name.setPosition(Global.SCREEN_WIDTH/8,Global.SCREEN_HEIGHT*7/8);

        //INVENTORY
        //some basic settings for the table
        this.inventory_grid = new Table();
        inventory_grid.setBounds(Global.SCREEN_WIDTH/8,0,Global.SCREEN_WIDTH*3/8,Global.SCREEN_HEIGHT);
        inventory_grid.setFillParent(true);
        inventory_grid.left();

        //Loadout slots
        this.loadout_inv = new Table();

        //Tabs
        this.current_tab = "";
        TextButton allItems_button = new TextButton("All items",Global.skin);
        allItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                current_tab = "";
                page_num = 1;
                hide_loadout();
                switch_page();
                AudioPlayer.play_sound("button_click");
            }
        });
        TextButton ninjaItems_button = new TextButton("Ninja",Global.skin);
        ninjaItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                current_tab = "katana,waki";
                page_num = 1;
                show_loadout("ninja");
                switch_page();
                AudioPlayer.play_sound("button_click");
            }
        });
        TextButton archerItems_button = new TextButton("Archer",Global.skin);
        archerItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                current_tab = "bow,quiver";
                page_num = 1;
                show_loadout("archer");
                switch_page();
                AudioPlayer.play_sound("button_click");
            }
        });
        TextButton warriorItems_button = new TextButton("Warrior",Global.skin);
        warriorItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                current_tab = "sword,helm";
                page_num = 1;
                show_loadout("warrior");
                switch_page();
                AudioPlayer.play_sound("button_click");
            }
        });
        TextButton wizardItems_button = new TextButton("Wizard",Global.skin);
        wizardItems_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                current_tab = "staff,spell";
                page_num = 1;
                show_loadout("wizard");
                switch_page();
                AudioPlayer.play_sound("button_click");
            }
        });

        this.tabs = new Table();
        tabs.setBounds(Global.SCREEN_WIDTH/8,0,Global.SCREEN_HEIGHT,Global.SCREEN_WIDTH/8);
        tabs.add(wizardItems_button);
        tabs.add(warriorItems_button);
        tabs.add(archerItems_button);
        tabs.add(ninjaItems_button);
        tabs.add(allItems_button);
        tabs.setTransform(true);
        tabs.rotateBy(90);
        tabs.bottom();
        //tabs.setDebug(true);

        //page flipper
        this.page = new Table();
        this.page_num = 1;
        this.left = new ImageButton(new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("left_arrow"))));
        left.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                page_num--;
                switch_page();
                AudioPlayer.play_sound("button_click");
            }
        });
        this.right = new ImageButton(new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("right_arrow"))));
        right.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                page_num++;
                switch_page();
                AudioPlayer.play_sound("button_click");
            }
        });
        this.page_label = new Label(""+page_num,Global.skin);
        page_label.setStyle(Global.labelStyle);

        page.add(left).pad(10);
        page.add(page_label);
        page.add(right).pad(10);
        page.setPosition(100,100);

        //Item info box
        this.item_info = new Table();
        this.item_name = new Label("",Global.skin);
        item_name.setStyle(Global.labelStyle);
        item_name.setFontScale(0.5f);
        this.stat_text = new Label("",Global.skin);
        stat_text.setStyle(Global.labelStyle);
        stat_text.setFontScale(0.3f);
        //TODO: change colour
        this.special_text = new Label("",Global.skin);
        special_text.setStyle(Global.labelStyle);
        special_text.setFontScale(0.25f);
        //TODO: change colour
        this.equip_button = new TextButton("Equip",Global.skin);
        equip_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                String item_type = AssetManager.getItemDescrip(selected_item).getItemType();
                String filter = Global.user_data.setLoadout(selected_item,item_type);
                refresh_loadout(filter);
                AudioPlayer.play_sound("button_click");
            }
        });

        item_info.setBounds(Global.SCREEN_WIDTH*4/8,0,Global.SCREEN_WIDTH*4/8,Global.SCREEN_HEIGHT/4f);
        item_info.add(item_name);
        item_info.row();
        item_info.add(stat_text).pad(5);
        item_info.row();
        item_info.add(special_text).pad(5);
        item_info.row();
        item_info.add(equip_button).pad(5);

        Label blank = new Label("",Global.skin);
        blank.setPosition(-Global.SCREEN_WIDTH,-Global.SCREEN_HEIGHT);
        blank.setDebug(true);
        //Now add everything to the stage
        stage.addActor(inventory_grid);
        stage.addActor(loadout_inv);
        stage.addActor(tabs);
        stage.addActor(page);
        stage.addActor(item_info);
        stage.addActor(inv_name);
        stage.addActor(blank);
    }

    public void switch_page() { //flip pages
        page_num = MathUtils.clamp(page_num,1,999);
        if (this.page_num == 1) { this.left.setVisible(false); } //hide left arrow if on page 1
        else { this.left.setVisible(true); } //otherwise show it

        if (filterItems(current_tab).size() - (page_num-1)*grid_x*grid_y <= grid_x*grid_y) { //if there arent enough items to overflow onto the next page
            this.right.setVisible(false); }
        else { this.right.setVisible(true); }

        //refresh stuff
        page_label.setText(""+this.page_num);
        refreshInventory(current_tab);
        hide_item_info(); //hide item descrip box afterr switching tabs
    }

    public void show_item_info(String item_name) { //show the item info box
        ItemData item_data = AssetManager.getItemDescrip(item_name);

        this.item_info.setVisible(true); //turn the whole table on

        //UPDATE item info - read info from json
        this.item_name.setText(item_data.getDisplayName());
        this.stat_text.setText(item_data.getStatText());
        String spec_text = item_data.getSpecialText();
        if (!spec_text.equals("")) { this.special_text.setText("\'" + item_data.getSpecialText() + "\'"); }
        else { this.special_text.setText(""); }

        if (this.current_tab.equals("")) { //dont show equp button on all items tab
            this.equip_button.setVisible(false);
        } else { this.equip_button.setVisible(true); }

    }

    public void hide_item_info() {
        this.item_info.setVisible(false);
    }

    public void refreshInventory(String filter) { //filter inv by item type, if an empty string is provided, it means no filtere
        //TODO: when refreshing inventory, actually ask the server for a refresh
        this.inventory_grid.clearChildren();

        //populate the table with the contents of the user's inventory
        ArrayList<String> inv = this.filterItems(filter);

        //used to determine which page of the inventory we're on
        int item_offset = (page_num-1)*grid_x*grid_y;
        for (int i = 0; i < item_offset; i++) {
            inv.remove(0);
        }

        //textures
        Texture empty_slot_up = AssetManager.getUIImage("empty_slot_up");
        Texture empty_slot_down = AssetManager.getUIImage("empty_slot_down");

        for (int j = 0; j < grid_y; j++) { //6 rows
            for (int i = 0; i < grid_x; i++) { //4 columns
                final ImageButton slot = new ImageButton(new TextureRegionDrawable(new TextureRegion(empty_slot_up)),new TextureRegionDrawable(new TextureRegion(empty_slot_down)));
                slot.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent event,float x,float y) {
                        if (slot.getChildren().size > 1) { //size gratehr than 1 means that there is an item on this slot

                            //find out which slot the item belongs in and equip it
                            String item_name = slot.getName();
                            assert(item_name != null && !item_name.equals("")): "Slot name is empty";

                            show_item_info(item_name);
                            selected_item = item_name;

                            AudioPlayer.play_sound("inventory_click");
                        }
                    }
                });
                if (inv.size() > 0) { //go through client's inv list and draw them
                    String item_path = inv.get(0);
                    Image item = new Image(AssetManager.getSpritesheet(item_path));
                    item.setFillParent(true);
                    slot.addActor(item);
                    slot.setName(item_path);
                    inv.remove(0);
                }
                this.inventory_grid.add(slot).pad(10);
            }
            this.inventory_grid.row(); //move down a row
        }
    }

    public void refresh_loadout(String filter) { //refresh the loadout tabs
        assert (filter.equals("ninja") || filter.equals("archer") || filter.equals("warrior") || filter.equals("wizard")): "Invalid filter";
        String[] loadout = Global.user_data.getLoadout(filter); //the first item is the weapon, second is the ability

        this.loadout_inv.clear();

        Texture empty_slot = AssetManager.getUIImage("empty_slot_up");

        Label class_name = new Label(filter,Global.skin);
        class_name.setStyle(Global.labelStyle);

        //weapon slot
        Stack weapon_stack = new Stack();
        weapon_stack.add(new Image(empty_slot));
        if (loadout.length == 2) { weapon_stack.add(new Image(AssetManager.getSpritesheet(loadout[0]))); } //if the user actually has a weapon equipped

        //class preview
        Stack class_preview = new Stack();
        class_preview.add(new Image(AssetManager.getUIImage("selected_class")));
        class_preview.add(new Image(AssetManager.getUIImage(filter)));

        //ability slot
        Stack ability_stack = new Stack();
        ability_stack.add(new Image(empty_slot));

        //add everything
        if (loadout.length == 2) { ability_stack.add(new Image(AssetManager.getSpritesheet(loadout[1]))); } //if the user actually has an ability equipped
        loadout_inv.add();
        loadout_inv.add(class_name);
        loadout_inv.row();
        loadout_inv.add(weapon_stack);
        loadout_inv.add(class_preview).pad(10);
        loadout_inv.add(ability_stack);
        loadout_inv.setBounds(Global.SCREEN_WIDTH*4/8,0,Global.SCREEN_WIDTH*4/8,Global.SCREEN_HEIGHT);
        loadout_inv.pad(30);
    }

    public ArrayList<String> filterItems(String filter) { //chooose which items we want or dont want
        //populate the table with the contents of the user's inventory
        ArrayList<String> inv = new ArrayList<String>();
        for (String name : Global.user_data.getInventory()) {
            if (filter.contains(AssetManager.getItemDescrip(name).getItemType()) || filter.length() == 0) {
                inv.add(name);
            }
        }
        return inv;
    }

    //toggles
    public void show_inv() {
        this.current_tab = "";
        switch_page();
        this.inventory_grid.setVisible(true);
        this.tabs.setVisible(true);
        this.loadout_inv.setVisible(false);
        this.page.setVisible(true);
        this.inv_name.setVisible(true);
    }

    public void hide_inv() {
        this.inventory_grid.setVisible(false);
        this.tabs.setVisible(false);
        this.loadout_inv.setVisible(false);
        this.page.setVisible(false);
        this.item_info.setVisible(false);
        this.inv_name.setVisible(false);
    }

    public void show_loadout(String filter) { refresh_loadout(filter); this.loadout_inv.setVisible(true); }
    public void hide_loadout() { this.loadout_inv.setVisible(false); }
}

class InventoryScreen implements Screen { //inventory screen on main menu

    private Stage stage;
    private Inventory inv;

    public InventoryScreen() {

        this.stage = new Stage();
        this.inv = new Inventory(this.stage);

        //simple back button
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("back"))));
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                AudioPlayer.play_sound("button_click");
                Global.game.setScreen(Global.game.mainmenu_screen);
            }
        });

        //set background colour
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        Pixmap menuPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        menuPixmap.setColor(Color.valueOf("4c4c4c80"));
        menuPixmap.fill();
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(menuPixmap))));

        this.stage.addActor(rootTable);
        this.stage.addActor(backButton.left().pad(20));
    }

    @Override public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        inv.show_inv();
    }

    @Override public void hide() { }

    @Override public void dispose() {
        this.stage.clear();
        this.stage.dispose();
    }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class Options {

    private Stage stage;

    private Table table;
    private Label musicLabel;
    private Label soundLabel;
    private Label.LabelStyle labelStyle;
    private Slider musicSlider;
    private Slider soundSlider;

    public Options(Stage stage){
        this.stage = stage;
        this.table = new Table();
        this.table.setFillParent(true);

        this.labelStyle = new Label.LabelStyle();
        BitmapFont font = Global.skin.getFont("PixelFont_Small");
        this.labelStyle.font = font;
        this.labelStyle.fontColor = Color.valueOf("B5B5B5");

        //sliders
        this.musicLabel = new Label("Change Music Volume:",Global.skin);
        this.musicLabel.setStyle(this.labelStyle);
        this.musicSlider = new Slider(0f,1f,0.01f,false,Global.skin);
        musicSlider.setValue(AudioPlayer.getMusicVolume());

        this.soundLabel = new Label("Change Sound Volume:",Global.skin);
        this.soundLabel.setStyle(this.labelStyle);
        this.soundSlider = new Slider(0f,1f,0.01f,false,Global.skin);
        soundSlider.setValue(AudioPlayer.getSoundVolume());

        //add stuff to stage
        this.table.add(musicLabel).padRight(10f).padBottom(10f);
        this.table.add(soundLabel).padLeft(10f).padBottom(10f);
        this.table.row();
        this.table.add(musicSlider).expandX();
        this.table.add(soundSlider).expandX();
        this.table.center();

        this.stage.addActor(table);
    }

    //toggles
    public void hide_options(){
        AudioPlayer.setMusicVolume(this.musicSlider.getValue());
        AudioPlayer.setSoundVolume(this.soundSlider.getValue());
        table.setVisible(false);
    }
    public void show_options(){
        this.musicSlider.setValue(AudioPlayer.getMusicVolume());
        this.soundSlider.setValue(AudioPlayer.getSoundVolume());
        table.setVisible(true);
    }
}

class OptionsScreen implements Screen {

    private Stage stage;
    private Options options;

    public OptionsScreen() {

        this.stage = new Stage();
        this.options = new Options(this.stage);

        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(AssetManager.getUIImage("back"))));
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                AudioPlayer.play_sound("button_click");
                Global.game.setScreen(Global.game.mainmenu_screen);
            }
        });

        //set backgorund colour
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        Pixmap menuPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        menuPixmap.setColor(Color.valueOf("4c4c4c80"));
        menuPixmap.fill();
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(menuPixmap))));

        this.stage.addActor(rootTable);
        this.stage.addActor(backButton.left().pad(20));
    }

    @Override public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        options.show_options();
    }

    @Override public void hide() {
        options.hide_options();
    }

    @Override public void dispose() {
        this.stage.clear();
        this.stage.dispose();
    }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}


class LoginScreen implements Screen {

    private Stage stage;
    private TextField username_field;
    private TextField password_field;
    private Label invalid_creds;

    private Label warning_text;
    private TextField reg_username_field;
    private TextField reg_email_field;
    private TextField reg_password_field;
    private TextField confirm_password_field;

    public LoginScreen() {
        stage = new Stage();

        //LOGIN
        this.username_field = new TextField("",Global.skin);
        username_field.setMessageText("Username"); //displays when box is empty
        username_field.setMaxLength(16);
        username_field.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                //if enter is pressed, submit form
                if (c == '\r') { submit_creds(username_field.getText(),password_field.getText()); }
            }
        });

        this.password_field = new TextField("",Global.skin);
        password_field.setMessageText("Password");
        password_field.setPasswordMode(true);
        password_field.setPasswordCharacter('*');
        username_field.setMaxLength(30);
        password_field.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                //if enter is pressed, submit form
                if (c == '\r') { submit_creds(username_field.getText(),password_field.getText()); }
            }
        });

        TextButton login_button = new TextButton("Login!",Global.skin);
        login_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                submit_creds(username_field.getText(),password_field.getText()); //we also submit the form if the button is pressed
                AudioPlayer.play_sound("button_click");
            }
        });

        this.invalid_creds = new Label("",Global.skin);

        CheckBox remember_me = new CheckBox("Remember me",Global.skin);

        Table login_table = new Table(Global.skin);
        Pixmap loginPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        loginPixmap.setColor(Color.valueOf("db0a3180"));//hexcode with A - 80: 50% transparency
        loginPixmap.fill();
        login_table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(loginPixmap))));

        Label logInLabel = new Label("Log in!",Global.skin);
        Label.LabelStyle logInLabelStyle = new Label.LabelStyle();
        logInLabelStyle.font = Global.skin.getFont("PixelFont");
        logInLabel.setStyle(logInLabelStyle);

        //add stuff to table
        login_table.setBounds(0,0,Global.SCREEN_WIDTH/2,Global.SCREEN_HEIGHT);
        login_table.add(logInLabel).padBottom(10);
        login_table.row();
        login_table.add(invalid_creds).padBottom(10);
        login_table.row();
        login_table.add(username_field).padBottom(5);
        login_table.row();
        login_table.add(password_field).padTop(5).padBottom(5);
        login_table.row();
        login_table.add(remember_me).padTop(5);
        login_table.row();
        login_table.add(login_button).padTop(10);

        //REGISTER
        Table register_table = new Table(Global.skin);
        Pixmap registerPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        registerPixmap.setColor(Color.valueOf("084ca180"));//hexcode with A - 80: 50% transparency
        registerPixmap.fill();
        register_table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(registerPixmap))));

        Label registerLabel = new Label("Register!",Global.skin);
        Label.LabelStyle registerLabelStyle = new Label.LabelStyle();
        registerLabelStyle.font = Global.skin.getFont("PixelFont");
        registerLabel.setStyle(registerLabelStyle);

        register_table.setBounds(Global.SCREEN_WIDTH/2,0,Global.SCREEN_WIDTH/2,Global.SCREEN_HEIGHT);
        this.warning_text = new Label("",Global.skin);

        this.reg_username_field = new TextField("",Global.skin);
        reg_username_field.setMessageText("Username");
        reg_username_field.setMaxLength(16);

        this.reg_email_field = new TextField("",Global.skin);
        reg_email_field.setMessageText("Email");
        reg_email_field.setMaxLength(30);

        this.reg_password_field = new TextField("",Global.skin);
        reg_password_field.setMessageText("Password");
        reg_password_field.setPasswordMode(true);
        reg_password_field.setPasswordCharacter('*');
        reg_password_field.setMaxLength(30);

        this.confirm_password_field = new TextField("",Global.skin);
        confirm_password_field.setMessageText("Confirm password");
        confirm_password_field.setPasswordMode(true);
        confirm_password_field.setPasswordCharacter('*');
        confirm_password_field.setMaxLength(30);

        TextButton register_button = new TextButton("Register!",Global.skin);
        register_button.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event,float x,float y) {
                String warning = register(reg_username_field.getText(),reg_email_field.getText(),reg_password_field.getText(),confirm_password_field.getText());
                warning_text.setText(warning);
                AudioPlayer.play_sound("button_click");
            }
        });

        //add stuff to stage
        register_table.add(registerLabel).padBottom(10);
        register_table.row();
        register_table.add(warning_text).padBottom(10);
        register_table.row();
        register_table.add(reg_username_field).padBottom(5);
        register_table.row();
        register_table.add(reg_email_field).padTop(5).padBottom(5);
        register_table.row();
        register_table.add(reg_password_field).padTop(5).padBottom(5);
        register_table.row();
        register_table.add(confirm_password_field).padTop(5);
        register_table.row();
        register_table.add(register_button).padTop(10);

        stage.addActor(login_table);
        stage.addActor(register_table);
    }

    @Override public void render(float delta) {
        //AUTO LOGIN FOR NOW
        //submit_creds("daniel","password");
        stage.act(delta);
        stage.draw();
    }

    public void submit_creds(String username,String password) { //check to see if creds are valid
        username = username.replaceAll("[,$\\s]",""); //get rid of dangerous characters
        password = password.replaceAll("[,$\\s]","");
        if (username.equals("") || password.equals("")) {
            AudioPlayer.play_sound("error");
            creds_declined();
            return;
        } //dont send if field(s) are empty
        Global.server_socket.send_msg(MT.CHECKCREDS,username+","+password);
    }

    public void creds_accepted() { //we can go to the main menu if creds were acccepted
        Global.game.setScreen(Global.game.awaitauth_screen);
        Global.game.loadScreens();
    }

    public void creds_declined() {
        this.password_field.setText(""); //if the creds are wrong, clear the password field
        this.invalid_creds.setText("Invalid username or password");
        AudioPlayer.play_sound("error");
    }

    public String register(String username,String email,String password,String confirmpass) { //check to see if registraion fields are valid
        if (username.length()==0 || email.length()==0 || password.length()==0 || confirmpass.length()==0) { return "Empty field"; }
        if (!password.equals(confirmpass)) { return "Passwords don't match"; }
        if (!(3 <= username.length() && username.length() <= 16)) { return "Username must be between 3 and 16 characters"; }
        if (username.contains("![a-zA-Z0-9]")) { return "Username can only contain letters and numbers"; }
        if (!(8 <= password.length() && password.length() <= 30)) { return "Password must be between 8 and 30 characters"; }
        if (password.contains("![a-zA-Z0-9]")) { return "Password can only contain letters and numbers"; }

        //if all else is good, send message to server to check to see if username is taken
        Global.server_socket.send_msg(MT.REGISTER,username+","+password);
        return ""; //no warning :)
    }

    public void register_success() { //tell user that their account was succesffully registered
        this.warning_text.setText("Registration Successful!");
        this.reg_username_field.setText("");
        this.reg_email_field.setText("");
        this.reg_password_field.setText("");
        this.confirm_password_field.setText("");
    }
    public void register_failed() {
        this.warning_text.setText("Username taken");
        AudioPlayer.play_sound("error");
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void hide() { }

    @Override public void dispose() {
        this.stage.clear();
        this.stage.dispose();
    }

    @Override public void resize(int width,int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    public Stage getStage() { return this.stage; }
}

class ScreenUtils { //some extra helper methods

    public static void dimScreen(ShapeRenderer sr,float dimness) {
        sr.setColor(0,0,0,dimness);
        sr.rect(0,0,Global.SCREEN_WIDTH,Global.SCREEN_HEIGHT);
    }
}