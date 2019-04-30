package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Pathfinder;

import java.util.Optional;

/**
 * Try to move near the selected enemy
 */
final public class MoveNearEnemy implements ActionGenerator {
    private Decoder<FightCell> decoder;
    private Pathfinder<FightCell> pathfinder;
    private Fighter fighter;

    @Override
    public void initialize(AI ai) {
        this.fighter = ai.fighter();
        this.decoder = new Decoder<>(ai.fighter().fight().map());
        this.pathfinder = decoder
            .pathfinder()
            .targetDistance(1)
            .walkablePredicate(FightCell::walkableIgnoreFighter)
            .cellWeightFunction(this::cellCost)
        ;
    }

    @Override
    public Optional<Action> generate(AI ai) {
        final int movementPoints = ai.turn().points().movementPoints();

        if (movementPoints < 1) {
            return Optional.empty();
        }

        return ai.enemy()
            .map(enemy -> pathfinder.findPath(ai.fighter().cell(), enemy.cell()).truncate(movementPoints + 1))
            .filter(path -> path.size() > 1)
            .map(path -> new Move(ai.turn(), ai.fighter(), path))
        ;
    }

    /**
     * Compute the cell cost for optimize the path finding
     */
    private int cellCost(FightCell cell) {
        // A fighter is on the cell : the cell is not walkable
        // But the fighter may leave the place at the next turn
        // The cost is higher than a simple detour, but permit to resolve a path blocked by a fighter
        if (cell.fighter().isPresent()) {
            return 15;
        }

        int cost = 1;

        // @todo check agility for tackle ?
        for (Direction direction : Direction.RESTRICTED) {
            if (decoder.nextCellByDirection(cell, direction)
                .flatMap(FightCell::fighter)
                .filter(other -> !other.team().equals(fighter.team()))
                .isPresent()
            ) {
                // Add a cost of 3 for each enemy around the cell
                // This cost corresponds to the detour cost + 1
                cost += 3;
            }
        }

        return cost;
    }
}
