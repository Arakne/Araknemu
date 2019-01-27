package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;

/**
 * Leave current NPC dialog
 */
final public class LeaveDialog implements Action {
    final static public class Factory implements ActionFactory {
        final static private LeaveDialog INSTANCE = new LeaveDialog();

        @Override
        public String type() {
            return "LEAVE";
        }

        @Override
        public Action create(ResponseAction entity) {
            return INSTANCE;
        }
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        return true;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        player.interactions().get(NpcDialog.class).stop();
    }
}
