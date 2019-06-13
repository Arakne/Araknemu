package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.teleport;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * Teleport to Astrub statue
 */
final public class GoToAstrub implements Action {
    final static public class Factory implements ActionFactory {
        final private ExplorationMapService service;

        public Factory(ExplorationMapService service) {
            this.service = service;
        }

        @Override
        public String type() {
            return "GOTO_ASTRUB";
        }

        @Override
        public Action create(ResponseAction entity) {
            return new GoToAstrub(service);
        }
    }

    final private ExplorationMapService service;

    public GoToAstrub(ExplorationMapService service) {
        this.service = service;
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        return true;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        final Position position = player.player().race().astrubPosition();

        player.interactions().push(new ChangeMap(player, service.load(position.map()), position.cell(), 7));
        player.player().setSavedPosition(position);
        player.send(Information.positionSaved());
    }
}
