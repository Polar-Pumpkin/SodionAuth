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

package red.mohist.sodionauth.core.repositories;

import org.hibernate.Session;
import red.mohist.sodionauth.core.entities.AuthLastInfo;
import red.mohist.sodionauth.core.entities.ServerPlayerKey;

import java.util.UUID;

public class AuthLastInfoRepository {
    private AuthLastInfoRepository() {
        //
    }

    public static AuthLastInfo get(Session session, UUID uuid) {
        return session.get(AuthLastInfo.class, new ServerPlayerKey(uuid));
    }

    public static void delete(Session session, AuthLastInfo lastInfo) {
        session.delete(lastInfo);
    }
}
