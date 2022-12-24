package com.yopal.secretsanta.utility;

import com.yopal.secretsanta.Secretsanta;
import com.yopal.secretsanta.enums.GameTypes;
import com.yopal.secretsanta.enums.UtilTypes;
import com.yopal.secretsanta.games.ElfOfTheMonth;
import com.yopal.secretsanta.games.FindThePresents;
import com.yopal.secretsanta.games.Parkour;
import com.yopal.secretsanta.games.SnowballBattle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class Countdown extends BukkitRunnable {
    private int countdownSeconds;
    private Secretsanta santa;

    private ElfOfTheMonth elfOfMonth;
    private Parkour parkour;
    private FindThePresents presents;
    private SnowballBattle battle;
    private GameTypes gameType;

    public Countdown(Secretsanta santa, int time, FindThePresents presents) {
        this.santa = santa;
        this.countdownSeconds = time;
        this.gameType = GameTypes.FINDPRESENTS;
        this.presents = presents;

    }

    public Countdown(Secretsanta santa, int time, SnowballBattle battle) {
        this.santa = santa;
        this.countdownSeconds = time;
        this.gameType = GameTypes.SNOWBALLBATTLE;
        this.battle = battle;
    }

    public Countdown(Secretsanta santa, int time, ElfOfTheMonth elfOfMonth) {
        this.santa = santa;
        this.countdownSeconds = time;
        this.gameType = GameTypes.ELFOFTHEMONTH;
        this.elfOfMonth = elfOfMonth;
    }

    public Countdown(Secretsanta santa, int time, Parkour parkour) {
        this.santa = santa;
        this.countdownSeconds = time;
        this.gameType = GameTypes.PARKOUR;
        this.parkour = parkour;
    }

    @Override
    public void run() {
        if (countdownSeconds == 0) {
            switch (gameType) {
                case FINDPRESENTS:
                    presents.finish();
                    break;
                case SNOWBALLBATTLE:
                    battle.finish();
                    break;
                case ELFOFTHEMONTH:
                    elfOfMonth.finish();
                    break;
                case PARKOUR:
                    parkour.finish();
                    break;
            }
            cancel();
            return;
        }

        if (countdownSeconds <= 10) {
            PlayerInteract.sendToAll(ChatColor.GREEN + "Game ends in " + countdownSeconds + " second" + (countdownSeconds == 1 ? "" : "s") + ".", UtilTypes.MESSAGE);
        }

        GameScoreboard.updateScoreboard(countdownSeconds);

        countdownSeconds--;

    }

    public void start() {
        runTaskTimer(santa, 0, 20);
    }


}
