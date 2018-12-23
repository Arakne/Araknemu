package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidateRestrictedDirectionsTest extends GameBaseCase {
    private ValidateRestrictedDirections validator;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushItemTemplates()
        ;

        map = explorationPlayer().map();

        validator = new ValidateRestrictedDirections();
    }

    @Test
    void validateWithoutRestrictions() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Path<ExplorationMapCell> path = new Path<>(
            new Decoder<>(player.map()),
            Arrays.asList(
                new PathStep(map.get(279), Direction.WEST),
                new PathStep(map.get(278), Direction.WEST)
            )
        );

        Move move = new Move(player, path, new PathValidator[0]);

        assertSame(path, validator.validate(move, move.path()));
    }

    @Test
    void validateWithRestrictionsSuccess() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        player.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);

        Path<ExplorationMapCell> path = new Path<>(
            new Decoder<>(player.map()),
            Arrays.asList(
                new PathStep(map.get(279), Direction.WEST),
                new PathStep(map.get(294), Direction.SOUTH_EAST)
            )
        );

        Move move = new Move(player, path, new PathValidator[0]);

        assertSame(path, validator.validate(move, move.path()));
    }

    @Test
    void validateWithRestrictionsError() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        player.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);

        Path<ExplorationMapCell> path = new Path<>(
            new Decoder<>(player.map()),
            Arrays.asList(
                new PathStep(map.get(279), Direction.WEST),
                new PathStep(map.get(278), Direction.WEST)
            )
        );

        Move move = new Move(player, path, new PathValidator[0]);

        assertThrows(IllegalArgumentException.class, () -> validator.validate(move, move.path()));
    }
}
