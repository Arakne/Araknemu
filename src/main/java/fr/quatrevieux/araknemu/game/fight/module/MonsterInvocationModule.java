package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations.MonsterInvocationHandler;
import fr.quatrevieux.araknemu.game.monster.MonsterService;

public class MonsterInvocationModule implements FightModule {
    final private MonsterService monsterService;
    final private Fight fight;

    public MonsterInvocationModule(MonsterService monsterService, Fight fight) {
        this.monsterService = monsterService;
        this.fight = fight;
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(181, new MonsterInvocationHandler(monsterService, fight));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[0];
    }
    
}
