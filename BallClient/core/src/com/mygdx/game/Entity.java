/* Shinobi Network
 ______   __   __   ______  __   ______  __  __
/\  ___\ /\ "-.\ \ /\__  _\/\ \ /\__  _\/\ \_\ \
\ \  __\ \ \ \-.  \\/_/\ \/\ \ \\/_/\ \/\ \____ \
 \ \_____\\ \_\\"\_\  \ \_\ \ \_\  \ \_\ \/\_____\
  \/_____/ \/_/ \/_/   \/_/  \/_/   \/_/  \/_____/

    Entity on the client side is just a texture and a position
    The server tells us what to draw and we draw it
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Entity {

    private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer,Entity>(); //used so we know which piece of data belongs to which entity

    //basic entity vars
    private String entity_type;
    private String name;
    private float x;
    private float y;
    private float rotation;

    //used for intepolation
    private float old_x;
    private float old_y;
    private float old_rotation;

    //graphical vars
    private Animation<TextureRegion> animation;
    private static final float animation_duration = 0.25f;//used for animation
    private float frameTime;

    private Entity(String name) { //THE ONLY TIME CLIENT IS ALLOWED TO CREATE ENTITIES IS IF THE SERVER SAYS SO
        this.name = name;
        init_animation(name);

        this.x = 0; this.y = 0; this.rotation = 0;
        this.old_x = 0; this.old_y = 0; this.old_rotation = 0;
    }

    public void init_animation(String name) {
        TextureRegion[] frames = Entity.createAnimation(name);
        this.animation = new Animation<TextureRegion>(animation_duration,frames);
    }

    private static TextureRegion[] createAnimation(String texture_path) { //create new animation from texture
        assert (AssetManager.animation_lib.containsKey(texture_path)): "Texture ("+texture_path+") hasn't been loaded yet.";
        Texture spritesheet = AssetManager.getSpritesheet(texture_path);

        int NUM_COLS = spritesheet.getWidth() / Global.SPRITESIZE; //get dimnsions of spritesheet
        int NUM_ROWS = spritesheet.getHeight() / Global.SPRITESIZE;

        TextureRegion[][] raw = TextureRegion.split(spritesheet, Global.SPRITESIZE, Global.SPRITESIZE); //split into individau; frames
        TextureRegion[] frames = new TextureRegion[NUM_COLS * NUM_ROWS];
        //push all raw data into a 1D array
        int index = 0;
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                frames[index++] = raw[i][j];
            }
        }
        return frames;
    }

    public void update_pos(float new_x, float new_y, float new_rotation) { //set new postion and push current pos to old pos
        this.old_x = this.x; this.old_y = this.y; this.old_rotation = this.rotation;
        this.x = new_x; this.y = new_y; this.rotation = new_rotation;
    }

    public static void update_entity(String data) { //if entity doesnt exist, we create a new one
        //format: ID,texture_path,x,y
        //parse data
        String[] parsed = data.split(",");
        String entity_type = parsed[0];
        int id = Integer.parseInt(parsed[1]);
        String name = parsed[2];
        float x = Float.parseFloat(parsed[3]);
        float y = Float.parseFloat(parsed[4]);
        float rot = Float.parseFloat(parsed[5]);

        Entity entity;
        if (!Entity.entity_library.containsKey(id)) { //if entity doesnt exist yet, create it
            //This block creates and integrates the entity
            Entity newEntity = new Entity(name);
            entity_library.put(id,newEntity);
            entity = newEntity;

        } else { entity = Entity.entity_library.get(id); } //otherwise just grab the entity instance

        if (!name.equals(entity.getName())) { //if the name changed, that means the animation changed
            entity.init_animation(name);
            entity.setName(name);
        }

        //apply all the updates
        entity.entity_type = entity_type;
        entity.update_pos(x,y,rot);

        //update health bars
        if (ET.valueOf(entity.entity_type.toUpperCase()) == ET.PLAYER) {
            HealthTracker ht;
            if (!HealthTracker.getHealthBarList().containsKey(id)) { //if an hp bar hasnt been created for this char yet
                ht = new HealthTracker(id);
            } else { ht = HealthTracker.getHealthBarList().get(id); } //otherwise just grab the instance
            assert (ht != null) : "Health bar not found";
            ht.setPos(entity.getX() - ht.getBarWidth()/2, entity.getY() + HealthTracker.y_offset); //bind the pos of ht to entity
        }
    }

    public static void kill_entity(int id) {
        //assert (Entity.entity_library.containsKey(id)): "The entity you are trying to remove doesn't exist in master list";
        if (!Entity.entity_library.containsKey(id)) { return; } //if the entity we are trying to remove doesnt exist, ignore it
        Entity.entity_library.remove(id);
    }

    public static void drawAll(SpriteBatch batch) {
        for (Entity e : Entity.entity_library.values()) {
            TextureRegion tex = e.getFrame();

            float rot = e.getRotation();
            if (rot < 0) rot += MathUtils.PI2; //get rid of negative angles

            if (e.getET().equals(ET.PLAYER.toString())) {
                //if mouse is in 2nd or 3rd quadrant, face left
                if (MathUtils.PI/2 < rot && rot < MathUtils.PI*3/2 && !tex.isFlipX()) { tex.flip(true, false); }
                //if mouse is in 4th or 1st quadrant, face right
                else if ((MathUtils.PI*3/2 < rot || rot < MathUtils.PI/2) && tex.isFlipX()) { tex.flip(true, false); }
                batch.draw(tex,e.getX()-Global.SPRITESIZE/2,e.getY()-Global.SPRITESIZE/2); //TODO, add scaling and rotation ALSO, DONT ASSUME SPRITESIZE!!!!!

            } else if (e.getET().equals(ET.PROJECTILE.toString())) {
                batch.draw(tex,e.getX()-Global.SPRITESIZE/2,e.getY()-Global.SPRITESIZE/2,Global.SPRITESIZE/2,Global.SPRITESIZE/2,Global.SPRITESIZE,Global.SPRITESIZE,1,1,e.getRotation()* MathUtils.radiansToDegrees);

            } else if (e.getET().equals(ET.WEAPON.toString())) {
                //if mouse is in 2nd or 3rd quadrant, face left
                if (MathUtils.PI/2 < rot && rot < MathUtils.PI*3/2 && !tex.isFlipY()) { tex.flip(false, true); }
                //if mouse is in 4th or 1st quadrant, face right
                else if ((MathUtils.PI*3/2 < rot || rot < MathUtils.PI/2) && tex.isFlipY()) { tex.flip(false, true); }
                batch.draw(tex,e.getX()-Global.SPRITESIZE/2,e.getY()-Global.SPRITESIZE/2,Global.SPRITESIZE/2,Global.SPRITESIZE/2,Global.SPRITESIZE,Global.SPRITESIZE,Global.WEAPONSCALE,Global.WEAPONSCALE,e.getRotation()* MathUtils.radiansToDegrees);

            }
        }
    }


    //static stuff
    public static void clearEntityLib() { Entity.entity_library.clear(); } //wipe all entites if user leaves the game
    public static Entity getEntity(int id) { //retrieve an entity instance from the master lib
        assert (Entity.entity_library.containsKey(id)): "Entity not found";
        return Entity.entity_library.get(id);
    }

    //simple getters
    public String getName() { return this.name; }
    public String getET() { return this.entity_type; }
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getRotation() { return this.rotation; }
    public float getOldX() { return this.old_x; }
    public float getOldY() { return this.old_y; }

    //setters
    public void setName(String name) { this.name = name; }

    //animation stuff
    public TextureRegion getFrame() { return this.animation.getKeyFrame(this.frameTime,true); }
    public void stepFrame(float deltaTime) { this.frameTime += deltaTime; } //possibly combine with getFrame
    public void resetFrame() { this.frameTime = 0; }
    public static void stepFrameAll(float deltaTime) { //step frame for all entities
        for (Entity e : Entity.entity_library.values()) {
            if (e.getOldX() == e.getX() && e.getOldY() == e.getY()) { e.resetFrame(); continue;} //ife entity hasnt moved, dont step animation
            e.stepFrame(deltaTime);
        }
    }


}
