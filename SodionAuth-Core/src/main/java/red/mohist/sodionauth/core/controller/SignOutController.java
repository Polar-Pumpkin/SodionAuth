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

package red.mohist.sodionauth.core.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.FullHttpRequest;
import red.mohist.sodionauth.core.provider.UserProvider;

import java.sql.SQLException;

public class SignOutController implements Controller {
    @Override
    public Object handle(JsonElement content, FullHttpRequest request) throws SQLException {
        JsonObject post = content.getAsJsonObject();
        String username = post.get("username").getAsString();
        String password = post.get("password").getAsString();
        if (UserProvider.instance.signout(username, password)) {
            return null;
        } else {
            return "err";
        }
    }
}
