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

package red.mohist.sodionauth.core.authbackends;

import red.mohist.sodionauth.core.config.MainConfiguration;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.User;

public abstract class AuthBackend {
    public String friendlyName;
    public boolean allowLogin;
    public boolean allowRegister;


    public AuthBackend(MainConfiguration.ApiBean.ApiConfigBean config){
        this.friendlyName = config.friendlyName;
        this.allowLogin = config.allowLogin;
        this.allowRegister = config.allowRegister;
    }

    public abstract LoginResult login(User user, AuthInfo authInfo, String password);
    public enum LoginResult{
        SUCCESS,
        ERROR_NAME {
            public String correct;
            public LoginResult setCorrect(String correct){
                this.correct = correct;
                return this;
            }
            public String getCorrect(){
                return correct;
            }
        },
        ERROR_PASSWORD,
        ERROR_SERVER,
        NO_USER;

        public LoginResult setCorrect(String ss){
            return this;
        }
    }

    public abstract RegisterResult register(User user,String password);
    public enum RegisterResult{
        SUCCESS,
        NAME_EXIST,
        EMAIL_EXIST,
        ERROR_NAME,
        ERROR_EMAIL,
        ERROR_SERVER
    }

    public abstract GetResult get(User user);
    public enum GetResult{
        SUCCESS,
        ERROR_NAME,
        EMAIL_DIFFERENT,
        NO_SUCH_USER,
        ERROR_SERVER {
            public String correct;
            public GetResult setCorrect(String correct){
                this.correct = correct;
                return this;
            }
            public String getCorrect(){
                return correct;
            }
        };
        public GetResult setCorrect(String ss){
            return this;
        }
        public String getCorrect(){
            return null;
        }
    }
}
