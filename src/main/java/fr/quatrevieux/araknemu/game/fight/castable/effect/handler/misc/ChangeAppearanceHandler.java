/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Handle appearance change effect
 *
 * The new appearance sprite id should be located on "special" effect value.
 * When a negative value is given, the current appearance will be removed if match with the effect value.
 */
public final class ChangeAppearanceHandler implements EffectHandler, BuffHook {
    private final Fight fight;

    public ChangeAppearanceHandler(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void handle(FightCastScope cast, FightCastScope.EffectScope effect) {
        final int newGfxId = effect.effect().special();

        for (FighterData target : effect.targets()) {
            if (newGfxId > 0) {
                // Positive effect : change the appearance
                fight.send(ActionEffect.changeAppearance(cast.caster(), target, newGfxId, 1));
            } else if (-newGfxId == getCurrentGfxId(target)) {
                // Negative effect, and the current appearance match : reset the appearance
                fight.send(ActionEffect.resetAppearance(cast.caster(), target));
            }
        }
    }

    @Override
    public void buff(FightCastScope cast, FightCastScope.EffectScope effect) {
        for (Fighter target : effect.targets()) {
            target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
        }
    }

    @Override
    public void onBuffStarted(Buff buff) {
        final FighterData caster = buff.caster();
        final FighterData target = buff.target();

        // Add 1 turn for duration on self cast
        final int duration = buff.effect().duration() + (caster.equals(target) ? 1 : 0);

        fight.send(ActionEffect.changeAppearance(caster, target, buff.effect().special(), duration));
    }

    /**
     * Get the current active appearance sprite id
     */
    private int getCurrentGfxId(FighterData fighter) {
        int gfxId = fighter.sprite().gfxId();

        for (Buff buff : fighter.buffs()) {
            if (buff.hook() instanceof ChangeAppearanceHandler) {
                gfxId = buff.effect().special();
            }
        }

        return gfxId;
    }
}
