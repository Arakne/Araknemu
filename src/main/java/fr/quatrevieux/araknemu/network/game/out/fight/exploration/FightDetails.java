package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

import java.util.stream.Collectors;

/**
 * Details for one fight
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Fights.as#L86
 */
final public class FightDetails {
    final private Fight fight;

    public FightDetails(Fight fight) {
        this.fight = fight;
    }

    @Override
    public String toString() {
        return "fD" + fight.id() + "|" +
            fight.teams().stream()
                .map(FightTeam::fighters)
                .map(
                    fighters -> fighters.stream()
                        .map(fighter -> fighter.sprite().name() + "~" + fighter.level())
                        .collect(Collectors.joining(";"))
                )
                .collect(Collectors.joining("|"))
        ;
    }
}
