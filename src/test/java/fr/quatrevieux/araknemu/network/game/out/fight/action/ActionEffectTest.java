package fr.quatrevieux.araknemu.network.game.out.fight.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ActionEffectTest {
    @Test
    void usedMovementPoints() {
        Fighter fighter = Mockito.mock(Fighter.class);
        Mockito.when(fighter.id()).thenReturn(123);

        assertEquals(
            "GA;129;123;123,-4",
            ActionEffect.usedMovementPoints(fighter, 4).toString()
        );
    }
}