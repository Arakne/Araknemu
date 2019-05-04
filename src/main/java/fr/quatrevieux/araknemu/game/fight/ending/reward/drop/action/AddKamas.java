package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action;

import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Add kamas to fighter
 */
final public class AddKamas implements DropRewardAction {
    @Override
    public void apply(DropReward reward, Fighter fighter) {
        if (reward.kamas() == 0) {
            return;
        }

        fighter.apply(new FighterOperation() {
            @Override
            public void onPlayer(PlayerFighter fighter) {
                fighter.player().inventory().addKamas(reward.kamas());
            }
        });
    }
}
