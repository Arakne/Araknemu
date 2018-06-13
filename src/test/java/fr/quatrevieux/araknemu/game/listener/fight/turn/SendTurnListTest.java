package fr.quatrevieux.araknemu.game.listener.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnListChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendTurnListTest extends FightBaseCase {
    private Fight fight;
    private SendTurnList listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new SendTurnList(fight);

        fight.state(PlacementState.class).startFight();
    }

    @Test
    void onTurnListChanged() {
        listener.on(new TurnListChanged(fight.turnList()));

        requestStack.assertLast(new FighterTurnOrder(fight.turnList()));
    }
}