/*
 * Copyright 2021 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.mohist.sodionauth.core.services;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.LastInfo;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.database.mappers.SqliteMapper;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.events.player.QuitEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.utils.Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SessionService {
    @Subscribe
    public void onBoot(BootEvent event) {
        Helper.getLogger().info("Initializing session service...");
        Service.database.mapper.initEntity(LastInfo.class);
    }

    @Subscribe
    public void onQuit(QuitEvent event) {
        AbstractPlayer player = event.getPlayer();
        Service.threadPool.startup.startTask(()->{
            if (!Service.auth.needCancelled(player)) {
                LastInfo lastInfo = LastInfo.getByUuid(player.getUniqueId());
                if(lastInfo==null) {
                    lastInfo = new LastInfo();
                }
                lastInfo.setUuid(event.getPlayer().getUniqueId())
                        .setInfo(new Gson().toJson(event.getPlayer().getPlayerInfo()))
                        .save();
            }
            player.teleport(Service.auth.default_location);
            Service.auth.logged_in.remove(player.getUniqueId());
        });
    }
}
