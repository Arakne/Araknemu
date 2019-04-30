package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest extends FightBaseCase {
    private Simulator simulator;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushFunctionalSpells();

        fight = createFight();
        fighter = player.fighter();
        fight.register(new AiModule(fight, new ChainAiFactory()));
        simulator = fight.attachment(Simulator.class);
    }

    @Test
    void simulateWithoutSupportedEffects() {
        simulator = new Simulator();

        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());

        assertEquals(fighter.spells().get(3), simulation.spell());
        assertEquals(fighter, simulation.caster());
        assertEquals(other.fighter().cell(), simulation.target());
    }

    @Test
    void simulateAttack() {
        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-9, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateAttackShouldLimitByTargetLife() {
        fighter.characteristics().alter(Characteristic.FIXED_DAMAGE, 1000);

        CastSimulation simulation = simulator.simulate(fighter.spells().get(3), fighter, other.fighter().cell());

        assertEquals(-50, simulation.enemiesLife());
    }

    @Test
    void simulateBoost() {
        CastSimulation simulation = simulator.simulate(getSpell(42, 1), fighter, fighter.cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(0, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(76, simulation.selfBoost());
    }

    @Test
    void simulateWithProbableEffects() {
        CastSimulation simulation = simulator.simulate(getSpell(109, 5), fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-22, simulation.enemiesLife());
        assertEquals(0, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateShouldHandleCriticality() {
        assertEquals(-22, simulator.simulate(getSpell(109, 5), fighter, other.fighter().cell()).enemiesLife());

        fighter.characteristics().alter(Characteristic.CRITICAL_BONUS, 100);
        assertEquals(-36, simulator.simulate(getSpell(109, 5), fighter, other.fighter().cell()).enemiesLife());
    }

    @Test
    void simulatePoison() {
        CastSimulation simulation = simulator.simulate(getSpell(181, 5), fighter, other.fighter().cell());

        assertEquals(0, simulation.alliesLife());
        assertEquals(-36, simulation.enemiesLife());
        assertEquals(-36, simulation.selfLife());
        assertEquals(0, simulation.alliesBoost());
        assertEquals(0, simulation.enemiesBoost());
        assertEquals(0, simulation.selfBoost());
    }

    @Test
    void simulateWithoutCriticalEffects() {
        assertEquals(-2000, simulator.simulate(getSpell(468, 5), fighter, other.fighter().cell()).enemiesBoost());
    }

    private Spell getSpell(int id, int level) {
        return container.get(SpellService.class).get(id).level(level);
    }
}
