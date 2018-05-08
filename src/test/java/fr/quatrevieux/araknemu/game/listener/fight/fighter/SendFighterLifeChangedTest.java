package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendFighterLifeChangedTest extends FightBaseCase {
    private Fight fight;
    private SendFighterLifeChanged listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendFighterLifeChanged(
            fight = createFight()
        );
    }

    @Test
    void onFighterLifeChanged() {
        listener.on(new FighterLifeChanged(player.fighter(), other.fighter(), -4));

        requestStack.assertLast(ActionEffect.alterLifePoints(other.fighter(), player.fighter(), -4));
    }
}