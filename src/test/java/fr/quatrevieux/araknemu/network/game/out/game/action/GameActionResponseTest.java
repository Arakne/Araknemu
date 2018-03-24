package fr.quatrevieux.araknemu.network.game.out.game.action;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameActionResponseTest {
    @Test
    void generateWithOneArgument() {
        assertEquals(
            "GA3;1;45;abKebIgbf",
            new GameActionResponse(3, ActionType.MOVE, 45, "abKebIgbf").toString()
        );
    }

    @Test
    void generateWithoutArguments() {
        assertEquals(
            "GA3;1;45",
            new GameActionResponse(3, ActionType.MOVE, 45).toString()
        );
    }

    @Test
    void generateWithoutTwoArguments() {
        assertEquals(
            "GA3;1;45;aaa;bbb",
            new GameActionResponse(3, ActionType.MOVE, 45, "aaa", "bbb").toString()
        );
    }
}