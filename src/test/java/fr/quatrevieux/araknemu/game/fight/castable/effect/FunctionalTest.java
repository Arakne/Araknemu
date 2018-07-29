package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastSuccess;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FightAction;
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

    @Test
    void probableEffectSpell() {
        Spell spell = castNormal(109, fighter2.cell()); // Bluff

        int damage = fighter2.life().max() - fighter2.life().current();

        assertBetween(1, 50, damage);

        requestStack.assertAll(
            "GAS1",
            new FightAction(new CastSuccess(fighter1, spell, fighter2.cell(), false)),
            ActionEffect.usedActionPoints(fighter1, 4),
            ActionEffect.alterLifePoints(fighter1, fighter2, -damage),
            "GAF0|1"
        );
    }

    @Test
    void returnSpell() {
        castNormal(4, fighter1.cell()); // Return spell
        fighter1.turn().stop();

        castNormal(109, fighter1.cell()); // Bluff

        int damage = fighter2.life().max() - fighter2.life().current();

        assertBetween(1, 50, damage);

        requestStack.assertOne(ActionEffect.alterLifePoints(fighter2, fighter2, -damage));
        requestStack.assertOne(ActionEffect.returnSpell(fighter1, true));
    }

    @Test
    void pointsChange() {
        castNormal(115, fighter1.cell()); // Odorat

        Optional<Buff> addAp = fighter1.buffs().stream().filter(buff -> buff.effect().effect() == 111).findFirst();
        Optional<Buff> remAp = fighter1.buffs().stream().filter(buff -> buff.effect().effect() == 168).findFirst();
        Optional<Buff> addMp = fighter1.buffs().stream().filter(buff -> buff.effect().effect() == 128).findFirst();
        Optional<Buff> remMp = fighter1.buffs().stream().filter(buff -> buff.effect().effect() == 169).findFirst();

        assertTrue(addAp.isPresent());
        assertTrue(remAp.isPresent());
        assertTrue(addMp.isPresent());
        assertTrue(remMp.isPresent());

        requestStack.assertOne(ActionEffect.buff(addAp.get(), addAp.get().effect().min()));
        requestStack.assertOne(ActionEffect.buff(remAp.get(), -remAp.get().effect().min()));
        requestStack.assertOne(ActionEffect.buff(addMp.get(), addMp.get().effect().min()));
        requestStack.assertOne(ActionEffect.buff(remMp.get(), -remMp.get().effect().min()));

        assertBetween(2, 5, addAp.get().effect().min());
        assertBetween(1, 4, remAp.get().effect().min());
        assertBetween(2, 5, addMp.get().effect().min());
        assertBetween(1, 4, remMp.get().effect().min());

        int apChange = addAp.get().effect().min() - remAp.get().effect().min();
        int mpChange = addMp.get().effect().min() - remMp.get().effect().min();

        assertEquals(6 + apChange, fighter1.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(3 + mpChange, fighter1.characteristics().get(Characteristic.MOVEMENT_POINT));

        passTurns(4);

        assertEquals(6, fighter1.characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(3, fighter1.characteristics().get(Characteristic.MOVEMENT_POINT));
    }

    @Test
    void addCharacteristic() {
        castNormal(42, fighter1.cell()); // Chance

        Optional<Buff> addLuck = fighter1.buffs().stream().filter(buff -> buff.effect().effect() == 123).findFirst();

        assertTrue(addLuck.isPresent());
        assertBetween(51, 60, addLuck.get().effect().min());
        assertEquals(addLuck.get().effect().min(), fighter1.characteristics().get(Characteristic.LUCK));
        requestStack.assertOne(ActionEffect.buff(addLuck.get(), addLuck.get().effect().min()));

        passTurns(5);

        assertEquals(0, fighter2.characteristics().get(Characteristic.LUCK));
    }

    @Test
    void removeCharacteristic() {
        castNormal(468, fighter2.cell()); // Flêche d'huile

        Optional<Buff> removeIntel = fighter2.buffs().stream().filter(buff -> buff.effect().effect() == 155).findFirst();

        assertTrue(removeIntel.isPresent());
        assertEquals(400, removeIntel.get().effect().min());
        assertEquals(-400, fighter2.characteristics().get(Characteristic.INTELLIGENCE));
        requestStack.assertOne(ActionEffect.buff(removeIntel.get(), -removeIntel.get().effect().min()));

        passTurns(5);

        assertEquals(0, fighter2.characteristics().get(Characteristic.INTELLIGENCE));
    }

    private void passTurns(int number) {
        for (; number > 0; --number) {
            fighter1.turn().stop();
            fighter2.turn().stop();
        }
    }

    private Spell castNormal(int spellId, FightCell target) {
        FightTurn currentTurn = fight.turnList().current().get();
        Spell spell = service.get(spellId).level(5);

        currentTurn.perform(new Cast(
            currentTurn,
            currentTurn.fighter(),
            spell,
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

        return spell;
    }
}
