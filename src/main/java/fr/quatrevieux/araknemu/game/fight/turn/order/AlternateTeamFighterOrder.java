package fr.quatrevieux.araknemu.game.fight.turn.order;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Sort fighters by initiative with alternate teams
 */
final public class AlternateTeamFighterOrder implements FighterOrderStrategy {
    @Override
    public List<Fighter> compute(List<FightTeam> teams) {
        List<Queue<Fighter>> fightersByTeam = computeTeamsOrder(teams);

        List<Fighter> orderedFighters = new ArrayList<>();

        boolean hasChanges;

        do {
            hasChanges = false;

            // Poll the next fighter on each teams
            for (Queue<Fighter> fighters : fightersByTeam) {
                if (!fighters.isEmpty()) {
                    orderedFighters.add(fighters.remove());
                    hasChanges = true;
                }
            }
        } while (hasChanges);

        return orderedFighters;
    }

    private List<Queue<Fighter>> computeTeamsOrder(List<FightTeam> teams) {
        List<Queue<Fighter>> fightersByTeam = new ArrayList<>();

        for (FightTeam team : teams) {
            // Sort team fighters by their initiative desc
            Queue<Fighter> fighters = new PriorityQueue<>(
                team.fighters().size(),
                (f1, f2) -> f2.characteristics().initiative() - f1.characteristics().initiative()
            );

            fighters.addAll(team.fighters());

            fightersByTeam.add(fighters);
        }

        // Sort team by their best fighter initiative desc
        fightersByTeam.sort((t1, t2) -> t2.peek().characteristics().initiative() - t1.peek().characteristics().initiative());

        return fightersByTeam;
    }
}
