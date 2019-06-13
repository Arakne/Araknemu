package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.teleport;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * Teleport to the given position
 *
 * Arguments : [mapid],[cellid],[cinematic]
 *
 * Cinematic is not required (if not set, or set to 0, no cinematic will be displayed)
 */
final public class Teleport implements Action {
    final static public class Factory implements ActionFactory {
        final private ExplorationMapService service;

        public Factory(ExplorationMapService service) {
            this.service = service;
        }

        @Override
        public String type() {
            return "TELEPORT";
        }

        @Override
        public Action create(ResponseAction entity) {
            final String[] position = StringUtils.split(entity.arguments(), ",", 3);

            return new Teleport(
                service,
                new Position(
                    Integer.parseInt(position[0]),
                    Integer.parseInt(position[1])
                ),
                position.length == 3 ? Integer.parseInt(position[2]) : 0
            );
        }
    }

    final private ExplorationMapService service;
    final private Position position;
    final private int cinematic;

    public Teleport(ExplorationMapService service, Position position, int cinematic) {
        this.service = service;
        this.position = position;
        this.cinematic = cinematic;
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        // @todo check if map exists ?
        return true;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        player.interactions().push(new ChangeMap(player, service.load(position.map()), position.cell(), cinematic));
    }
}
