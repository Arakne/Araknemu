package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.invocations;

import java.util.Collections;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope.EffectScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
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
        addMonsterToFight(cast, effect); // sadida lvl 100 puppet hit here
    }

    @Override
    public void handle(CastScope cast, EffectScope effect) {
        addMonsterToFight(cast, effect); // normal invocations
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

    private void addMonsterToFight(CastScope cast, EffectScope effect) {
        initMonstersIDs();
        Monster invoc = monsterService.load(effect.effect().min()).all().get(effect.effect().max() -1);
        MonsterFighter fighter = new MonsterFighter(--index, invoc, fight.turnList().currentFighter().team());

        fighter.invocationIntoFight(fight, cast.target());
        fighter.setInvoker((Fighter)cast.caster());
        fight.turnList().currentFighter().team().join(fighter);
        fight.turnList().add(fighter);

        fighter.init();

        fight.send(new ActionEffect(181, cast.caster(), (new AddSprites(Collections.singleton(fighter.sprite()))).toString()));
        fight.send(new ActionEffect(999, cast.caster(), (new FighterTurnOrder(fight.turnList())).toString()));
    }
}
