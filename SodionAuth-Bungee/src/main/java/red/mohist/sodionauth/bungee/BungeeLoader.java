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

package red.mohist.sodionauth.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.reflections.Reflections;
import red.mohist.sodionauth.bungee.implementation.BungeePlayer;
import red.mohist.sodionauth.bungee.interfaces.BungeeAPIListener;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.events.TickEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.modules.LogProvider;
import red.mohist.sodionauth.core.modules.PlatformAdapter;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.Collection;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class BungeeLoader extends Plugin implements PlatformAdapter {
    public static BungeeLoader instance;
    public SodionAuthCore sodionAuthCore;

    @Override
    public void onEnable() {
        try {
            new Helper(getDataFolder().toString(), new LogProvider() {
                @Override
                public void info(String info) {
                    getLogger().info(info);
                }

                @Override
                public void info(String info, Exception exception) {
                    getLogger().info(info);
                    getLogger().info(exception.toString());
                }

                @Override
                public void warn(String info) {
                    getLogger().warning(info);
                }

                @Override
                public void warn(String info, Exception exception) {
                    getLogger().warning(info);
                    getLogger().warning(exception.toString());
                }
            });

            instance = this;
            Helper.getLogger().info("Hello, SodionAuth!");

            sodionAuthCore = new SodionAuthCore(this);

            registerListeners();

            getProxy().getScheduler().schedule(this, () -> {
                new TickEvent().post();
            }, 0, 50, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().warning("SodionAuth load fail.");
            getProxy().stop("SodionAuth load fail.");
        }
    }

    private void registerListeners() {
        int unavailableCount = 0;
        Set<Class<? extends BungeeAPIListener>> classes = new Reflections("red.mohist.sodionauth.bungee.listeners")
                .getSubTypesOf(BungeeAPIListener.class);
        for (Class<? extends BungeeAPIListener> clazz : classes) {
            BungeeAPIListener listener;
            try {
                listener = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                Helper.getLogger().warn(clazz.getName() + " is not available.");
                unavailableCount++;
                continue;
            }
            if (!listener.isAvailable()) {
                Helper.getLogger().warn(clazz.getName() + " is not available.");
                unavailableCount++;
                continue;
            }
            getProxy().getPluginManager().registerListener(this, listener);
        }
        if (unavailableCount > 0) {
            Helper.getLogger().warn("Warning: Some features in this plugin is not available on this version of bungee");
            Helper.getLogger().warn("If your encountered errors, do NOT report to SodionAuth.");
            Helper.getLogger().warn("Error count: " + unavailableCount);
        }
    }

    @Override
    public void onDisable() {
        new DownEvent().post();
    }

    @Override
    public void registerPluginMessageChannel(String channel) {
        getProxy().registerChannel(channel);
    }

    @Override
    public void shutdown() {
        getProxy().stop();
    }

    @Override
    public LocationInfo getSpawn(String world) {
        return new LocationInfo("", 0, 0, 0, 0, 0);
    }

    @Override
    public String getDefaultWorld() {
        return "";
    }

    @Override
    public void onLogin(AbstractPlayer player) {
        Helper.getLogger().info(player.getName() + " login in");
    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {

    }

    @Override
    public Collection<AbstractPlayer> getAllPlayer() {
        Collection<AbstractPlayer> allPlayers = new Vector<>();
        for (ProxiedPlayer onlinePlayer : getProxy().getPlayers()) {
            allPlayers.add(new BungeePlayer(onlinePlayer));
        }
        return allPlayers;
    }
}
