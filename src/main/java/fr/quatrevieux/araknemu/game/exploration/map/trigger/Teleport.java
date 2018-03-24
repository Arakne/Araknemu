package fr.quatrevieux.araknemu.game.exploration.map.trigger;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.apache.commons.lang3.StringUtils;

/**
 * Teleport action
 */
final public class Teleport implements CellActionPerformer {
    final private ExplorationMapService service;

    public Teleport(ExplorationMapService service) {
        this.service = service;
    }

    @Override
    public CellAction action() {
        return CellAction.TELEPORT;
    }

    @Override
    public void perform(MapTrigger trigger, ExplorationPlayer player) throws Exception {
        String[] data = StringUtils.split(trigger.arguments(), ",", 2);

        int mapId  = Integer.parseInt(data[0]);
        int cellId = Integer.parseInt(data[1]);

        ExplorationMap map = service.load(mapId);

        // teleport on same map
        if (map.equals(player.map())) {
            player.changeCell(cellId);
            return;
        }

        player.interactions().push(
            new ChangeMap(player, map, cellId)
        );
    }
}
