package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.game.activity.ActivityService;
import fr.quatrevieux.araknemu.game.activity.Task;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathException;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.slf4j.Logger;

import java.time.Duration;

/**
 * Task for random move monsters on exploration maps
 *
 * At most one group move per map per execution.
 * The move is performed if its randomly selected with moveChange and if the path is not too complex
 */
final public class MoveMonsters implements Task {
    final private MonsterEnvironmentService service;
    final private Duration delay;
    final private int moveChance;

    final private RandomUtil random = new RandomUtil();

    /**
     * Initialise the move task
     *
     * @param service The environment server
     * @param delay  The period delay
     * @param moveChance Move chance for each groups, in percent
     */
    public MoveMonsters(MonsterEnvironmentService service, Duration delay, int moveChance) {
        this.service = service;
        this.delay = delay;
        this.moveChance = moveChance;
    }

    @Override
    public void execute(Logger logger) {
        service.groups()
            .filter(position -> !position.available().isEmpty())
            .filter(position -> random.bool(moveChance))
            .forEach(this::move)
        ;
    }

    @Override
    public Duration delay() {
        return delay;
    }

    @Override
    public boolean retry(ActivityService service) {
        return false;
    }

    @Override
    public String toString() {
        return "Move monsters";
    }

    /**
     * Try to move a group from the given position
     */
    private void move(LivingMonsterGroupPosition position) {
        final MonsterGroup group = random.of(position.available());
        final ExplorationMapCell newCell = position.cell();

        // Fixed group
        if (newCell.equals(group.cell())) {
            return;
        }

        try {
            Path<ExplorationMapCell> path = new Decoder<>(newCell.map())
                .pathfinder()
                .exploredCellLimit(50)
                .findPath(group.cell(), newCell)
            ;

            group.move(path);
        } catch (PathException e) {
            // Ignore exception
        }
    }
}
