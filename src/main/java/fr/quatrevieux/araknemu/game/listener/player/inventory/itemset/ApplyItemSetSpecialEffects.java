package fr.quatrevieux.araknemu.game.listener.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Apply the item set's special effects
 */
final public class ApplyItemSetSpecialEffects implements Listener<EquipmentChanged> {
    final private GamePlayer player;

    public ApplyItemSetSpecialEffects(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(EquipmentChanged event) {
        event.entry()
            .item()
            .set()
            .map(set -> player.inventory().itemSets().get(set))
            .ifPresent(
                itemSet -> {
                    if (event.equiped()) {
                        itemSet.applyCurrentBonus(player);
                    } else {
                        itemSet.relieveLastBonus(player);
                    }
                }
            )
        ;
    }

    @Override
    public Class<EquipmentChanged> event() {
        return EquipmentChanged.class;
    }
}
