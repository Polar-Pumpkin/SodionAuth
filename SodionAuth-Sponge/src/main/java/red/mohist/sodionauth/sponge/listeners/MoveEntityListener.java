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

package red.mohist.sodionauth.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.sponge.implementation.SpongePlayer;
import red.mohist.sodionauth.sponge.interfaces.SpongeAPIListener;

public class MoveEntityListener implements SpongeAPIListener {

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onMoveEntity(MoveEntityEvent event, @First Player player) {
        if (event instanceof MoveEntityEvent.Teleport
                || !Service.auth.needCancelled(new SpongePlayer(player))) {
            return;
        }

        if (Config.teleport.getTpSpawnBeforeLogin(true)) {
            if (Config.security.getSpectatorLogin()) {
                event.setCancelled(true);
                new SpongePlayer(player).teleport(new LocationInfo(
                        SodionAuthCore.instance.default_location.world,
                        SodionAuthCore.instance.default_location.x,
                        SodionAuthCore.instance.default_location.y,
                        SodionAuthCore.instance.default_location.z,
                        SodionAuthCore.instance.default_location.yaw,
                        SodionAuthCore.instance.default_location.pitch
                ));
            } else {
                if (SodionAuthCore.instance.default_location.x
                        != event.getToTransform().getPosition().getFloorX()
                        || SodionAuthCore.instance.default_location.z
                        != event.getToTransform().getPosition().getFloorZ()) {
                    event.setCancelled(true);
                    new SpongePlayer(player).teleport(new LocationInfo(
                            SodionAuthCore.instance.default_location.world,
                            SodionAuthCore.instance.default_location.x,
                            event.getToTransform().getPosition().getFloorY(),
                            SodionAuthCore.instance.default_location.z,
                            SodionAuthCore.instance.default_location.yaw,
                            SodionAuthCore.instance.default_location.pitch
                    ));
                }
            }

        } else {
            if (event.getFromTransform().getPosition().getFloorX()
                    != event.getToTransform().getPosition().getFloorX()
                    || event.getFromTransform().getPosition().getFloorZ()
                    != event.getToTransform().getPosition().getFloorZ()) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void eventClass() {
        MoveEntityEvent.class.getName();
    }
}
