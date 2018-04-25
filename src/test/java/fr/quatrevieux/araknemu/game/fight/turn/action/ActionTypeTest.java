package fr.quatrevieux.araknemu.game.fight.turn.action;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionTypeTest {

    @Test
    void byId() {
        assertEquals(ActionType.CAST, ActionType.byId(300));
        assertEquals(ActionType.MOVE, ActionType.byId(1));
    }
}