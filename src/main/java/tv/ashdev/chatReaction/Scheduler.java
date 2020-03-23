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

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The type Scheduler.
 */
public class Scheduler implements Runnable {

  private final ChatReaction plugin;
  private String randString;
  private boolean guessed;

  /**
   * Instantiates a new Scheduler.
   *
   * @param plugin the plugin
   */
  public Scheduler(final ChatReaction plugin) {
    this.plugin = plugin;
  }

  @Override
  public void run() {
    guessed = false;
    randString = RandomStringUtils.random(
        plugin.getConfig().getInt("randomString.numberOfChars"),
        plugin.getConfig().getBoolean("randomString.letters"),
        plugin.getConfig().getBoolean("randomString.numbers")
    );
    if (plugin.getConfigString("randomString.string1") == null) {
      Bukkit.getServer().broadcastMessage(plugin.prefix + " " + randString);
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
      int start = plugin.getConfigString("randomString.string1").indexOf('{');
      int finish = plugin.getConfigString("randomString.string1").indexOf('}');

      JSONMessage.create(
          plugin.prefix + plugin.getConfigString("randomString.string1").substring(0, start) + " ")
          .then(plugin.getConfigString("randomString.string1").substring(start, finish + 1)
              .replace("{tooltip}", plugin.getConfigString("randomString.tooltip")))
          .tooltip(randString).then(" " + plugin.getConfigString("randomString.string2")).send(player);
    }

    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if (guessed) {
        return;
      }
      if (plugin.getConfigString("noOneGuessed") == null) {
        Bukkit.getServer().broadcastMessage(plugin.prefix + " Sadly no one guessed correctly");
      }
      for (Player player : Bukkit.getOnlinePlayers()) {
        player.sendMessage(plugin.chatColor(plugin.prefix + " " + plugin.getConfigString("noOneGuessed")));
      }
      guessed = true;
    }, 20 * plugin.getConfigInt("timeInSeconds"));
  }

  /**
   * Gets rand string.
   *
   * @return the rand string
   */
  public String getRandString() {
    return randString;
  }

  /**
   * Has guessed boolean.
   *
   * @return the boolean
   */
  public boolean hasGuessed() {
    return guessed;
  }

  /**
   * Sets guess.
   *
   * @param guessed the guessed
   */
  public void setGuess(final boolean guessed) {
    this.guessed = guessed;
  }

}
