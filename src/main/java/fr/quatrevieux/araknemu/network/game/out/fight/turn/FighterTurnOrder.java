package fr.quatrevieux.araknemu.network.game.out.fight.turn;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurnList;

import java.util.stream.Collectors;

/**
 * Send the order of fighters turn
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L320
 */
final public class FighterTurnOrder {
    final private FightTurnList list;

    public FighterTurnOrder(FightTurnList list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "GTL|" + list.fighters().stream()
            .map(Fighter::id)
            .map(Object::toString)
            .collect(Collectors.joining("|"))
        ;
    }
}
