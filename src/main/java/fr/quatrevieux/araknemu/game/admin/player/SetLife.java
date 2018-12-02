package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.List;

/**
 * Change the player life
 */
final public class SetLife extends AbstractCommand {
    final private GamePlayer player;

    public SetLife(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Change the player current life")
            .help(
                "USAGE :",
                "\tsetlife [number|max]",
                "EXAMPLE :",
                "\tsetlife 300                : Set the player life to 300",
                "\tsetlife max                : Set full life to the player",
                "\t${player:John} setlife 250 : Set John's life to 250"
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "setlife";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        if (arguments.get(1).equalsIgnoreCase("max")) {
            player.properties().life().set(player.properties().life().max());

            performer.success("{} retrieve all his life", player.name());
        } else {
            player.properties().life().set(Integer.parseUnsignedInt(arguments.get(1)));

            performer.success("Life of {} is set to {}", player.name(), arguments.get(1));
        }
    }
}
