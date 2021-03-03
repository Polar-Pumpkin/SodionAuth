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

package red.mohist.sodionauth.yggdrasilserver.modules;

import java.util.HashMap;

public class Textures {
    public int timestamp;
    public String profileId;
    public String profileName;
    public HashMap<String, Texture> textures;

    public Textures setProfileId(String profileId) {
        this.profileId = profileId;
        return this;
    }

    public Textures setTimestamp(int timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Textures setProfileName(String profileName) {
        this.profileName = profileName;
        return this;
    }

    public Textures addProperties(String key, Texture value) {
        textures.put(key, value);
        return this;
    }

    public Textures removeProperties(String key) {
        textures.remove(key);
        return this;
    }

    public Textures setTime() {
        setTimestamp((int) (System.currentTimeMillis() / 1000));
        return this;
    }

    public static class Texture {
        public String url;
        public HashMap<String, String> metadata;

        public Texture setUrl(String url) {
            this.url = url;
            return this;
        }

        public Texture addMetadata(String key, String value) {
            metadata.put(key, value);
            return this;
        }

        public Texture removeMetadata(String key) {
            metadata.remove(key);
            return this;
        }
    }
}
