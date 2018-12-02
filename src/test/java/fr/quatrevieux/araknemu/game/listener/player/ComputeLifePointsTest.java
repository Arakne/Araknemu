package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComputeLifePointsTest extends GameBaseCase {
    private GamePlayer player;
    private ComputeLifePoints listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new ComputeLifePoints(
            player = gamePlayer()
        );
    }

    @Test
    void onCharacteristicsChanged() {
        player.properties().characteristics().base().set(Characteristic.VITALITY, 100);

        listener.on(new CharacteristicsChanged());

        assertEquals(395, player.properties().life().max());
    }
}