package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterStateChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendStateTest extends FightBaseCase {
    private Fight fight;
    private SendState listener;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        listener = new SendState(fight);
    }

    @Test
    void onStateAdd() {
        listener.on(new FighterStateChanged(fighter, 5, FighterStateChanged.Type.ADD));

        requestStack.assertLast(ActionEffect.addState(fighter, 5));
    }

    @Test
    void onStateRemove() {
        listener.on(new FighterStateChanged(fighter, 5, FighterStateChanged.Type.REMOVE));

        requestStack.assertLast(ActionEffect.removeState(fighter, 5));
    }

    @Test
    void onStateUpdate() {
        requestStack.clear();
        listener.on(new FighterStateChanged(fighter, 5, FighterStateChanged.Type.UPDATE));

        requestStack.assertEmpty();
    }
}