package fr.quatrevieux.araknemu.game.exploration.map.trigger;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrap triggers for an exploration map
 */
final public class MapTriggers {
    final private Map<Integer, List<MapTrigger>> triggersByCell;
    final private Map<CellAction, CellActionPerformer> actions;

    public MapTriggers(Collection<MapTrigger> triggers, Map<CellAction, CellActionPerformer> actions) {
        this.actions = actions;

        triggersByCell = triggers
            .stream()
            .collect(Collectors.groupingBy(MapTrigger::cell))
        ;
    }

    /**
     * Perform cell actions on the player
     */
    public void perform(ExplorationPlayer player) {
        if (!triggersByCell.containsKey(player.position().cell())) {
            return;
        }

        try {
            for (MapTrigger trigger : triggersByCell.get(player.position().cell())) {
                actions.get(trigger.action()).perform(trigger, player);
            }
        } catch (Exception e) {
            // @todo handle exceptions
            throw new RuntimeException(e);
        }
    }
}
