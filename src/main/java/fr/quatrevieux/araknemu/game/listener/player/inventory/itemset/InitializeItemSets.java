package fr.quatrevieux.araknemu.game.listener.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.object.UpdateItemSet;

/**
 * Send all item set data on join game
 */
final public class InitializeItemSets implements Listener<GameJoined> {
    final private GamePlayer player;

    public InitializeItemSets(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(GameJoined event) {
        player.inventory().itemSets()
            .all()
            .forEach(
                itemSet -> player.send(
                    new UpdateItemSet(itemSet)
                )
            )
        ;
    }

    @Override
    public Class<GameJoined> event() {
        return GameJoined.class;
    }
}
