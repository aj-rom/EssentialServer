/*
 *     File: Cooldown.java
 *     Last Modified: 6/28/20, 4:14 PM
 *     Project: EssentialServer
 *     Copyright (C) 2020 CoachL_ck
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.coachluck.utils;

import io.github.coachluck.EssentialServer;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {

    public enum CooldownType {
        TELEPORT,
        WARP
    }

    @Getter private final CooldownType cooldownType;
    @Getter private int timeRemaining;

    public Cooldown(CooldownType type) {
        EssentialServer plugin = EssentialServer.getPlugin(EssentialServer.class);
        cooldownType = type;
        switch (type) {
            case TELEPORT:
                timeRemaining = plugin.getConfig().getInt("teleport.cooldown-time");
                break;
            case WARP:
                timeRemaining = plugin.getWarpFile().getWarpData().getInt("cooldown");
                break;
            default: break;
        }

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                timeRemaining--;
                if (timeRemaining == 0) {
                    cancel();
                }
            }
        };
        task.runTaskTimerAsynchronously(plugin, 20, 20);
    }

    public static boolean checkCooldown(UUID uuid, HashMap<UUID, Cooldown> cooldowns) {
        if(cooldowns.get(uuid) != null) {
            Cooldown cooldown = cooldowns.get(uuid);
            if(cooldown.getTimeRemaining() != 0)
                return true;

            cooldowns.remove(uuid);
            return false;
        }

        cooldowns.put(uuid, new Cooldown(Cooldown.CooldownType.WARP));
        return false;
    }
}
