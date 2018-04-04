package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightJoined;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFighterTest extends GameBaseCase {
    private PlayerFighter fighter;
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fighter = new PlayerFighter(
            gamePlayer(true)
        );

        map = container.get(FightService.class).map(
            container.get(ExplorationMapService.class).load(10340)
        );
    }

    @Test
    void player() throws SQLException, ContainerException {
        assertSame(gamePlayer(), fighter.player());
    }

    @Test
    void sprite() {
        assertInstanceOf(PlayerFighterSprite.class, fighter.sprite());
    }

    @Test
    void team() {
        FightTeam team = new SimpleTeam(fighter, new ArrayList<>(), 0);
        fighter.join(team);

        assertSame(team, fighter.team());
    }

    @Test
    void fight() {
        Fight fight = new Fight(new ChallengeType(), map, new ArrayList<>());

        fighter.setFight(fight);

        assertSame(fight, fighter.fight());
    }

    @Test
    void moveFirstTime() {
        fighter.move(map.get(123));

        assertSame(map.get(123), fighter.cell());
        assertSame(fighter, map.get(123).fighter().get());
    }

    @Test
    void moveWillLeaveLastCell() {
        fighter.move(map.get(123));
        fighter.move(map.get(124));

        assertSame(map.get(124), fighter.cell());
        assertSame(fighter, map.get(124).fighter().get());

        assertFalse(map.get(123).fighter().isPresent());
    }

    @Test
    void send() {
        fighter.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void dispatcher() {
        assertTrue(fighter.dispatcher().has(SendFightJoined.class));
    }

    @Test
    void setReady() {
        Fight fight = new Fight(new ChallengeType(), map, new ArrayList<>());
        fighter.setFight(fight);

        AtomicReference<FighterReadyStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterReadyStateChanged.class, ref::set);

        fighter.setReady(true);
        assertTrue(fighter.ready());
        assertSame(fighter, ref.get().fighter());
        assertTrue(ref.get().ready());
    }

    @Test
    void unsetReady() {
        Fight fight = new Fight(new ChallengeType(), map, new ArrayList<>());
        fighter.setFight(fight);
        fighter.setReady(true);

        AtomicReference<FighterReadyStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterReadyStateChanged.class, ref::set);

        fighter.setReady(false);
        assertFalse(fighter.ready());
        assertSame(fighter, ref.get().fighter());
        assertFalse(ref.get().ready());
    }
}
