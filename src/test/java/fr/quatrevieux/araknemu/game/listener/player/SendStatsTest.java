package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendStatsTest extends FightBaseCase {
    private SendStats listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendStats(gamePlayer(true));
        requestStack.clear();
    }

    @Test
    void onCharacteristicsChanged() throws SQLException, ContainerException {
        listener.on(new CharacteristicsChanged());

        requestStack.assertLast(
            new Stats(gamePlayer().properties())
        );
    }

    @Test
    void onCharacteristicsChangedWithFighter() throws Exception {
        createFight();
        PlayerFighter fighter = player.fighter();

        fighter.init();
        fighter.life().alter(fighter, -100);

        listener.on(new CharacteristicsChanged());

        requestStack.assertLast(new Stats(fighter.properties()));
    }
}
