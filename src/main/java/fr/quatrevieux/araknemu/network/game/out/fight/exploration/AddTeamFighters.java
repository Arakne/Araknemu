package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Add fighters on a team flag
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1155
 */
final public class AddTeamFighters {
    final private FightTeam team;
    final private Collection<? extends Fighter> fighters;

    public AddTeamFighters(FightTeam team) {
        this(team, team.fighters());
    }

    public AddTeamFighters(FightTeam team, Collection<? extends Fighter> fighters) {
        this.team = team;
        this.fighters = fighters;
    }

    @Override
    public String toString() {
        return "Gt" + team.id() + "|" +
            fighters.stream()
                .map(fighter -> "+" + fighter.id() + ";" + fighter.sprite().name() + ";" + fighter.level())
                .collect(Collectors.joining("|"))
        ;
    }
}
