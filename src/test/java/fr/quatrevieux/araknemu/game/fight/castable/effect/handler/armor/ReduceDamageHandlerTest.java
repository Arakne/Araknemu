package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.armor;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReduceDamageHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private ReduceDamageHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new ReduceDamageHandler();

        requestStack.clear();
    }

    @Test
    void handle() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertThrows(UnsupportedOperationException.class, () -> handler.handle(scope, scope.effects().get(0)));
    }

    @Test
    void buff() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.buff(scope, scope.effects().get(0));

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
    void onDamageWillReduce() {
        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(7);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), caster, caster, handler);

        Damage damage = new Damage(20, Element.NEUTRAL);

        handler.onDamage(buff, damage);

        assertEquals(14, damage.reducedDamage());
        assertEquals(6, damage.value());
    }

    @Test
    void onDamageWithNegativeStatsWillNotReduce() {
        caster.characteristics().alter(Characteristic.STRENGTH, -1000);

        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(7);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), caster, caster, handler);

        Damage damage = new Damage(20, Element.NEUTRAL);

        handler.onDamage(buff, damage);

        assertEquals(0, damage.reducedDamage());
        assertEquals(20, damage.value());
    }

    @Test
    void onDamageWillUseDamageElementCharacteristicAndIntelligence() {
        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(10);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), target, target, handler);

        Damage damage = new Damage(20, Element.NEUTRAL);
        handler.onDamage(buff, damage);
        assertEquals(10, damage.reducedDamage());

        target.characteristics().alter(Characteristic.STRENGTH, 100);
        damage = new Damage(20, Element.NEUTRAL);
        handler.onDamage(buff, damage);
        assertEquals(15, damage.reducedDamage());

        target.characteristics().alter(Characteristic.INTELLIGENCE, 100);
        damage = new Damage(20, Element.NEUTRAL);
        handler.onDamage(buff, damage);
        assertEquals(20, damage.reducedDamage());

        damage = new Damage(20, Element.AIR);
        handler.onDamage(buff, damage);
        assertEquals(15, damage.reducedDamage());
    }

    @Test
    void onDamageWillIgnoreUnsupportedElement() {
        SpellEffect returnEffect = Mockito.mock(SpellEffect.class);

        Mockito.when(returnEffect.min()).thenReturn(10);
        Mockito.when(returnEffect.special()).thenReturn(8);

        Buff buff = new Buff(returnEffect, Mockito.mock(Spell.class), target, target, handler);

        Damage damage = new Damage(20, Element.NEUTRAL);
        handler.onDamage(buff, damage);
        assertEquals(0, damage.reducedDamage());

        damage = new Damage(20, Element.AIR);
        handler.onDamage(buff, damage);
        assertEquals(10, damage.reducedDamage());
    }
}
