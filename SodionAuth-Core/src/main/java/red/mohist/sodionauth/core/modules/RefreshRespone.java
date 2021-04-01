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

package red.mohist.sodionauth.core.modules;

public class RefreshRespone {
    public String accessToken;
    public String clientToken;
    public Profile selectedProfile;
    public User user;

    public RefreshRespone setSelectedProfile(Profile profile) {
        selectedProfile = profile;
        return this;
    }

    public RefreshRespone setUser(User user) {
        this.user = user;
        return this;
    }

    public RefreshRespone setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public RefreshRespone setClientToken(String clientToken) {
        this.clientToken = clientToken;
        return this;
    }
}
