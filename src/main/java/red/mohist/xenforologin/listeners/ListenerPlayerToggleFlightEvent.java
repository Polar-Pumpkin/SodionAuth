package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerPlayerToggleFlightEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerToggleFlightEvent(PlayerToggleFlightEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerToggleFlightEvent.class.getName();
    }
}
