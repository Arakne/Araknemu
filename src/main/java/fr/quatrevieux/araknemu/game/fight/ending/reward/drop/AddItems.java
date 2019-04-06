package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;

/**
 * Add the dropped items to the fighter
 */
final public class AddItems implements DropRewardAction {
    final private ItemService service;

    public AddItems(ItemService service) {
        this.service = service;
    }

    @Override
    public void apply(DropReward reward, Fighter fighter) {
        // @todo use visitor on fighter
        if (!(fighter instanceof PlayerFighter)) {
            return;
        }

        final PlayerInventory inventory = ((PlayerFighter) fighter).player().inventory();

        reward.items().forEach((itemId, quantity) -> {
            for (; quantity > 0; --quantity) {
                inventory.add(service.create(itemId));
            }
        });
    }
}
