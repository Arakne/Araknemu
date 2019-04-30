package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Try to move far all enemies
 *
 * The selected cell is the cell with the highest distance from the nearest enemy
 * Select only cells with higher distance than current cell
 */
final public class MoveFarEnemies implements ActionGenerator {
    final private Movement movement;

    private List<CoordinateCell<FightCell>> enemiesCells;
    private int currentCellScore = 0;

    public MoveFarEnemies() {
        movement = new Movement(this::score, scoredCell -> scoredCell.score() < currentCellScore);
    }

    @Override
    public void initialize(AI ai) {
        movement.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        final int movementPoints = ai.turn().points().movementPoints();

        if (movementPoints < 1) {
            return Optional.empty();
        }

        enemiesCells = ai.enemies().map(Fighter::cell).map(CoordinateCell::new).collect(Collectors.toList());
        currentCellScore = score(new CoordinateCell<>(ai.fighter().cell()));

        return movement.generate(ai);
    }

    /**
     * The score function
     *
     * Negates the score valid because lowest score is selected first,
     * but we needs that the highest distance is selected first.
     */
    private int score(CoordinateCell<FightCell> cell) {
        return -enemiesCells.stream().mapToInt(cell::distance).min().orElse(0);
    }
}
