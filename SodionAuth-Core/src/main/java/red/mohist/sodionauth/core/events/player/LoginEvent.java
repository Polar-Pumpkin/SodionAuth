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

package red.mohist.sodionauth.core.events.player;

import red.mohist.sodionauth.core.events.Cancellable;
import red.mohist.sodionauth.core.events.Event;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

public class LoginEvent extends Event implements Cancellable {
    private final AbstractPlayer player;
    private boolean cancel;

    public LoginEvent(AbstractPlayer player) {
        this.player = player;
    }

    public AbstractPlayer getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancel = true;
    }

}
