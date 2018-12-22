package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.SpellCasted;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.world.map.util.CoordinateCell;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

import java.time.Duration;

/**
 * Cast a spell
 */
final public class Cast implements Action {
    final private FightTurn turn;
    final private Fighter caster;
    final private Spell spell;
    final private FightCell target;
    final private SpellConstraintsValidator validator;
    final private CriticalityStrategy criticalityStrategy;

    private CastSuccess result;

    public Cast(FightTurn turn, Fighter caster, Spell spell, FightCell target) {
        this(turn, caster, spell, target, new SpellConstraintsValidator(turn), new BaseCriticalityStrategy(caster));
    }

    public Cast(FightTurn turn, Fighter caster, Spell spell, FightCell target, SpellConstraintsValidator validator, CriticalityStrategy criticalityStrategy) {
        this.turn = turn;
        this.caster = caster;
        this.spell = spell;
        this.target = target;
        this.validator = validator;
        this.criticalityStrategy = criticalityStrategy;
    }

    @Override
    public boolean validate() {
        Error error = spell == null
            ? Error.cantCastNotFound()
            : validator.validate(spell, target)
        ;

        if (error != null) {
            if (caster instanceof Sender) {
                Sender.class.cast(caster).send(error);
            }

            return false;
        }

        return true;
    }

    @Override
    public ActionResult start() {
        if (!target.equals(caster.cell())) {
            caster.setOrientation(
                new CoordinateCell<>(caster.cell())
                    .directionTo(new CoordinateCell<>(target))
            );
        }

        if (criticalityStrategy.failed(spell.criticalFailure())) {
            return new CastFailed(caster, spell);
        }

        return result = new CastSuccess(
            caster,
            spell,
            target,
            criticalityStrategy.hit(spell.criticalHit())
        );
    }

    @Override
    public Fighter performer() {
        return caster;
    }

    @Override
    public ActionType type() {
        return ActionType.CAST;
    }

    @Override
    public void end() {
        if (result.critical()) {
            caster.fight().send(ActionEffect.criticalHitSpell(caster, spell));
        }

        turn.points().useActionPoints(spell.apCost());
        turn.fight().effects().apply(new CastScope(spell, caster, target).withRandomEffects(result.effects()));

        turn.fight().dispatch(new SpellCasted(this));
    }

    @Override
    public void failed() {
        turn.points().useActionPoints(spell.apCost());

        if (spell.endsTurnOnFailure()) {
            turn.stop();
        }
    }

    @Override
    public Duration duration() {
        return Duration.ofMillis(500);
    }

    public Fighter caster() {
        return caster;
    }

    public Spell spell() {
        return spell;
    }

    public FightCell target() {
        return target;
    }
}
