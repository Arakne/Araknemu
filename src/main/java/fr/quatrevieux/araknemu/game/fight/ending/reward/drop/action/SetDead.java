package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action;

import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Remove all life point of looser fighter
 */
final public class SetDead implements DropRewardAction, FighterOperation {
    @Override
    public void apply(DropReward reward, Fighter fighter) {
        fighter.apply(this);
    }

    @Override
    public void onPlayer(PlayerFighter fighter) {
        fighter.player().properties().life().set(0);
    }
}
