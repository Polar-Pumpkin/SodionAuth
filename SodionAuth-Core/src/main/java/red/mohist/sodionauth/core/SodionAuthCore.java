/*
 * Copyright 2020 Mohist-Community
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

package red.mohist.sodionauth.core;

import com.google.gson.Gson;
import jdk.internal.loader.ClassLoaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.knownspace.minitask.ITask;
import org.knownspace.minitask.ITaskFactory;
import org.knownspace.minitask.TaskFactory;
import org.knownspace.minitask.locks.UniqueFlag;
import org.knownspace.minitask.locks.Unlocker;
import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.dependency.DependencyManager;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.exception.AuthenticatedException;
import red.mohist.sodionauth.core.interfaces.PlatformAdapter;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.modules.PlayerInfo;
import red.mohist.sodionauth.core.protection.SecuritySystems;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.LoginTicker;
import red.mohist.sodionauth.core.utils.ResultTypeUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SodionAuthCore {

    public static SodionAuthCore instance;
    private final AtomicBoolean isEnabled = new AtomicBoolean(false);
    public PlatformAdapter api;
    public LocationInfo default_location;
    public LocationInfo spawn_location;
    private Connection connection;

    public SodionAuthCore(PlatformAdapter platformAdapter) {
        try {
            Helper.getLogger().info("Initializing basic services...");
            {
                // WARN You are not permitted to interfere any protection that prevents loading in CatServer
                String a0 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
                try {
                    try {
                        Class.forName("catserver.server.CatServer");
                    } catch (Throwable e) {
                        // System.err.println(e);
                        try {
                            ClassLoader.getSystemClassLoader().loadClass("catserver.server.CatServer");
                        } catch (Throwable e1) {
                            // System.err.println(e1);
                            try {
                                ClassLoaders.appClassLoader().loadClass("catserver.server.CatServer");
                            } catch (Throwable e2) {
                                // System.err.println(e2);
                                ClassLoader classLoader = this.getClass().getClassLoader();
                                while (classLoader != null) {
                                    try {
                                        classLoader.loadClass("catserver.server.CatServer");
                                        break;
                                    } catch (Throwable e3) {
                                        // System.err.println(e3);
                                        classLoader = classLoader.getParent();
                                    }
                                }
                                throw new ClassNotFoundException();
                            }
                        }
                    }
                    // WARN You are not permitted to interfere any protection that prevents loading in CatServer
                    String a1 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
                    System.out.println("======================== [ Security Check ] ========================");
                    System.out.println("万分抱歉，本插件未对CatServer作任何测试，尚不能保证能正常使用。");
                    System.out.println("出于保护您的服务器安全的考虑，SodionAuth已经停止载入。");
                    System.out.println("如果你需要使用本插件，建议使用Mohist服务端，我们完成了良好的适配。");
                    System.out.println("We are sorry that this plugin is not tested against CatServer at all");
                    System.out.println("It can cause some major issues preventing the plugin to work.");
                    System.out.println("Because of security issues, SodionAuth aborted starting the server.");
                    System.out.println("If you want to use it, switch to Mohist which is more stable.");
                    System.out.println("==================================================================");
                    new Thread(() -> {
                        System.exit(-1);
                        // WARN You are not permitted to interfere any protection that prevents loading in CatServer
                        String a2 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
                    }).start();
                    Thread.sleep(1000);
                    new Thread(() -> {
                        Runtime.getRuntime().halt(-1);
                        // WARN You are not permitted to interfere any protection that prevents loading in CatServer
                        String a3 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
                    }).start();
                    return;
                    // WARN You are not permitted to interfere any protection that prevents loading in CatServer
                } catch (ClassNotFoundException ignored) {
                    // WARN You are not permitted to interfere any protection that prevents loading in CatServer
                    String a5 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
                } catch (Exception e) {
                    // WARN You are not permitted to interfere any protection that prevents loading in CatServer
                    String a6 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
                    e.printStackTrace();
                }
                // WARN You are not permitted to interfere any protection that prevents loading in CatServer
                String a7 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
            }

            DependencyManager.checkDependencyMaven("org.mindrot", "jbcrypt", "0.4", () -> {
                try {
                    Class.forName("org.mindrot.jbcrypt.BCrypt");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("org.reflections", "reflections", "0.9.12", () -> {
                try {
                    Class.forName("org.reflections.Reflections");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("org.apache.httpcomponents", "fluent-hc", "4.5.11", () -> {
                try {
                    Class.forName("org.reflections.Reflections");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("com.maxmind.geoip2", "geoip2", "2.14.0", () -> {
                try {
                    Class.forName("com.maxmind.geoip2.DatabaseReader");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("com.google.code.findbugs", "jsr305", "3.0.2", () -> {
                try {
                    Class.forName("javax.annotation.Nullable");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });

            instance = this;
            api = platformAdapter;
            isEnabled.set(true);
            DependencyManager.checkForSQLite();

            Helper.getLogger().info("Loading configurations...");
            loadConfig();
            SecuritySystems.reloadConfig();
            LoginTicker.register();

            Helper.getLogger().info("Initializing session storage...");
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + Helper.getConfigPath("SodionAuth.db"));
                if (!connection.getMetaData().getTables(null, null, "last_info", new String[]{"TABLE"}).next()) {
                    PreparedStatement pps = connection.prepareStatement("CREATE TABLE last_info (uuid NOT NULL,info,PRIMARY KEY (uuid));");
                    pps.executeUpdate();
                }
                if (!connection.getMetaData().getTables(null, null, "sessions", new String[]{"TABLE"}).next()) {
                    PreparedStatement pps = connection.prepareStatement("CREATE TABLE sessions (uuid NOT NULL,ip,time,PRIMARY KEY (uuid));");
                    pps.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Helper.getLogger().info("Check for existing players...");
            for (AbstractPlayer abstractPlayer : api.getAllPlayer()) {
                canJoinAsync(abstractPlayer).then(result -> {
                    if (result != null)
                        abstractPlayer.kick(result);
                });
                onJoin(abstractPlayer);
            }

            Helper.getLogger().info("Done");
        } catch (Throwable throwable) {
            isEnabled.set(false);
            throw throwable;
        }
    }

    public void loadFail() {
        api.shutdown();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isEnabled() {
        return isEnabled.get();
    }

    public void onDisable() {
        isEnabled.set(false);
        Helper.getLogger().info("Removing existing players...");
        for (AbstractPlayer abstractPlayer : api.getAllPlayer()) {
            onQuit(abstractPlayer);
        }
        Helper.getLogger().info("Stopping services...");
        Helper.getLogger().info("Stopping session storage...");
        try {
            connection.close();
        } catch (Throwable ignored) {
        }
        instance = null;
    }

    private void loadConfig() {
        spawn_location = api.getSpawn(api.getDefaultWorld());
        default_location = new LocationInfo(
                Config.spawn.getWorld(api.getDefaultWorld()),
                Config.spawn.getX(spawn_location.x),
                Config.spawn.getY(spawn_location.y),
                Config.spawn.getZ(spawn_location.z),
                Config.spawn.getYaw(spawn_location.yaw),
                Config.spawn.getPitch(spawn_location.pitch)
        );
    }

    public boolean needCancelled(AbstractPlayer player) {
        return Service.auth.needCancelled(player);
    }

    @Deprecated
    public void login(AbstractPlayer player) throws AuthenticatedException {
        try {
            loginAsync(player).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AuthenticatedException) {
                throw (AuthenticatedException) cause;
            }
            cause.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void realLogin(AbstractPlayer player) throws AuthenticatedException {
        if (Service.auth.logged_in.getOrDefault(player.getUniqueId(), StatusType.NEED_LOGIN)
                .equals(StatusType.LOGGED_IN)) {
            //already login
            return;
        }
        Service.auth.logged_in.put(player.getUniqueId(), StatusType.LOGGED_IN);
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM last_info WHERE uuid=? LIMIT 1;");
            pps.setString(1, player.getUniqueId().toString());
            ResultSet rs = pps.executeQuery();
            if (!rs.next()) {
                player.setPlayerInfo(new PlayerInfo());
            } else {
                player.setPlayerInfo(new Gson().fromJson(rs.getString("info"), PlayerInfo.class));
            }


        } catch (Throwable e) {
            player.setGameMode(Config.security.getDefaultGamemode());
            e.printStackTrace();
        }

        try {
            PreparedStatement pps = connection.prepareStatement("DELETE FROM sessions WHERE uuid = ?;");
            pps.setString(1, player.getUniqueId().toString());
            pps.executeUpdate();

            pps = connection.prepareStatement("INSERT INTO sessions(uuid, ip, time) VALUES (?, ?, ?);");
            pps.setString(1, player.getUniqueId().toString());
            pps.setString(2, player.getAddress().getHostAddress());
            pps.setInt(3, (int) (System.currentTimeMillis() / 1000));
            pps.executeUpdate();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        api.onLogin(player);
        Helper.getLogger().warn("Logging in " + player.getUniqueId());
        player.sendMessage(player.getLang().getSuccess());
    }

    public ITask<Void> loginAsync(AbstractPlayer player) {
        return Service.async.dbUniqueFlag.lock().then(() -> {
            try (Unlocker<UniqueFlag> unlocker = new Unlocker<>(Service.async.dbUniqueFlag)) {
                realLogin(player);
            } catch (Exception e) {
                throw e;
            }
        });
    }

    public void onQuit(AbstractPlayer player) {
        LocationInfo leave_location = player.getLocation();
        Service.async.dbUniqueFlag.lock().then(() -> {
            try (Unlocker<UniqueFlag> unlocker = new Unlocker<>(Service.async.dbUniqueFlag)) {
                if (!needCancelled(player)) {
                    try {
                        PreparedStatement pps = connection.prepareStatement("DELETE FROM last_info WHERE uuid = ?;");
                        pps.setString(1, player.getUniqueId().toString());
                        pps.executeUpdate();
                        pps = connection.prepareStatement("INSERT INTO last_info(uuid, info) VALUES (?, ?);");
                        pps.setString(1, player.getUniqueId().toString());
                        pps.setString(2, new Gson().toJson(player.getPlayerInfo()));
                        pps.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Helper.getLogger().warn("Fail to save location.");
                    }
                }
                player.teleport(default_location);
                Service.auth.logged_in.remove(player.getUniqueId());
            } catch (Exception ignore) {
            }
        });
    }

    public String canLogin(AbstractPlayer player) {
        return SecuritySystems.canJoin(player);
    }


    @Deprecated
    public CompletableFuture<String> canJoin(AbstractPlayer player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        Service.async.executor.execute(() -> {
            Service.auth.logged_in.put(player.getUniqueId(), StatusType.HANDLE);
            future.complete(canJoinHandle(player));
        });
        return future;
    }

    public ITask<String> canJoinAsync(AbstractPlayer player) {
        return Service.async.startup.startTask(() -> {
            Service.auth.logged_in.put(player.getUniqueId(), StatusType.HANDLE);
            return canJoinHandle(player);
        });
    }

    public String canJoinHandle(AbstractPlayer player) {
        if (Service.auth.logged_in.containsKey(player.getUniqueId())
                && Service.auth.logged_in.get(player.getUniqueId()) != StatusType.HANDLE) {
            return null;
        }
        Service.auth.logged_in.put(player.getUniqueId(), StatusType.HANDLE);

        ResultType resultType = AuthBackendSystems.getCurrentSystem()
                .join(player)
                .shouldLogin(false);
        switch (resultType) {
            case OK:
                Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_LOGIN);
                return null;
            case ERROR_NAME:
                return player.getLang().getErrors().getNameIncorrect(
                        resultType.getInheritedObject());
            case NO_USER:
                if (Config.api.getAllowRegister()) {
                    Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                    return null;
                } else {
                    return player.getLang().getErrors().getNoUser();
                }
            case UNKNOWN:
                return player.getLang().getErrors().getUnknown(resultType.getInheritedObject());
        }
        return player.getLang().getErrors().getUnknown();
    }

    public void onJoin(AbstractPlayer abstractPlayer) {
        api.sendBlankInventoryPacket(abstractPlayer);
        if (Config.teleport.getTpSpawnBeforeLogin()) {
            abstractPlayer.teleport(default_location);
        }
        if (Config.security.getSpectatorLogin()) {
            abstractPlayer.setGameMode(3);
        }
        LoginTicker.add(abstractPlayer);
        Service.async.dbUniqueFlag.lock().then(() -> {
            try (Unlocker<UniqueFlag> unlocker = new Unlocker<>(Service.async.dbUniqueFlag)) {
                if (Config.session.getEnable()) {
                    PreparedStatement pps = connection.prepareStatement("SELECT * FROM sessions WHERE uuid=? AND ip=? AND time>? LIMIT 1;");
                    pps.setString(1, abstractPlayer.getUniqueId().toString());
                    pps.setString(2, abstractPlayer.getAddress().getHostAddress());
                    pps.setInt(3, (int) (System.currentTimeMillis() / 1000 - Config.session.getTimeout()));
                    ResultSet rs = pps.executeQuery();
                    if (rs.next()) {
                        abstractPlayer.sendMessage(abstractPlayer.getLang().getSession());
                        realLogin(abstractPlayer);
                    }
                }
            } catch (Throwable e) {
                Helper.getLogger().warn("Fail use session.");
                e.printStackTrace();
            }
        });
    }
}