package com.yopal.secretsanta.managers;

import com.xxmicloxx.NoteBlockAPI.model.FadeType;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import com.yopal.secretsanta.Secretsanta;
import org.bukkit.entity.Player;

import java.io.File;

public class MusicManager {
    private static Playlist playlist;

    public static void loadPlaylist(Secretsanta santa) {

        Song song1 = NBSDecoder.parse(new File(santa.getDataFolder(), "alliwantforchristmasisyou.nbs"));
        Song song2 = NBSDecoder.parse(new File(santa.getDataFolder(),"FrostyTheSnowman.nbs"));
        Song song3 = NBSDecoder.parse(new File(santa.getDataFolder(),"It's-Beginning-To-Look-A-Lot-Like-Christmas.nbs"));
        Song song4 = NBSDecoder.parse(new File(santa.getDataFolder(),"JingleBellRock.nbs"));
        Song song5 = NBSDecoder.parse(new File(santa.getDataFolder(),"jinglebells.nbs"));
        Song song6 = NBSDecoder.parse(new File(santa.getDataFolder(),"lastchristmas.nbs"));
        Song song7 = NBSDecoder.parse(new File(santa.getDataFolder(),"NightmareBeforeChristmas.nbs"));
        Song song8 = NBSDecoder.parse(new File(santa.getDataFolder(),"NutcrackerDance.nbs"));
        Song song9 = NBSDecoder.parse(new File(santa.getDataFolder(),"NutcrackerRussian.nbs"));
        Song song10 = NBSDecoder.parse(new File(santa.getDataFolder(),"silent-night.nbs"));
        Song song11 = NBSDecoder.parse(new File(santa.getDataFolder(),"TwelveDaysofChristmas.nbs"));
        Song song12 = NBSDecoder.parse(new File(santa.getDataFolder(),"NutcrackerWaltz.nbs"));
        MusicManager.playlist = new Playlist(song1, song2, song3, song4, song5, song6, song7, song8, song9, song10, song11, song12);

    }

    public static void playSong(Player player) {
        if (ConfigManager.getAllPlayers().contains(player.getUniqueId())) {
            return;
        }

        RadioSongPlayer rsp = new RadioSongPlayer(MusicManager.playlist);
        rsp.addPlayer(player);
        rsp.setRandom(true);
        rsp.setLoop(true);
        rsp.setPlaying(true);
    }
}
