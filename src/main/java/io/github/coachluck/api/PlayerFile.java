/*
 *     File: PlayerFile.java
 *     Last Modified: 11/10/20, 1:26 AM
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

package io.github.coachluck.api;

import io.github.coachluck.EssentialServer;
import io.github.coachluck.utils.ChatUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerFile {

    private EssentialServer plugin = EssentialServer.getPlugin(EssentialServer.class);

    private YamlConfiguration playerData;

    private File playerDataFile;

    private UUID uuid;

    public PlayerFile(UUID playerUUID) {
        this.uuid = playerUUID;
        playerDataFile = new File(plugin.getDataFolder(),
                "data" + File.separator + playerUUID.toString() + ".yml");

        if(!playerDataFile.exists()) {
            playerData = initialSetup();
        }
        else {
            playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        }

    }

    private YamlConfiguration initialSetup() {
        YamlConfiguration newData = new YamlConfiguration();
        playerDataFile.mkdirs();

        try {
            playerDataFile.createNewFile();
        } catch (IOException e) {
            sendErrorMsg();
            e.printStackTrace();
        }

       return newData;
    }

    private void sendErrorMsg() {
        ChatUtils.logMsg("&cError creating file for " + uuid.toString());
    }

}
