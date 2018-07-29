package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerFighterPropertiesTest extends FightBaseCase {
    private PlayerFighterProperties properties;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight();
        properties = new PlayerFighterProperties(player.fighter(), player);
    }

    @Test
    void getters() {
        assertInstanceOf(PlayerFighterCharacteristics.class, properties.characteristics());
        assertInstanceOf(PlayerFighterLife.class, properties.life());
        assertEquals(player.spells(), properties.spells());
        assertEquals(player.experience(), properties.experience());
    }
}
