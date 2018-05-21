package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.Fight;

import java.util.stream.Collectors;

/**
 * Show fight on exploration map
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L1117
 */
final public class ShowFight {
    final private Fight fight;

    public ShowFight(Fight fight) {
        this.fight = fight;
    }

    @Override
    public String toString() {
        return "Gc+" + fight.id() + ";" + fight.type().id() + "|" +
            fight.teams().stream()
                .map(team -> team.id() + ";" + team.cell() + ";" + team.type() + ";" + team.alignment().id())
                .collect(Collectors.joining("|"))
        ;
    }
}
