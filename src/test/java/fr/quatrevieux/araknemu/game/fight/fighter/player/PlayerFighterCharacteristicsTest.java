package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFighterCharacteristicsTest extends GameBaseCase {
    private PlayerCharacteristics baseCharacteristics;
    private PlayerFighterCharacteristics fighterCharacteristics;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        baseCharacteristics = gamePlayer(true).characteristics();
        fighterCharacteristics = new PlayerFighterCharacteristics(baseCharacteristics);
    }

    @Test
    void initiative() {
        assertEquals(baseCharacteristics.initiative(), fighterCharacteristics.initiative());
    }

    @Test
    void get() {
        assertEquals(baseCharacteristics.get(Characteristic.ACTION_POINT), fighterCharacteristics.get(Characteristic.ACTION_POINT));
        assertEquals(baseCharacteristics.get(Characteristic.STRENGTH), fighterCharacteristics.get(Characteristic.STRENGTH));
    }
}