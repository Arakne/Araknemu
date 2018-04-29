package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Registry for all actions factories
 */
final public class ActionFactoryRegistry implements CellActionFactory {
    final private Map<Integer, CellActionFactory> factories = new HashMap<>();

    @Override
    public CellAction create(MapTrigger trigger) {
        if (!factories.containsKey(trigger.action())) {
            throw new NoSuchElementException("Cannot found cell action " + trigger.action());
        }

        return factories.get(trigger.action()).create(trigger);
    }

    /**
     * Register a new factory into the registry
     *
     * @param action The action id
     * @param factory The factory
     */
    public ActionFactoryRegistry register(int action, CellActionFactory factory) {
        factories.put(action, factory);

        return this;
    }
}
