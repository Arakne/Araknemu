package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

class SendNewFighterTest extends FightBaseCase {
    private Fight fight;
    private SendNewFighter listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight(true);
        listener = new SendNewFighter(fight);
    }

    @Test
    void onFighterAdded() throws SQLException, ContainerException, JoinFightException {
        PlayerFighter fighter = makePlayerFighter(makeSimpleGamePlayer(10));
        fight.team(0).join(fighter);
        fighter.move(fight.map().get(123));

        requestStack.clear();

        listener.on(new FighterAdded(fighter));
        requestStack.assertLast(new AddSprites(Collections.singleton(fighter.sprite())));
    }
}
