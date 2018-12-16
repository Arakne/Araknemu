package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Send the total and max weight of the inventory
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L222
 */
final public class InventoryWeight {
    final private GamePlayer player;

    public InventoryWeight(GamePlayer player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "Ow" + player.inventory().weight() + "|" + player.properties().characteristics().pods();
    }
}
