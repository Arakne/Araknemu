package fr.quatrevieux.araknemu.game.listener.player.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttachFighterTest extends FightBaseCase {
    private AttachFighter listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        listener = new AttachFighter(gamePlayer());
    }

    @Test
    void onFightJoined() throws Exception {
        Fight fight = createFight(false);
        PlayerFighter fighter = makePlayerFighter(gamePlayer());

        listener.on(new FightJoined(fight, fighter));

        assertTrue(gamePlayer().isFighting());
        assertSame(fighter, gamePlayer().fighter());
        assertSame(fighter, gamePlayer().scope());
    }
}
