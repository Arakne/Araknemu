package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Remove fighters from a team flag
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1155
 */
final public class RemoveTeamFighters {
    final private FightTeam team;
    final private Collection<? extends Fighter> fighters;

    public RemoveTeamFighters(FightTeam team, Collection<? extends Fighter> fighters) {
        this.team = team;
        this.fighters = fighters;
    }

    @Override
    public String toString() {
        return "Gt" + team.id() + "|" +
            fighters.stream()
                .map(fighter -> "-" + fighter.id())
                .collect(Collectors.joining("|"))
        ;
    }
}
