package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.out.fight.ChangeFighterPlaceError;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class ChangeFighterStartPlaceTest extends FightBaseCase {
    private ChangeFighterStartPlace handler;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        handler = new ChangeFighterStartPlace();
    }

    @Test
    void handleError() throws Exception {
        fight = createFight();
        fighter = player.fighter();

        try {
            handler.handle(session, new FighterChangePlace(256));
            fail("ErrorPacket must be thrown");
        } catch (ErrorPacket e) {
            assertEquals(
                new ChangeFighterPlaceError().toString(),
                e.packet().toString()
            );
        }
    }

    @Test
    void handleSuccess() throws Exception {
        fight = createFight();
        fighter = player.fighter();

        handler.handle(session, new FighterChangePlace(123));

        requestStack.assertLast(new FighterPositions(fight.fighters()));

        assertEquals(123, fighter.cell().id());
    }

    @Test
    void functionalNotInFight() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new FighterChangePlace(123)));
    }

    @Test
    void functionalSuccess() throws Exception {
        fight = createFight();
        fighter = player.fighter();

        handlePacket(new FighterChangePlace(123));
        Thread.sleep(5);

        assertEquals(123, fighter.cell().id());
    }
}
