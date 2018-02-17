package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayableCharacter;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

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

        player.print(
            new PlayableCharacter.Printer() {
                @Override
                public PlayableCharacter.Printer level(int level) {
                    performer.info("Level: {}", level);

                    return this;
                }

                @Override
                public PlayableCharacter.Printer name(String name) {
                    performer.info("Name: {}", name);

                    return this;
                }

                @Override
                public PlayableCharacter.Printer race(Race race) {
                    performer.info("Race: {}", race);

                    return this;
                }

                @Override
                public PlayableCharacter.Printer sex(Sex sex) {
                    performer.info("Sex: {}", sex);

                    return this;
                }

                @Override
                public PlayableCharacter.Printer colors(Colors colors) {
                    return this;
                }

                @Override
                public PlayableCharacter.Printer id(int id) {
                    performer.info("ID: {}", id);

                    return this;
                }

                @Override
                public PlayableCharacter.Printer gfxID(int gfxID) {
                    performer.info("GfxID: {}", gfxID);

                    return this;
                }

                @Override
                public PlayableCharacter.Printer server(int id) {
                    performer.info("Server: {}", id);

                    return this;
                }

                @Override
                public PlayableCharacter.Printer accessories(Accessories accessories) {
                    return this;
                }
            }
        );
    }
}
