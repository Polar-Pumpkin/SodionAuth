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

package red.mohist.sodionauth.core.database.entities;

import red.mohist.sodionauth.core.database.annotations.limits.NotNull;
import red.mohist.sodionauth.core.database.annotations.limits.PrimaryKey;

public class AuthInfo extends Entity {
    @PrimaryKey
    protected Integer id;

    @NotNull
    protected Integer userId;

    @NotNull
    protected String type;

    protected String data;

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public AuthInfo setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String getType() {
        return type;
    }

    public AuthInfo setType(String type) {
        this.type = type;
        return this;
    }

    public String getData() {
        return data;
    }

    public AuthInfo setData(String data) {
        this.data = data;
        return this;
    }
}
