package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendFighterRemovedTest extends FightBaseCase {
    private Fight fight;
    private SendFighterRemoved listener;
    private Fighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new SendFighterRemoved(fight);
        fighter = makePlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(fighter, fight.team(0));

        requestStack.clear();
    }

    @Test
    void onFighterRemoved() {
        listener.on(new FighterRemoved(fighter, fight));

        requestStack.assertLast(new RemoveSprite(fighter.sprite()));
    }
}