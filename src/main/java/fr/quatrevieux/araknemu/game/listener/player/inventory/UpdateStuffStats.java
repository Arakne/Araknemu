package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

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
        if (event.equiped()) {
            for (SpecialEffect effect : event.entry().item().specials()) {
                effect.apply(player);
            }
        } else {
            for (SpecialEffect effect : event.entry().item().specials()) {
                effect.relieve(player);
            }
        }

        player.characteristics().rebuildStuffStats();
    }

    @Override
    public Class<EquipmentChanged> event() {
        return EquipmentChanged.class;
    }
}
