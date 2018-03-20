package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.characteristic.BaseCharacteristics;
import fr.quatrevieux.araknemu.game.player.race.GamePlayerRace;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BaseCharacteristicsTest extends GameBaseCase {
    private Player player;
    private MutableCharacteristics playerStats;
    private BaseCharacteristics baseCharacteristics;

    private ListenerAggregate dispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushRaces()
            .pushSpells()
        ;

        GamePlayerRace race = container.get(PlayerRaceService.class).get(Race.FECA);

        player = new Player(1);

        playerStats = player.stats();

        playerStats.set(Characteristic.STRENGTH, 150);
        playerStats.set(Characteristic.VITALITY, 50);
        playerStats.set(Characteristic.ACTION_POINT, 2);

        baseCharacteristics = new BaseCharacteristics(dispatcher = new DefaultListenerAggregate(), race, player);
    }

    @Test
    void getWillAddRaceAndPlayerStats() {
        assertEquals(0, baseCharacteristics.get(Characteristic.COUNTER_DAMAGE));
        assertEquals(8, baseCharacteristics.get(Characteristic.ACTION_POINT));
        assertEquals(150, baseCharacteristics.get(Characteristic.STRENGTH));
    }

    @Test
    void setWillUpdatePlayerStats() {
        baseCharacteristics.set(Characteristic.INTELLIGENCE, 50);

        assertEquals(50, baseCharacteristics.get(Characteristic.INTELLIGENCE));
        assertEquals(50, playerStats.get(Characteristic.INTELLIGENCE));
    }

    @Test
    void add() {
        AtomicReference<CharacteristicsChanged> ref = new AtomicReference<>();
        dispatcher.add(CharacteristicsChanged.class, ref::set);

        baseCharacteristics.add(Characteristic.STRENGTH, 10);

        assertNotNull(ref.get());
        assertEquals(160, baseCharacteristics.get(Characteristic.STRENGTH));
    }

    @Test
    void apLevel1() {
        player.setLevel(1);
        player.stats().set(Characteristic.ACTION_POINT, 0);

        assertEquals(6, baseCharacteristics.get(Characteristic.ACTION_POINT));
    }

    @Test
    void apLevel100() {
        player.setLevel(100);
        player.stats().set(Characteristic.ACTION_POINT, 0);

        assertEquals(7, baseCharacteristics.get(Characteristic.ACTION_POINT));
    }
}
