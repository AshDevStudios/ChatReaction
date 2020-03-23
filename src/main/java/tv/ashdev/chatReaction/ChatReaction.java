/*
 * MIT License
 *
 * Copyright (c) 2020 AshDev (Ashley Tonkin)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tv.ashdev.chatReaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The type Chat reaction.
 */
public final class ChatReaction extends JavaPlugin implements Listener {

  /**
   * The Prefix.
   */
  public String prefix = chatColor(getConfigString("prefix"));
  private Scheduler scheduler;

  @Override
  public void onEnable() {
    scheduler = new Scheduler(this);
    Bukkit.getServer().getPluginManager().registerEvents(this, this);
    messageReaction();
    getConfig().options().copyDefaults(true);
    saveDefaultConfig();
    reloadConfig();
  }

  /**
   * Message reaction.
   */
  public void messageReaction() {
    Bukkit.getScheduler().runTaskTimer(
        this, scheduler, 20 * getConfigInt("timeInSeconds"),
        20 * getConfigInt("timeBetweenWords"));
  }

  /**
   * On player chat.
   *
   * @param event the event
   */
  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();
    String msg = event.getMessage();
    if (scheduler.hasGuessed() || !msg.equals(scheduler.getRandString())) {
      return;
    }
    scheduler.setGuess(true);
    if (getConfigString("guessedCorrectly") == null) {
      Bukkit.broadcastMessage(prefix + " " + player.getDisplayName() + " has guessed correctly!");
    }
    Bukkit.getScheduler().runTask(this, () ->
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            getConfigString("reward").replace("{user}", player.getName())
        )
    );
    for (Player players : Bukkit.getOnlinePlayers()) {
      players.sendMessage(prefix + " " + chatColor(getConfigString("guessedCorrectly"))
          .replace("{name}", player.getDisplayName()));
    }

    player.sendTitle(
        chatColor(getConfigString("title.string")),
        chatColor(getConfigString("title.sub")),
        getConfigInt("title.fadeIn"),
        getConfigInt("title.stay"),
        getConfigInt("title.fadeOut"));
    event.setCancelled(true);
  }

  /**
   * Gets config string.
   *
   * @param string the string
   * @return the config string
   */
  public String getConfigString(String string) {
    if (getConfig().getString(string) == null) {
      return "";
    }
    return getConfig().getString(string);
  }

  /**
   * Gets config int.
   *
   * @param string the string
   * @return the config int
   */
  public Integer getConfigInt(String string) {
    return getConfig().getInt(string);
  }

  /**
   * Chat color string.
   *
   * @param msg the msg
   * @return the string
   */
  public String chatColor(String msg) {
    return ChatColor.translateAlternateColorCodes('&', msg);
  }

}
