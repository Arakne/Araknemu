package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Simple cell type
 */
final public class BasicCell implements ExplorationMapCell {
    final private int id;
    final private MapTemplate.Cell template;
    final private ExplorationMap map;

    public BasicCell(int id, MapTemplate.Cell template, ExplorationMap map) {
        this.id = id;
        this.template = template;
        this.map = map;
    }

    @Override
    public ExplorationMap map() {
        return map;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public boolean walkable() {
        return template.active() && template.movement() > 1;
    }

    @Override
    public boolean free() {
        // @todo check movement value
        if (!walkable()) {
            return false;
        }

        for (Creature creature : map.creatures()) {
            if (equals(creature.cell())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BasicCell && equals((BasicCell) obj);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
