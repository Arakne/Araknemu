package fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastSuccess;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;

import java.time.Duration;

/**
 * Use weapon / close combat attack
 */
final public class CloseCombat implements Action {
    final private FightTurn turn;
    final private Fighter caster;
    final private FightCell target;
    final private CriticalityStrategy criticalityStrategy;

    private CastSuccess result;

    public CloseCombat(FightTurn turn, Fighter caster, FightCell target) {
        this(turn, caster, target, new BaseCriticalityStrategy(caster));
    }

    public CloseCombat(FightTurn turn, Fighter caster, FightCell target, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.caster = caster;
        this.target = target;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public boolean validate() {
//        Error error = spell == null
//            ? Error.cantCastNotFound()
//            : validator.validate(spell, target)
//        ;
//
//        if (error != null) {
//            if (caster instanceof Sender) {
//                Sender.class.cast(caster).send(error);
//            }
//
//            return false;
//        }

        return true;
    }

    @Override
    public ActionResult start() {
//        if (criticalityStrategy.failed(spell.criticalFailure())) {
//            return new CastFailed(caster, spell);
//        }
//
//        return result = new CastSuccess(
//            caster,
//            spell,
//            target,
//            criticalityStrategy.hit(spell.criticalHit())
//        );

        return null;
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public ActionType type() {
        return ActionType.CLOSE_COMBAT;
    }

    @Override
    public void end() {
//        if (result.critical()) {
//            caster.fight().send(ActionEffect.criticalHitSpell(caster, spell));
//        }
//
//        turn.points().useActionPoints(spell.apCost());
//
//        for (SpellEffect effect : result.effects()) {
//            turn.fight().effects().apply(caster, spell, effect, target);
//        }
    }

    @Override
    public void failed() {
//        turn.points().useActionPoints(spell.apCost());
//
//        if (spell.endsTurnOnFailure()) {
//            turn.stop();
//        }
    }

    @Override
    public Duration duration() {
        return Duration.ofMillis(500);
    }
}
