package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

import java.util.List;

/**
 * Get stats on map cells
 */
final public class MapStats extends AbstractCommand {
    final private MapTemplateRepository repository;

    public MapStats(MapTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Compute stats un maps and cells")
            .requires(Permission.DEBUG)
        ;
    }

    @Override
    public String name() {
        return "mapstats";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) throws AdminException {
        performer.info("Loading map stats...");

        int[] movements = new int[10];

        int count = 0;
        int withFightPos = 0;
        int cellCount = 0;
        int desactived = 0;
        int interactive = 0;

        for (MapTemplate template : repository.all()) {
            ++count;

            if (template.fightPlaces().length >= 2) {
                ++withFightPos;
            }

            cellCount += template.cells().size();

            for (MapTemplate.Cell cell : template.cells()) {
                ++movements[cell.movement()];

                if (!cell.active()) {
                    ++desactived;
                }

                if (cell.interactive()) {
                    ++interactive;
                }
            }
        }

        performer.success("============================");
        performer.success("        Maps stats");
        performer.success("============================");

        performer.info("Maps        : {}", count);
        performer.info("Fight pos   : {} ({}%)", withFightPos, withFightPos * 100 / count);
        performer.info("Cells       : {} ({} per maps)", cellCount, cellCount / count);
        performer.info("Desactived  : {} ({}%)", desactived, desactived * 100 / cellCount);
        performer.info("Interactive : {} ({}%)", interactive, interactive * 100 / cellCount);
        performer.info("Unwalkable  : {} ({}%)", movements[0], movements[0] * 100 / cellCount);
        performer.info("Mov 1       : {} ({}%)", movements[1], movements[1] * 100 / cellCount);
        performer.info("Triggers    : {} ({}%)", movements[2], movements[2] * 100 / cellCount);
        performer.info("Mov 3       : {} ({}%)", movements[3], movements[3] * 100 / cellCount);
        performer.info("Normal      : {} ({}%)", movements[4], movements[4] * 100 / cellCount);
        performer.info("Mount park  : {} ({}%)", movements[5], movements[5] * 100 / cellCount);
        performer.info("Road        : {} ({}%)", movements[6], movements[6] * 100 / cellCount);
        performer.info("Mov 7       : {} ({}%)", movements[7], movements[7] * 100 / cellCount);

        performer.success("============================");
    }
}
