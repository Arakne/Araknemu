package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.List;

/**
 * Info command for a player
 */
final public class Info extends AbstractCommand {
    final private GamePlayer player;

    public Info(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Display information on the selected player")
            .requires(Permission.MANAGE_PLAYER)
            .help("info")
        ;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        performer.success("Player info : {}", player.name());
        performer.success("==============================");
        performer.info("ID:    {}", player.id());
        performer.info("Name:  {}", player.name());
        performer.info("Level: {}", player.experience().level());
        performer.info("Race:  {}", player.race().name());
        performer.info("Sex:   {}", player.spriteInfo().sex());
        performer.info("GfxID: {}", player.spriteInfo().gfxId());
        performer.success("==============================");
    }
}
