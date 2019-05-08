package fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.WeaponConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.SendPacket;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

import java.time.Duration;

/**
 * Use weapon / close combat attack
 */
final public class CloseCombat implements Action {
    final private FightTurn turn;
    final private Fighter caster;
    final private FightCell target;
    final private WeaponConstraintsValidator validator;
    final private CriticalityStrategy criticalityStrategy;

    private CloseCombatSuccess result;

    public CloseCombat(FightTurn turn, Fighter caster, FightCell target) {
        this(turn, caster, target, new WeaponConstraintsValidator(), new BaseCriticalityStrategy(caster));
    }

    public CloseCombat(FightTurn turn, Fighter caster, FightCell target, WeaponConstraintsValidator validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.caster = caster;
        this.target = target;
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public boolean validate() {
        Error error = validator.validate(turn, caster.weapon(), target);

        if (error != null) {
            caster.apply(new SendPacket(error));

            return false;
        }

        return true;
    }

    @Override
    public ActionResult start() {
        if (criticalityStrategy.failed(caster.weapon().criticalFailure())) {
            return new CloseCombatFailed(caster);
        }

        return result = new CloseCombatSuccess(
            caster,
            caster.weapon(),
            target,
            criticalityStrategy.hit(caster.weapon().criticalHit())
        );
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
        if (result.critical()) {
            caster.fight().send(ActionEffect.criticalHitCloseCombat(caster));
        }

        turn.points().useActionPoints(caster.weapon().apCost());
        turn.fight().effects().apply(new CastScope(caster.weapon(), caster, target).withEffects(result.effects()));
    }

    @Override
    public void failed() {
        turn.points().useActionPoints(caster.weapon().apCost());
        turn.stop();
    }

    @Override
    public Duration duration() {
        return Duration.ofMillis(500);
    }
}
