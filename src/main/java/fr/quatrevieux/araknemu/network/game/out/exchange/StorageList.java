package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.game.item.inventory.Inventory;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.network.game.out.object.ItemSerializer;

import java.util.stream.Collectors;

/**
 * Send the current stored items and kamas of the storage
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L1039
 */
final public class StorageList {
    final private Inventory<? extends ItemEntry> inventory;

    public StorageList(Inventory<? extends ItemEntry> inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return "EL" +
            inventory.stream()
                .map(ItemSerializer::new)
                .map(serializer -> "O" + serializer)
                .collect(Collectors.joining(";")) + ";" +
            "G" + inventory.kamas()
        ;
    }
}
