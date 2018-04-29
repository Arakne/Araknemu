package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;

/**
 * Teleport action
 */
final public class Teleport implements CellAction {
    final static public int ACTION_ID = 0;

    final private ExplorationMapService service;
    final private int cell;
    final private Position target;

    public Teleport(ExplorationMapService service, int cell, Position target) {
        this.service = service;
        this.cell = cell;
        this.target = target;
    }

    @Override
    public void perform(ExplorationPlayer player) {
        ExplorationMap map = service.load(target.map());

        // teleport on same map
        if (map.equals(player.map())) {
            player.changeCell(target.cell());
            return;
        }

        player.interactions().push(new ChangeMap(player, map, target.cell()));
    }

    @Override
    public int cell() {
        return cell;
    }
}
