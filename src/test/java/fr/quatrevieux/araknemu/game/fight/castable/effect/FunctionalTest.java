package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionalTest extends FightBaseCase {
    private SpellService service;
    private Fight fight;

    private PlayerFighter fighter1;
    private PlayerFighter fighter2;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = container.get(SpellService.class);

        dataSet.pushFunctionalSpells();

        fight = createFight();

        fighter1 = player.fighter();
        fighter2 = other.fighter();

        fighter1.move(fight.map().get(185));
        fighter2.move(fight.map().get(170));

        fight.state(PlacementState.class).startFight();
        fight.turnList().start();

        requestStack.clear();
    }

    @Test
    void poisonSpell() {
        castNormal(181, fighter1.cell()); // Tremblement

        Optional<Buff> buff1 = fighter1.buffs().stream().filter(buff -> buff.effect().effect() == 99).findFirst();
        Optional<Buff> buff2 = fighter2.buffs().stream().filter(buff -> buff.effect().effect() == 99).findFirst();

        assertTrue(buff1.isPresent());
        assertTrue(buff2.isPresent());

        assertEquals(5, buff1.get().remainingTurns());
        assertEquals(4, buff2.get().remainingTurns());

        assertEquals(fighter1.life().current(), fighter1.life().max());
        assertEquals(fighter2.life().current(), fighter2.life().max());

        fighter1.turn().stop();

        assertEquals(12, fighter2.life().max() - fighter2.life().current());
        requestStack.assertOne(ActionEffect.alterLifePoints(fighter1, fighter2, -12));

        assertEquals(4, buff1.get().remainingTurns());
        assertEquals(4, buff2.get().remainingTurns());

        fighter2.turn().stop();

        requestStack.assertOne(ActionEffect.alterLifePoints(fighter1, fighter1, -12));
        assertEquals(4, buff1.get().remainingTurns());
        assertEquals(3, buff2.get().remainingTurns());
        fighter1.turn().stop();

        requestStack.assertOne(ActionEffect.alterLifePoints(fighter1, fighter2, -12));
        assertEquals(3, buff1.get().remainingTurns());
        assertEquals(3, buff2.get().remainingTurns());
    }

    @Test
    void skipNextTurn() {
        castNormal(1630, fighter2.cell());

        Optional<Buff> found = fighter2.buffs().stream().filter(buff -> buff.effect().effect() == 140).findFirst();

        assertTrue(found.isPresent());
        assertEquals(140, found.get().effect().effect());
        assertEquals(0, found.get().remainingTurns());

        requestStack.assertOne(ActionEffect.skipNextTurn(fighter1, fighter2));

        fighter1.turn().stop();
        assertSame(fighter1, fight.turnList().currentFighter());
        fighter1.turn().stop();

        assertSame(fighter2, fight.turnList().currentFighter());
    }

    private void castNormal(int spellId, FightCell target) {
        FightTurn currentTurn = fight.turnList().current().get();

        currentTurn.perform(new Cast(
            currentTurn,
            currentTurn.fighter(),
            service.get(spellId).level(5),
            target,
            new SpellConstraintsValidator(currentTurn),

            // Ensure no critical hit / fail
            new CriticalityStrategy() {
                public int hitRate(int base) { return 0; }
                public int failureRate(int base) { return 0; }
                public boolean hit(int baseRate) { return false; }
                public boolean failed(int baseRate) { return false; }
            }
        ));

        currentTurn.terminate();
    }
}
