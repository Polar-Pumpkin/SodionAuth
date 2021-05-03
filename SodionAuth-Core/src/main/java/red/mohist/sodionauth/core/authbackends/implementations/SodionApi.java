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

package red.mohist.sodionauth.core.authbackends.implementations;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import red.mohist.sodionauth.core.authbackends.AuthBackend;
import red.mohist.sodionauth.core.config.MainConfiguration;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.Lang;
import red.mohist.sodionauth.libs.http.HttpEntity;
import red.mohist.sodionauth.libs.http.client.entity.UrlEncodedFormEntity;
import red.mohist.sodionauth.libs.http.client.fluent.Form;
import red.mohist.sodionauth.libs.http.client.methods.CloseableHttpResponse;
import red.mohist.sodionauth.libs.http.client.methods.HttpGet;
import red.mohist.sodionauth.libs.http.client.methods.HttpPost;
import red.mohist.sodionauth.libs.http.client.methods.HttpUriRequest;
import red.mohist.sodionauth.libs.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

public class SodionApi extends AuthBackend {
    private final String url;
    private final String key;

    public static final String REGISTER = "register";
    public static final String LOGIN_BY_NAME = "loginByName";
    public static final String LOGIN_BY_EMAIL = "loginByEmail";
    public static final String GET_BY_NAME = "getByName";
    public static final String GET_BY_EMAIL = "getByEmail";

    public SodionApi(MainConfiguration.ApiBean.WebBean config) {
        super(config);
        this.url = config.url;
        this.key = config.key;
    }

    @Override
    public LoginResult login(User user, AuthInfo authInfo, String password) {
        try {
            JsonObject response = request(LOGIN_BY_NAME, ImmutableMap.of(
                    "username", user.getName(),
                    "password", password
            ));
            switch (response.get("result").getAsString()){
                case "ok":
                    return LoginResult.SUCCESS();
                case "name_incorrect":
                    return LoginResult.ERROR_NAME().setCorrect(response.get("correct").getAsString());
                case "password_incorrect":
                    return LoginResult.ERROR_PASSWORD();
                case "no_user":
                    return LoginResult.NO_USER();
                default:
                    Helper.getLogger().warn(
                            "Error while login player " + user.getName() + " in sodionapi " + url + " " + response.get("result").getAsString());
                    return LoginResult.ERROR_SERVER();
            }
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while login player " + user.getName() + " in sodionapi " + url, e);
            return LoginResult.ERROR_SERVER();
        }
    }

    @Override
    public RegisterResult register(User user, String password) {
        try {
            JsonObject response = request(REGISTER, ImmutableMap.of(
                    "username", user.getName(),
                    "email", user.getEmail(),
                    "password", password
            ));
            switch (response.get("result").getAsString()) {
                case "ok":
                    return RegisterResult.SUCCESS;
                case "user_exist":
                    return RegisterResult.NAME_EXIST;
                case "email_exist":
                    return RegisterResult.EMAIL_EXIST;
                default:
                    Helper.getLogger().warn(
                            "Error while register player " + user.getName() + " in sodionapi " + url + " " + response.get("result").getAsString());
                    return RegisterResult.ERROR_SERVER;
            }
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while register player " + user.getName() + " to sodionapi " + url, e);
            return RegisterResult.ERROR_SERVER;
        }
    }

    @Override
    public GetResult get(User user) {
        try {
            JsonObject response = request(GET_BY_NAME,
                    ImmutableMap.of("username",user.getName()));
            switch (response.get("result").getAsString()){
                case "ok":
                    return GetResult.SUCCESS()
                            .setEmail(response.get("email").getAsString())
                            .setName(response.get("username").getAsString());
                case "no_user":
                    return GetResult.NO_SUCH_USER();
                default:
                    return GetResult.ERROR_SERVER();
            }
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while get player " + user.getName() + " to sodionapi " + url, e);
            return GetResult.ERROR_SERVER();
        }
    }

    protected JsonObject request(String action, Map<String, String> data) throws IOException {
        HttpUriRequest request;
        HttpPost postRequest = new HttpPost(url);
        Form form = Form.form();
        form.add("action",action);
        data.forEach(form::add);
        form.add("key",key);
        postRequest.setEntity(new UrlEncodedFormEntity(form.build()));
        request = postRequest;
        return Service.httpClient.executeAsJson(request);
    }
}
