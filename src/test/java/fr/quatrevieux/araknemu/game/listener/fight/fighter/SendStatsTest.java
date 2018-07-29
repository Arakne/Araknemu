package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendStatsTest extends FightBaseCase {
    private SendStats listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        listener = new SendStats(player.fighter());
    }

    @Test
    void onCharacteristicChanged() {
        listener.on(new FighterCharacteristicChanged(Characteristic.STRENGTH, 10));

        requestStack.assertLast(new Stats(player.fighter().properties()));
    }
}