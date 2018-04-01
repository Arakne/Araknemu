package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;

import java.util.ArrayList;
import java.util.List;

/**
 * Display cells by their movement value
 */
final public class Movement extends AbstractCommand {
    final private MapTemplateRepository repository;

    public Movement(MapTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Highlight cell by their movement value")
            .help("movement [1-8]")
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "movement";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        AdminUser user = AdminUser.class.cast(performer);

        int mov = Integer.parseInt(arguments.get(1));

        MapTemplate map = repository.get(user.player().position().map());

        List<Integer> cells = new ArrayList<>();

        for (int i = 0; i < map.cells().size(); ++i) {
            if (map.cells().get(i).movement() == mov) {
                cells.add(i);
            }
        }

        user.send(
            new FightStartPositions(
                new List[] {
                    cells,
                    new ArrayList()
                },
                0
            )
        );
    }
}
