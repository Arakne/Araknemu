package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Remove all life point of looser fighter
 */
final public class SetDead implements DropRewardAction {
    @Override
    public void apply(DropReward reward, Fighter fighter) {
        // @todo use visitor on fighter
        if (fighter instanceof PlayerFighter) {
            ((PlayerFighter) fighter).player().properties().life().set(0);
        }
    }
}
