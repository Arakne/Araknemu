package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendFighterDieTest extends FightBaseCase {
    private Fight fight;
    private SendFighterDie listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendFighterDie(
            fight = createFight()
        );
    }

    @Test
    void onFighterDie() {
        listener.on(new FighterDie(player.fighter(), other.fighter()));

        requestStack.assertLast(ActionEffect.fighterDie(other.fighter(), player.fighter()));
    }
}