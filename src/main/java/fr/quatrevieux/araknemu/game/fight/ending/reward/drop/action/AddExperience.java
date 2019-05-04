package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action;

import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
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

        fighter.apply(new FighterOperation() {
            @Override
            public void onPlayer(PlayerFighter fighter) {
                fighter.player().properties().experience().add(reward.xp());
            }
        });
    }
}
