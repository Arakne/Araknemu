package fr.quatrevieux.araknemu.game.fight.turn.order;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

import java.util.List;

/**
 * Compute the fighters turn order
 */
public interface FighterOrderStrategy {
    /**
     * Order the fighters from given teams
     *
     * @param teams The fight teams
     */
    public List<Fighter> compute(List<FightTeam> teams);
}
