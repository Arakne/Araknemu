package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import java.util.Collections;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope.EffectScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;

final public class MonsterInvocationHandler implements EffectHandler {
    final private MonsterService monsterService;
    final private Fight fight;
    private int index = 0;
    private boolean initialized = false;

    public MonsterInvocationHandler(MonsterService monsterService, Fight fight) {
        this.monsterService = monsterService;
        this.fight = fight;
    }

    @Override
    public void buff(CastScope cast, EffectScope effect) {
        throw new UnsupportedOperationException("Cannot perform invocation from a buff");
    }

    @Override
    public void handle(CastScope cast, EffectScope effect) {
        initMonstersIDs();
        Monster invoc = monsterService.load(effect.effect().min()).all().get(effect.effect().max() -1);
        MonsterFighter fighter = new MonsterFighter(--index, invoc, fight.turnList().currentFighter().team());

        cast.caster().addInvocation(fighter, cast.target());
        
        fight.send(new ActionEffect(181, cast.caster(), (new AddSprites(Collections.singleton(fighter.sprite()))).toString()));
        fight.send(new ActionEffect(999, cast.caster(), (new FighterTurnOrder(fight.turnList())).toString()));
    }

    private void initMonstersIDs() {
        if(initialized) {
            return;
        }
        
        fight.fighters().forEach(fighter -> {
            if(index > fighter.id()) {
                index = fighter.id();
            }
        });

        initialized = true;
    }
}
