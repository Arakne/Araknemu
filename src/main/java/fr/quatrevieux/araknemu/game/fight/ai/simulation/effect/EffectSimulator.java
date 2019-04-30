package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;

public interface EffectSimulator {
    public void simulate(CastSimulation simulation, CastScope.EffectScope effect);
}
