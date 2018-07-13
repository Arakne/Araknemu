package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StealLifeHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private StealLifeHandler handler;

    int baseLife;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();
        target.move(fight.map().get(123));

        caster.life().alter(caster, -50);
        baseLife = caster.life().current();

        handler = new StealLifeHandler(Element.AIR);

        requestStack.clear();
    }

    @Test
    void applyFixedEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        assertEquals(10, target.life().max() - target.life().current());
        assertEquals(5, caster.life().current() - baseLife);

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.alterLifePoints(caster, caster, 5)
        );
    }

    @Test
    void applyBoostEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());

        player.characteristics().base().set(Characteristic.AGILITY, 100);

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        assertEquals(20, target.life().max() - target.life().current());
        assertEquals(10, caster.life().current() - baseLife);
    }

    @Test
    void applyOnFullLife() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());

        caster.life().alter(caster, 50);
        requestStack.clear();

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.alterLifePoints(caster, caster, 0)
        );
    }

    @Test
    void applyWithoutEffect() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(0);
        Mockito.when(effect.area()).thenReturn(new CellArea());

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        requestStack.assertAll(ActionEffect.alterLifePoints(caster, target, 0));
    }

    @Test
    void applyHealTooHigh() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        caster.life().alter(caster, 45);
        requestStack.clear();

        Mockito.when(effect.min()).thenReturn(20);
        Mockito.when(effect.area()).thenReturn(new CellArea());

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -20),
            ActionEffect.alterLifePoints(caster, caster, 5)
        );
    }

    @Test
    void applyDamage1() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(1);
        Mockito.when(effect.area()).thenReturn(new CellArea());

        handler.handle(caster, Mockito.mock(Spell.class), effect, target.cell());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -1),
            ActionEffect.alterLifePoints(caster, caster, 1)
        );
    }

    @Test
    void applyToEmptyCell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());

        handler.handle(caster, Mockito.mock(Spell.class), effect, fight.map().get(5));

        requestStack.assertEmpty();

        assertEquals(baseLife, caster.life().current());
    }

    @Test
    void applyToEmptyCellWithArea() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2)));

        handler.handle(caster, Mockito.mock(Spell.class), effect, fight.map().get(122));

        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -10));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, caster, 5));
    }

    @Test
    void applyWithAreaMultipleFighters() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 25)));

        handler.handle(caster, Mockito.mock(Spell.class), effect, fight.map().get(122));

        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -10));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, caster, 5));

        requestStack.assertOne(ActionEffect.alterLifePoints(caster, caster, -10));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, caster, 5));
    }

    /**
     * #56 : Suicide with steel life will not heal
     */
    @Test
    void applySuicide() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10000);
        Mockito.when(effect.area()).thenReturn(new CellArea());

        handler.handle(caster, Mockito.mock(Spell.class), effect, caster.cell());

        assertEquals(0, caster.life().current());
        assertTrue(caster.dead());

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, caster, -245),
            ActionEffect.fighterDie(caster, caster)
        );
    }

    @Test
    void buffWillAddBuffToList() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.duration()).thenReturn(5);

        handler.buff(caster, spell, effect, target.cell());

        Optional<Buff> found = target.buffs().stream().filter(buff -> buff.effect().equals(effect)).findFirst();

        assertTrue(found.isPresent());
        assertEquals(caster, found.get().caster());
        assertEquals(target, found.get().target());
        assertEquals(effect, found.get().effect());
        assertEquals(spell, found.get().action());
        assertEquals(handler, found.get().hook());
        assertEquals(5, found.get().remainingTurns());
    }

    @Test
    void buffWithAreaMultipleFighters() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 20)));

        handler.buff(caster, Mockito.mock(Spell.class), effect, fight.map().get(122));

        assertTrue(caster.buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
        assertTrue(target.buffs().stream().anyMatch(buff -> buff.effect().equals(effect)));
    }

    @Test
    void onStartTurn() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10);

        assertTrue(handler.onStartTurn(new Buff(effect, Mockito.mock(Spell.class), caster, target, handler)));

        assertEquals(10, target.life().max() - target.life().current());
        assertEquals(5, caster.life().current() - baseLife);

        requestStack.assertAll(
            ActionEffect.alterLifePoints(caster, target, -10),
            ActionEffect.alterLifePoints(caster, caster, 5)
        );
    }

    @Test
    void onStartTurnOnDie() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);

        Mockito.when(effect.min()).thenReturn(10000);

        assertFalse(handler.onStartTurn(new Buff(effect, Mockito.mock(Spell.class), caster, target, handler)));
    }
}
