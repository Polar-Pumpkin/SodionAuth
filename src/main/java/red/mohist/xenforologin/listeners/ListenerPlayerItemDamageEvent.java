package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerPlayerItemDamageEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        if (XenforoLogin.instance.needCancelled(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerItemDamageEvent.class.getName();
    }
}