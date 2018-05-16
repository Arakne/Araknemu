package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

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
    public boolean equals(Object obj) {
        return obj instanceof BasicCell && equals((BasicCell) obj);
    }
}
