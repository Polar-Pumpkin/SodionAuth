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

import org.teasoft.bee.osql.Suid;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.HoneyFactory;
import red.mohist.sodionauth.core.database.Mapper;
import red.mohist.sodionauth.core.database.sqlite.SqliteMapper;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

public class DatabaseService {
    public HoneyFactory honeyFactory;
    public Suid suid;
    public SuidRich suidRich;
    public Mapper mapper;

    public DatabaseService(){
        Helper.getLogger().info("Initializing database service...");

        HoneyConfig.getHoneyConfig().dbName = "SQLite";
        HoneyConfig.getHoneyConfig().setUrl("jdbc:sqlite:bee.db");
        HoneyConfig.getHoneyConfig().loggerType="log4j2";

        honeyFactory = BeeFactory.getHoneyFactory();
        suid = BeeFactory.getHoneyFactory().getSuid();
        suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        switch (Config.database.type) {
            case "sqlite":
                mapper = new SqliteMapper();
                break;
            default:
                mapper = null;
        }
    }
}
