package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RefreshStates;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendState;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatesModuleTest extends FightBaseCase {
    @Test
    void listeners() throws Exception {
        Fight fight = createFight(false);

        fight.register(new RaulebaqueModule(fight));
        fight.nextState();
        fight.start();

        assertTrue(fight.dispatcher().has(RefreshStates.class));
        assertTrue(fight.dispatcher().has(SendState.class));

        fight.stop();

        assertFalse(fight.dispatcher().has(RefreshStates.class));
        assertFalse(fight.dispatcher().has(SendState.class));
    }

    @Test
    void pushStateEffect() throws Exception {
        Fight fight = createFight(false);

        fight.register(new RaulebaqueModule(fight));
        fight.nextState();
        fight.start();

        CastScope scope = makeCastScopeForEffect(950);

        fight.effects().apply(scope);

        requestStack.assertLast(ActionEffect.addState(other.fighter(), 0));
    }

    @Test
    void removeStateEffect() throws Exception {
        Fight fight = createFight(false);

        fight.register(new RaulebaqueModule(fight));
        fight.nextState();
        fight.start();

        other.fighter().states().push(0);
        requestStack.clear();

        CastScope scope = makeCastScopeForEffect(951);

        fight.effects().apply(scope);

        requestStack.assertLast(ActionEffect.removeState(other.fighter(), 0));
    }
}
