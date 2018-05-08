package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.event.ActionPointsUsed;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendUsedActionPointsTest extends FightBaseCase {
    private Fight fight;
    private SendUsedActionPoints listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendUsedActionPoints(
            fight = createFight()
        );
    }

    @Test
    void onActionPointsUsed() {
        listener.on(new ActionPointsUsed(player.fighter(), 4));

        requestStack.assertLast(ActionEffect.usedActionPoints(player.fighter(), 4));
    }
}
