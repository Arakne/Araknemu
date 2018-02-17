package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BaseCharacteristicsTest {
    private MutableCharacteristics raceStats;
    private MutableCharacteristics playerStats;
    private BaseCharacteristics baseCharacteristics;

    private ListenerAggregate dispatcher;

    @BeforeEach
    void setUp() {
        raceStats = new DefaultCharacteristics();

        raceStats.set(Characteristic.ACTION_POINT, 6);
        raceStats.set(Characteristic.MOVEMENT_POINT, 3);
        raceStats.set(Characteristic.DISCERNMENT, 100);
        raceStats.set(Characteristic.MAX_SUMMONED_CREATURES, 1);
        raceStats.set(Characteristic.INITIATIVE, 1);

        playerStats = new DefaultCharacteristics();

        playerStats.set(Characteristic.STRENGTH, 150);
        playerStats.set(Characteristic.VITALITY, 50);
        playerStats.set(Characteristic.ACTION_POINT, 2);

        baseCharacteristics = new BaseCharacteristics(
            dispatcher = new DefaultListenerAggregate(),
            raceStats,
            playerStats
        );
    }

    @Test
    void getWillAddRaceAndPlayerStats() {
        assertEquals(0, baseCharacteristics.get(Characteristic.COUNTER_DAMAGE));
        assertEquals(100, baseCharacteristics.get(Characteristic.DISCERNMENT));
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
}
