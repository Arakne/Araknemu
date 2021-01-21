package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

@FunctionalInterface
public interface InvocationFighterFactory {
    
    public Fighter create(int id);
}
