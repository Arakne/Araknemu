package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearFighterTest extends FightBaseCase {
    private Fight fight;
    private ClearFighter listener;
    private Fighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new ClearFighter();
        fighter = player.fighter();
    }

    @Test
    void onFighterRemoved() {
        listener.on(new FighterRemoved(fighter, fight));

        assertFalse(fighter.cell().fighter().isPresent());
    }
}
