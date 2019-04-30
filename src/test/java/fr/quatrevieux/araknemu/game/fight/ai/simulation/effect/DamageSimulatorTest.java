package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageSimulatorTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private Fighter target;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        target = other.fighter();
    }

    @Test
    void simulateSimple() {
        assertEquals(-15, simulate());
    }

    @Test
    void simulateWithResistance() {
        target.characteristics().alter(Characteristic.RESISTANCE_EARTH, 5);
        assertEquals(-10, simulate());

        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_EARTH, 25);
        assertEquals(-6, simulate());
    }

    @Test
    void simulateWithBoost() {
        fighter.characteristics().alter(Characteristic.FIXED_DAMAGE, 5);
        assertEquals(-20, simulate());

        fighter.characteristics().alter(Characteristic.PERCENT_DAMAGE, 25);
        assertEquals(-22, simulate());

        fighter.characteristics().alter(Characteristic.STRENGTH, 100);
        assertEquals(-32, simulate());
    }

    @Test
    void simulateBuff() {
        DamageSimulator simulator = new DamageSimulator(Element.EARTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.duration()).thenReturn(2);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, fighter.cell());

        CastScope scope = makeCastScope(fighter, spell, effect, fighter.cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(-22, simulation.selfLife());
    }

    @Test
    void simulateArea() {
        DamageSimulator simulator = new DamageSimulator(Element.EARTH);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, other.fighter().cell());

        CastScope scope = makeCastScope(fighter, spell, effect, other.fighter().cell());
        simulator.simulate(simulation, scope.effects().get(0));

        assertEquals(-15, simulation.selfLife());
        assertEquals(-15, simulation.enemiesLife());
    }

    private int simulate() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.min()).thenReturn(10);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastSimulation simulation = new CastSimulation(spell, fighter, target.cell());

        CastScope scope = makeCastScope(fighter, spell, effect, target.cell());
        new DamageSimulator(Element.EARTH).simulate(simulation, scope.effects().get(0));

        return simulation.enemiesLife();
    }
}
