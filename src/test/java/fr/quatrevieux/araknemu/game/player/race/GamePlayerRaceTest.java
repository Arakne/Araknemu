package fr.quatrevieux.araknemu.game.player.race;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GamePlayerRaceTest extends GameBaseCase {
    private PlayerRaceService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushRaces()
            .pushSpells()
        ;

        service = container.get(PlayerRaceService.class);
    }

    @Test
    void life() {
        GamePlayerRace race = service.get(Race.CRA);

        assertEquals(50, race.life(1));
        assertEquals(55, race.life(2));
        assertEquals(795, race.life(150));
    }

    @Test
    void initiativeBase() {
        GamePlayerRace race = service.get(Race.CRA);

        assertEquals(100, race.initiative(400));
    }

    @Test
    void initiativeSacri() {
        GamePlayerRace race = service.get(Race.SACRIEUR);

        assertEquals(50, race.initiative(400));
    }

    @Test
    void spells() {
        GamePlayerRace race = service.get(Race.FECA);

        List<SpellLevels> levels = new ArrayList<>(race.spells());

        assertEquals(3, levels.get(0).id());
        assertEquals(6, levels.get(1).id());
        assertEquals(17, levels.get(2).id());
    }

    @Test
    void getters() {
        GamePlayerRace race = service.get(Race.ENUTROF);

        assertEquals(Race.ENUTROF, race.race());
        assertEquals("Enutrof", race.name());
        assertEquals(120, race.startDiscernment());
        assertEquals(1000, race.startPods());
    }

    @Test
    void baseStatsLowLevel() {
        GamePlayerRace race = service.get(Race.ENUTROF);

        Characteristics characteristics = race.baseStats(1);

        assertEquals(6, characteristics.get(Characteristic.ACTION_POINT));
        assertEquals(3, characteristics.get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, characteristics.get(Characteristic.MAX_SUMMONED_CREATURES));

        assertSame(characteristics, race.baseStats(50));
    }

    @Test
    void baseStatsHighLevel() {
        GamePlayerRace race = service.get(Race.ENUTROF);

        Characteristics characteristics = race.baseStats(100);

        assertEquals(7, characteristics.get(Characteristic.ACTION_POINT));
        assertEquals(3, characteristics.get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, characteristics.get(Characteristic.MAX_SUMMONED_CREATURES));

        assertSame(characteristics, race.baseStats(150));
    }
}
