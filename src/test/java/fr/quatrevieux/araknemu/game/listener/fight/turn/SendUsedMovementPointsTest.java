package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.event.MovementPointsUsed;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendUsedMovementPointsTest extends FightBaseCase {
    private Fight fight;
    private SendUsedMovementPoints listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendUsedMovementPoints(
            fight = createFight()
        );
    }

    @Test
    void onMovementPointsUsed() {
        listener.on(new MovementPointsUsed(player.fighter(), 4));

        requestStack.assertLast(ActionEffect.usedMovementPoints(player.fighter(), 4));
    }
}