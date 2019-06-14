package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.HashMap;

public class AudioPlayer {

    private static HashMap<String, Sound> sound_lib = new HashMap<String, Sound>();
    private static ArrayList<String> music_lib = new ArrayList<String>();

    private static Music currentMusic;

    private static float music_volume = 0.25f;
    private static float sound_volume = 1f;

    public static void play_music() {
        String music_path = AudioPlayer.music_lib.get(Global.rnd.nextInt(AudioPlayer.music_lib.size()));
        AudioPlayer.currentMusic = Gdx.audio.newMusic(Gdx.files.internal(music_path));
        currentMusic.setVolume(music_volume);
        currentMusic.setLooping(true);
        currentMusic.play();
    }

    public static void play_sound(String sound_name) {
        assert (AudioPlayer.sound_lib.containsKey(sound_name)): "Sound not found: "+sound_name;
        AudioPlayer.sound_lib.get(sound_name).play(sound_volume);
    }

    public static void load_sounds(String dir) {
        FileHandle root = new FileHandle(dir);
        for (FileHandle file : root.list()) {
            String filename = file.name().replaceAll(".ogg", "");
            AudioPlayer.sound_lib.put(filename, Gdx.audio.newSound(Gdx.files.internal(dir + file.name())));
        }
    }
    public static void load_music(String dir) {
        FileHandle root = new FileHandle(dir);
        for (FileHandle file : root.list()) {
            AudioPlayer.music_lib.add(dir+file.name());
        }
    }

    public static void setSoundVolume(float sound_volume) { AudioPlayer.sound_volume = sound_volume; }
    public static void setMusicVolume(float music_volume) { AudioPlayer.music_volume = music_volume; }
    public static float getSoundVolume() { return AudioPlayer.sound_volume; }
    public static float getMusicVolume() { return AudioPlayer.music_volume; }
}
