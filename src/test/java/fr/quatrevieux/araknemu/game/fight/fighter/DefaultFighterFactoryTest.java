package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.PlayerFighterCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightJoined;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.*;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DefaultFighterFactoryTest extends GameBaseCase {
    private DefaultFighterFactory factory;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new DefaultFighterFactory(
            dispatcher = new DefaultListenerAggregate()
        );
    }

    @Test
    void createPlayerFighter() throws SQLException, ContainerException {
        AtomicReference<PlayerFighterCreated> ref = new AtomicReference<>();
        dispatcher.add(PlayerFighterCreated.class, ref::set);

        GamePlayer player = gamePlayer();

        PlayerFighter fighter = factory.create(player);

        assertSame(fighter, ref.get().fighter());
        assertSame(player, fighter.player());

        assertTrue(fighter.dispatcher().has(SendFightJoined.class));
        assertTrue(fighter.dispatcher().has(ApplyEndFightReward.class));
        assertTrue(fighter.dispatcher().has(StopFightSession.class));
        assertTrue(fighter.dispatcher().has(SendFightLeaved.class));
        assertTrue(fighter.dispatcher().has(LeaveOnDisconnect.class));
        assertTrue(fighter.dispatcher().has(ApplyLeaveReward.class));
        assertTrue(fighter.dispatcher().has(SendStats.class));
    }
}