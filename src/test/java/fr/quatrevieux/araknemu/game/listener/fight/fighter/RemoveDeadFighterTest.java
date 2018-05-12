package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.listener.fight.CheckFightTerminated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemoveDeadFighterTest extends FightBaseCase {
    private Fight fight;
    private RemoveDeadFighter listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();
        listener = new RemoveDeadFighter(fight);

        fight.dispatcher().remove(CheckFightTerminated.class);
        fight.dispatcher().remove(RemoveDeadFighter.class);
        requestStack.clear();
    }

    @Test
    void onFighterDieNotCurrentTurn() {
        listener.on(new FighterDie(other.fighter(), other.fighter()));

        assertFalse(other.fighter().cell().fighter().isPresent());
    }

    @Test
    void onFighterDieCurrentTurn() {
        player.fighter().life().alter(player.fighter(), -1000);
        listener.on(new FighterDie(player.fighter(), player.fighter()));

        assertFalse(player.fighter().cell().fighter().isPresent());
        assertThrows(FightException.class, () -> player.fighter().turn());
        assertFalse(fight.turnList().current().get().active());
    }
}