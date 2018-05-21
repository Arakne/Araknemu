package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendFighterReadyStateTest extends FightBaseCase {
    private SendFighterReadyState listener;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fighter = new PlayerFighter(gamePlayer(true));
        Fight fight = createFight();

        listener = new SendFighterReadyState(fight);
    }

    @Test
    void onFighterReadyStateChanged() {
        listener.on(new FighterReadyStateChanged(fighter));

        requestStack.assertLast(new FighterReadyState(fighter));
    }
}