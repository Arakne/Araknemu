package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;

import java.util.Arrays;
import java.util.List;

/**
 * Show / hide fight places
 */
final public class FightPos extends AbstractCommand {
    final private MapTemplateRepository repository;

    public FightPos(MapTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Show all fight positions")
            .help("fightpos [show|hide]")
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "fightpos";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        AdminUser user = AdminUser.class.cast(performer);

        if (arguments.size() > 1 && arguments.get(1).equalsIgnoreCase("hide")) {
            user.player().exploration().leave();
            user.send("GV"); // @todo leave fight packet
            return;
        }

        MapTemplate map = repository.get(user.player().position().map());

        if (
            map.fightPlaces().length < 2
            || map.fightPlaces()[0].isEmpty()
            || map.fightPlaces()[1].isEmpty()
        ) {
            performer.error("No fight places found");
            return;
        }

        performer.info("Places : {}", Arrays.toString(map.fightPlaces()));

        if (arguments.size() > 1 && arguments.get(1).equalsIgnoreCase("show")) {
            user.send(new FightStartPositions(map.fightPlaces(), 0));
        }
    }
}
