package fr.quatrevieux.araknemu.game.event.listener.player.inventory;

import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.inventory.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Rebuild the stuff stats
 */
final public class UpdateStuffStats implements Listener<EquipmentChanged> {
    final private GamePlayer player;

    public UpdateStuffStats(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(EquipmentChanged event) {
        player.characteristics().rebuildStuffStats();
    }

    @Override
    public Class<EquipmentChanged> event() {
        return EquipmentChanged.class;
    }
}
