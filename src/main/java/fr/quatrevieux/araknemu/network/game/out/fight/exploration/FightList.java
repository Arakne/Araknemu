package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.util.DofusDate;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * List of fights on the current map
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Fights.as#L55
 */
final public class FightList {
    final private Collection<Fight> fights;

    public FightList(Collection<Fight> fights) {
        this.fights = fights;
    }

    @Override
    public String toString() {
        return "fL" + fights.stream()
            .map(
                fight ->
                    fight.id() + ";" +
                    (fight.active() ? DofusDate.fromDuration(fight.duration()).toMilliseconds() : -1) + ";" +
                    fight.teams().stream()
                        .map(team -> team.type() + "," + team.alignment().id() + "," + team.fighters().size())
                        .collect(Collectors.joining(";"))
            )
            .collect(Collectors.joining("|"))
        ;
    }
}
