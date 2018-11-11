package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidator;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateWalkable;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndGameActionTest extends GameBaseCase {
    private EndGameAction handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new EndGameAction();
    }

    @Test
    void handleSuccess() throws Exception {
        dataSet.pushMaps();

        ExplorationPlayer player = explorationPlayer();

        player.interactions().push(
            new Move(
                player,
                new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
                new PathValidator[] {new ValidateWalkable()}
            )
        );

        handler.handle(session, new GameActionAcknowledge(1));

        assertFalse(player.interactions().busy());
        assertEquals(395, player.position().cell());
    }
}