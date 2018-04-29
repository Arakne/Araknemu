package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellActionFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * Create the teleport action
 */
final public class TeleportFactory implements CellActionFactory {
    final private ExplorationMapService service;

    public TeleportFactory(ExplorationMapService service) {
        this.service = service;
    }

    @Override
    public CellAction create(MapTrigger trigger) {
        String[] position = StringUtils.split(trigger.arguments(), ",", 2);

        return new Teleport(
            service,
            trigger.cell(),
            new Position(
                Integer.parseInt(position[0]),
                Integer.parseInt(position[1])
            )
        );
    }
}
