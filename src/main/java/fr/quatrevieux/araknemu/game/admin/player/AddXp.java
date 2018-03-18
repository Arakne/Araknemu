package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.List;

/**
 * Add experience to player
 */
final public class AddXp extends AbstractCommand {
    final private GamePlayer player;

    public AddXp(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Add experience to player")
            .help("addxp [quantity]")
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "addxp";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        long xp = Long.parseUnsignedLong(arguments.get(1));

        player.level().addExperience(xp);

        performer.success("Add {} xp to {} (level = {})", xp, player.name(), player.level().level());
    }
}
