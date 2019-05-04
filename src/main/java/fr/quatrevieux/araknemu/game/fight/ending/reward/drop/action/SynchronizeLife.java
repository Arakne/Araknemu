package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action;

import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Synchronize exploration player life points with the fighter
 */
final public class SynchronizeLife implements DropRewardAction, FighterOperation {
    @Override
    public void apply(DropReward reward, Fighter fighter) {
        fighter.apply(this);
    }

    @Override
    public void onPlayer(PlayerFighter fighter) {
        // @todo set percent
        fighter.player().properties().life().set(
            fighter.player().properties().life().max() * fighter.life().current() / fighter.life().max()
        );
    }
}
