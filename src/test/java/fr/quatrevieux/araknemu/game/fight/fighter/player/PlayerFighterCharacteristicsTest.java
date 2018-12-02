package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFighterCharacteristicsTest extends FightBaseCase {
    private PlayerCharacteristics baseCharacteristics;
    private PlayerFighterCharacteristics fighterCharacteristics;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        baseCharacteristics = player.properties().characteristics();
        fighterCharacteristics = new PlayerFighterCharacteristics(baseCharacteristics, fighter = player.fighter());
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

    @Test
    void getWithBuff() {
        fighterCharacteristics.alter(Characteristic.STRENGTH, 10);

        assertEquals(10 + baseCharacteristics.get(Characteristic.STRENGTH), fighterCharacteristics.get(Characteristic.STRENGTH));
    }

    @Test
    void delegates() {
        assertEquals(baseCharacteristics.base(), fighterCharacteristics.base());
        assertEquals(baseCharacteristics.stuff(), fighterCharacteristics.stuff());
        assertEquals(baseCharacteristics.feats(), fighterCharacteristics.feats());
        assertEquals(baseCharacteristics.boostPoints(), fighterCharacteristics.boostPoints());
    }

    @Test
    void alter() {
        AtomicReference<FighterCharacteristicChanged> ref = new AtomicReference<>();
        fighter.dispatcher().add(FighterCharacteristicChanged.class, ref::set);

        fighterCharacteristics.alter(Characteristic.STRENGTH, 10);
        assertEquals(10, fighterCharacteristics.boost().get(Characteristic.STRENGTH));

        assertEquals(Characteristic.STRENGTH, ref.get().characteristic());
        assertEquals(10, ref.get().value());

        fighterCharacteristics.alter(Characteristic.STRENGTH, -10);
        assertEquals(0, fighterCharacteristics.boost().get(Characteristic.STRENGTH));

        assertEquals(Characteristic.STRENGTH, ref.get().characteristic());
        assertEquals(-10, ref.get().value());
    }
}
