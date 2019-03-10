package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Return the the save position when loose fight
 */
final public class ReturnToSavePosition implements DropRewardAction {
    @Override
    public void apply(DropReward reward, Fighter fighter) {
        // @todo visitor
        if (!(fighter instanceof PlayerFighter)) {
            return;
        }

        final GamePlayer player = ((PlayerFighter) fighter).player();

        player.setPosition(player.savedPosition());
    }
}
