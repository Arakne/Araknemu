package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action;

import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
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
        fighter.apply(new FighterOperation() {
            @Override
            public void onPlayer(PlayerFighter fighter) {
                final PlayerInventory inventory = fighter.player().inventory();

                reward.items().forEach((itemId, quantity) -> {
                    for (; quantity > 0; --quantity) {
                        inventory.add(service.create(itemId));
                    }
                });
            }
        });
    }
}
