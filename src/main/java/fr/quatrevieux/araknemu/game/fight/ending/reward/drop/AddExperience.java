package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Add experience to fighter
 */
final public class AddExperience implements DropRewardAction {
    @Override
    public void apply(DropReward reward, Fighter fighter) {
        if (reward.xp() == 0) {
            return;
        }

        // @todo use visitor on fighter
        if (fighter instanceof PlayerFighter) {
            ((PlayerFighter) fighter).player().properties().experience().add(reward.xp());
        }
    }
}
