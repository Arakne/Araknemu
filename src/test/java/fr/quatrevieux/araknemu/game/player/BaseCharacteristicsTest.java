package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseCharacteristicsTest {
    private MutableCharacteristics raceStats;
    private MutableCharacteristics playerStats;
    private BaseCharacteristics baseCharacteristics;

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
}