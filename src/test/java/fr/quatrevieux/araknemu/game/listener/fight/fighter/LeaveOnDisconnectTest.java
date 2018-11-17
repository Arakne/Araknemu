package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;

class LeaveOnDisconnectTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter fighter;
    private LeaveOnDisconnect listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = makePlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(fighter, fight.team(0));

        listener = new LeaveOnDisconnect(fighter);

        requestStack.clear();
    }

    @Test
    void onDisconnect() {
        listener.on(new Disconnected());

        assertFalse(fight.fighters().contains(fighter));
        requestStack.assertLast(new RemoveSprite(fighter.sprite()));
    }
}
