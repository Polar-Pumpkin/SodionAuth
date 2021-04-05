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

package red.mohist.sodionauth.core.http.textures;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import red.mohist.sodionauth.core.modules.Profile;
import red.mohist.sodionauth.core.modules.Textures;
import red.mohist.sodionauth.core.provider.UserProvider;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.libs.http.HttpEntity;
import red.mohist.sodionauth.libs.http.client.ResponseHandler;
import red.mohist.sodionauth.libs.http.client.methods.CloseableHttpResponse;
import red.mohist.sodionauth.libs.http.client.methods.HttpGet;
import red.mohist.sodionauth.libs.http.client.methods.HttpPost;
import red.mohist.sodionauth.libs.http.entity.StringEntity;
import red.mohist.sodionauth.libs.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class OnlineTexture extends Texture {
    private final String url;

    OnlineTexture(JsonElement config) {
        url = config.getAsJsonObject().get("url").getAsString();
    }

    public Textures getTextures(String username) {
        try {
            String uuid = getUuidByName(username);
            if (uuid == null) {
                return null;
            }
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }
                return null;
            };

            HttpGet request = new HttpGet(url + "/sessionserver/session/minecraft/profile/" + uuid);
            final CloseableHttpResponse response = Service.httpClient.execute(request);
            String result = responseHandler.handleResponse(response);
            response.close();
            if (result == null) {
                return null;
            }
            Profile profile = new Gson().fromJson(result, Profile.class);
            return new Gson().fromJson(
                    Arrays.toString(Base64.getDecoder().decode(profile.getProperty("textures").value)),
                    Textures.class)
                    .setProfileName(username)
                    .setProfileId(UserProvider.plainUUID(username))
                    .setTime();


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getUuidByName(String username) {
        try {
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }
                return null;
            };

            HttpPost request = new HttpPost(url + "api/profiles/minecraft");
            JsonArray jsonContent = new JsonArray();
            jsonContent.add(username);
            request.setEntity(new StringEntity(jsonContent.getAsString()));
            final CloseableHttpResponse response = Service.httpClient.execute(request);
            String result = responseHandler.handleResponse(response);
            response.close();

            String uuid = null;
            for (JsonElement element : new Gson().fromJson(result, JsonArray.class)) {
                uuid = element.getAsJsonObject().get("id").getAsString();
            }
            return uuid;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
