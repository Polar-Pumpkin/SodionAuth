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

package red.mohist.sodionauth.sponge;

import com.eloli.sodioncore.file.BaseFileService;
import com.eloli.sodioncore.orm.AbstractSodionCore;
import com.eloli.sodioncore.sponge.SodionCore;
import com.eloli.sodioncore.sponge.SpongeLogger;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.events.player.ClientMessageEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.FoodInfo;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.modules.PlatformAdapter;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.sponge.implementation.SpongePlayer;
import red.mohist.sodionauth.sponge.interfaces.SpongeAPIListener;
import red.mohist.sodionauth.sponge.listeners.*;

import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "sodionauth",
        name = "SodionAuth",
        description = "A new generation of authentication plugin for Minecraft",
        version = "2.0-Alpha.3",
        dependencies = {
                @Dependency(id = "sodioncore")
        }
)
public class SpongeLoader implements PlatformAdapter {
    public static SpongeLoader instance;
    public SodionAuthCore sodionAuthCore;
    @Inject
    private Logger logger;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        try {
            instance = this;
            new Helper(new BaseFileService(privateConfigDir.toString()),
                    new SpongeLogger(logger),
                    ((SodionCore) getSodionCore()).getDependencyManager(this));

            Helper.getLogger().info("Hello, SodionAuth!");

            sodionAuthCore = new SodionAuthCore(this);

            {
                int unavailableCount = 0;
                List<Class<? extends SpongeAPIListener>> classes = new ArrayList<>();

                classes.add(AuthListener.class);
                classes.add(ChangeInventoryListener.class);
                classes.add(ChatListener.class);
                classes.add(ClickInventoryListener.class);
                classes.add(DamageEntityListener.class);
                classes.add(DisconnectListener.class);
                classes.add(DropItemListener.class);
                classes.add(InteractBlockListener.class);
                classes.add(InteractEntityListener.class);
                classes.add(InteractInventoryListener.class);
                classes.add(InteractItemListener.class);
                classes.add(JoinListener.class);
                classes.add(MoveEntityListener.class);
                classes.add(PickupListener.class);
                classes.add(SendCommandListener.class);
                classes.add(StartListener.class);

                for (Class<? extends SpongeAPIListener> clazz : classes) {
                    SpongeAPIListener listener;
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
                    Sponge.getEventManager().registerListeners(this, listener);
                }
                if (unavailableCount > 0) {
                    Helper.getLogger().warn("Warning: Some features in this plugin is not available on this version of sponge");
                    Helper.getLogger().warn("If your encountered errors, do NOT report to SodionAuth.");
                    Helper.getLogger().warn("Error count: " + unavailableCount);
                }
            }
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        //new TickEvent().post();
                    })
                    .intervalTicks(1).submit(this);

        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SodionAuth load fail.");
            Sponge.getServer().shutdown(Text.of("SodionAuth load fail."));
        }
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        new DownEvent().post();
    }

    @Override
    public void registerPluginMessageChannel(String channel) {
        Sponge.getChannelRegistrar().getOrCreateRaw(this, channel)
                .addListener(Platform.Type.SERVER, (data, connection, side) -> {
                    if (connection instanceof PlayerConnection) {
                        new ClientMessageEvent(
                                channel,
                                data.readBytes(data.available()),
                                new SpongePlayer(((PlayerConnection) connection).getPlayer())
                        ).post();
                    }
                });
    }

    @Override
    public void shutdown() {
        Sponge.getServer().shutdown(Text.of("SodionAuth load fail."));
    }

    @Override
    public AbstractSodionCore getSodionCore() {
        return (AbstractSodionCore) Sponge.getPluginManager().getPlugin("sodioncore").get().getInstance().get();
    }


    @Override
    public Collection<AbstractPlayer> getAllPlayer() {
        Collection<AbstractPlayer> allPlayers = new Vector<>();
        for (Player onlinePlayer : Sponge.getServer().getOnlinePlayers()) {
            allPlayers.add(new SpongePlayer(onlinePlayer));
        }
        return allPlayers;
    }

    @Override
    public LocationInfo getSpawn(String worldName) {
        Optional<Location<World>> world = Sponge.getServer().getWorld(worldName).map(World::getSpawnLocation);
        return new LocationInfo(worldName, world.get().getX(), world.get().getY(), world.get().getZ(), 0, 0);
    }

    @Override
    public String getDefaultWorld() {
        return Sponge.getServer().getDefaultWorldName();
    }

    @Override
    public void onLogin(AbstractPlayer player) {

    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {
        if (player instanceof SpongePlayer) {
            Runnable run = () -> {
                Player handle = ((SpongePlayer) player).handle;
                handle.respawnPlayer();
                player.setEffects(new LinkedList<>());
                player.setHealth(20);
                player.setRemainingAir(0);
                player.setMaxHealth(20);
                player.setFood(new FoodInfo());
                if (Config.security.spectatorLogin) {
                    player.setGameMode(3);
                } else {
                    player.setGameMode(Config.security.defaultGamemode);
                }
            };
            if (Sponge.getServer().isMainThread()) {
                run.run();
            } else {
                Sponge.getScheduler()
                        .createTaskBuilder()
                        .execute(run)
                        .submit(this);
            }
        }
    }
}
