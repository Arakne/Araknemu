package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.network.game.out.fight.FightEnd;

import java.util.ArrayList;
import java.util.List;

/**
 * The fight is terminated
 */
final public class FinishState implements FightState {
    @Override
    public void start(Fight fight) {
        FightRewardsSheet rewardsSheet = fight.type().rewards().generate(results(fight));

        rewardsSheet.rewards().forEach(reward -> reward.fighter().dispatch(new FightFinished(reward)));

        fight.send(new FightEnd(rewardsSheet));

        fight.destroy();
    }

    @Override
    public int id() {
        return 4;
    }

    /**
     * Compute the end fight results
     */
    private EndFightResults results(Fight fight) {
        List<Fighter> winners = new ArrayList<>();
        List<Fighter> loosers = new ArrayList<>();

        for (FightTeam team : fight.teams()) {
            if (team.alive()) {
                winners.addAll(team.fighters());
            } else {
                loosers.addAll(team.fighters());
            }
        }

        return new EndFightResults(fight, winners, loosers);
    }
}
