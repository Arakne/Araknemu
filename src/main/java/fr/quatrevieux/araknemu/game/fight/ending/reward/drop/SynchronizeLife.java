package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Synchronize exploration player life points with the fighter
 */
final public class SynchronizeLife implements DropRewardAction {
    @Override
    public void apply(DropReward reward, Fighter fighter) {
        // @todo use visitor on fighter
        if (fighter instanceof PlayerFighter) {
            final GamePlayer player = ((PlayerFighter) fighter).player();

            // @todo set percent
            player.properties().life().set(
                player.properties().life().max() * fighter.life().current() / fighter.life().max()
            );
        }
    }
}
