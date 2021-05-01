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

package red.mohist.sodionauth.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import red.mohist.sodionauth.bukkit.implementation.BukkitPlayer;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.player.CanJoinEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;

public class ListenerAsyncPlayerPreLoginEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        AbstractPlayer abstractPlayer = new BukkitPlayer(
                event.getName(), event.getUniqueId(), event.getAddress());
        if (!SodionAuthCore.instance.isEnabled()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, abstractPlayer.getLang().errors.server);
            return;
        }

        if (Bukkit.getPlayerExact(event.getName()) != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, abstractPlayer.getLang().errors.loginExist);
            return;
        }

        if (!Service.auth.logged_in.containsKey(event.getUniqueId())) {
            Service.auth.logged_in.remove(event.getUniqueId());
        }

        CanJoinEvent canJoinEvent = new CanJoinEvent(abstractPlayer);
        if (!canJoinEvent.post()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, canJoinEvent.getMessage());
        }
    }

    @Override
    public void eventClass() {
        AsyncPlayerPreLoginEvent.class.getName();
    }
}
