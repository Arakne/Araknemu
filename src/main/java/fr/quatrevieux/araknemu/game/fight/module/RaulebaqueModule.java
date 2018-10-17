package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.RaulebaqueHandler;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

import java.util.HashMap;
import java.util.Map;

/**
 * Module for handle Raulebaque spell
 *
 * This spell will reset all fighters position to the initial one
 */
final public class RaulebaqueModule implements FightModule {
    final private Fight fight;

    private Map<Fighter, FightCell> startPositions;

    public RaulebaqueModule(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(784, new RaulebaqueHandler(fight, this));
    }

    public Map<Fighter, FightCell> startPositions() {
        return startPositions;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FightStarted>() {
                @Override
                public void on(FightStarted event) {
                    loadStartPositions();
                }

                @Override
                public Class<FightStarted> event() {
                    return FightStarted.class;
                }
            }
        };
    }

    /**
     * Load the start positions
     */
    private void loadStartPositions() {
        startPositions = new HashMap<>();

        for (Fighter fighter : fight.fighters()) {
            startPositions.put(fighter, fighter.cell());
        }
    }
}
