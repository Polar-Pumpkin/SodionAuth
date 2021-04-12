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

package red.mohist.sodionauth.core.utils;

import com.google.gson.Gson;
import red.mohist.sodionauth.core.config.MainConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class Config {
    public static MainConfiguration instance;
    public static MainConfiguration fallback;
    public static String defaultLang;
    public static String serverId;
    public static MainConfiguration.DatabaseBean database;
    public static MainConfiguration.ApiBean api;
    public static MainConfiguration.BungeeBean bungee;
    public static MainConfiguration.DependenciesBean dependencies;
    public static MainConfiguration.SessionBean session;
    public static MainConfiguration.SpawnBean spawn;
    public static MainConfiguration.TeleportBean teleport;
    public static MainConfiguration.SecurityBean security;
    public static MainConfiguration.ProtectionBean protection;

    public static void init() throws IOException {
        Helper.instance.saveResource("config.json", false);
        File configFile = new File(Helper.getConfigPath("config.json"));
        FileInputStream fileReader = new FileInputStream(configFile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileReader, StandardCharsets.UTF_8);
        instance = new Gson().fromJson(inputStreamReader, MainConfiguration.class);
        inputStreamReader.close();
        fileReader.close();

        InputStream defaultFileReader = Helper.instance.getResource("config.json");
        InputStreamReader defaultInputStreamReader = new InputStreamReader(defaultFileReader, StandardCharsets.UTF_8);
        fallback = new Gson().fromJson(defaultInputStreamReader, MainConfiguration.class);
        defaultInputStreamReader.close();
        defaultFileReader.close();

        defaultLang = instance.defaultLang;
        serverId = instance.serverId;
        database = instance.database;
        api = instance.api;
        bungee = instance.bungee;
        dependencies = instance.dependencies;
        session = instance.session;
        spawn = instance.spawn;
        teleport = instance.teleport;
        security = instance.security;
        protection = instance.protection;
    }
}
