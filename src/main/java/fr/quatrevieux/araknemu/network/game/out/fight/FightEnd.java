package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;

import java.util.stream.Collectors;

/**
 * The fight is terminated
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1240
 */
final public class FightEnd {
    final private FightRewardsSheet rewardsSheet;

    public FightEnd(FightRewardsSheet rewardsSheet) {
        this.rewardsSheet = rewardsSheet;
    }

    @Override
    public String toString() {
        return "GE" +
            rewardsSheet.results().fight().duration() + "|" +
            rewardsSheet.results().fight().turnList().currentFighter().id() + "|" +
            rewardsSheet.type().ordinal() + "|" +
            rewardsSheet.rewards().stream().map(DropReward::render).collect(Collectors.joining("|"))
        ;
    }
}
