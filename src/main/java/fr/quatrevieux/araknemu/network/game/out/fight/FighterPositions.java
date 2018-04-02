package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Send all fighters positions
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L193
 */
final public class FighterPositions {
    final private Collection<Fighter> fighters;

    public FighterPositions(Collection<Fighter> fighters) {
        this.fighters = fighters;
    }

    @Override
    public String toString() {
        return
            "GIC|" +
            fighters.stream()
                .map(fighter -> fighter.id() + ";" + fighter.cell().id())
                .collect(Collectors.joining("|"))
            ;
    }
}
