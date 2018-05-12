package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckFightTerminatedTest extends FightBaseCase {
    private Fight fight;
    private CheckFightTerminated listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        listener = new CheckFightTerminated(fight);

        fight.dispatcher().remove(CheckFightTerminated.class);
        requestStack.clear();
    }

    @Test
    void onFighterDieAlreadyFighting() {
        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertTrue(fight.active());
    }

    @Test
    void onFighterDieWillTerminateFight() {
        player.fighter().life().alter(player.fighter(), -1000);

        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertFalse(fight.active());
    }
}
