package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 *
 */
class RaulebaqueModuleTest extends FightBaseCase {
    @Test
    void effect() throws Exception {
        Fight fight = createFight(false);

        fight.register(new RaulebaqueModule(fight));
        fight.nextState();
        fight.start();

        FightCell playerCell = player.fighter().cell();
        FightCell otherCell = other.fighter().cell();

        player.fighter().move(fight.map().get(123));
        other.fighter().move(fight.map().get(321));

        CastScope scope = makeCastScopeForEffect(784);

        fight.effects().apply(scope);

        assertSame(playerCell, player.fighter().cell());
        assertSame(otherCell, other.fighter().cell());
    }

    @Test
    void startPositions() throws Exception {
        Fight fight = createFight(false);
        RaulebaqueModule module = new RaulebaqueModule(fight);

        fight.register(module);
        fight.nextState();
        fight.start();

        FightCell playerCell = player.fighter().cell();
        FightCell otherCell = other.fighter().cell();

        Map<Fighter, FightCell> expected = new HashMap<>();
        expected.put(player.fighter(), playerCell);
        expected.put(other.fighter(), otherCell);

        assertEquals(expected, module.startPositions());
    }
}
