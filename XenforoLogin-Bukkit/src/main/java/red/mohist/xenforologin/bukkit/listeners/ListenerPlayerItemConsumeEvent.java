package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;

public class ListenerPlayerItemConsumeEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerItemConsumeEvent.class.getName();
    }
}