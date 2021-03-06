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

package red.mohist.sodionauth.yggdrasilserver;

import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.interfaces.LogProvider;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class YggdrasilServerEntry {
    private static final Logger logger = Logger
            .getLogger("XenforoLogin-Yggdrasil-Launcher");


    public static void main(String[] args) throws Exception {
        logger.info("Hello world!");
        new Helper(".", new LogProvider() {
            @Override
            public void info(String info) {
                logger.info(info);
            }

            @Override
            public void info(String info, Exception exception) {
                logger.log(Level.INFO, info, exception);
            }

            @Override
            public void warn(String info) {
                logger.warning(info);
            }

            @Override
            public void warn(String info, Exception exception) {
                logger.log(Level.WARNING, info, exception);
            }
        });
        new SodionAuthCore(new YggdrasilServerLoader());
        YggdrasilServerCore server = new YggdrasilServerCore();
        server.start();
    }

}
