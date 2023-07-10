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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.object;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.AddZones;
import fr.quatrevieux.araknemu.util.Asserter;

/**
 * Add a glyph on the target cell
 * The glyph will trigger the related spell on end turn of each fighter in its area
 *
 * Arguments:
 * - min: spell ID
 * - max: spell level
 * - special: glyph color
 *
 * This effect will also take in account:
 * - The effect duration as glyph duration
 * - The effect area as glyph size
 *
 * Remaining turns of the glyph are decreased at the start of the caster turn.
 * So it will disappear just before the caster turn.
 *
 * @see EndTurnGlyph
 */
public final class AddEndTurnGlyphHandler implements EffectHandler {
    private final Fight fight;
    private final SpellService spellService;

    public AddEndTurnGlyphHandler(Fight fight, SpellService spellService) {
        this.fight = fight;
        this.spellService = spellService;
    }

    @Override
    public void handle(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        throw new UnsupportedOperationException("AddEndTurnGlyphHandler must have a duration");
    }

    @Override
    public void buff(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        final Fighter caster = cast.caster();
        final SpellEffect spellEffect = effect.effect();

        final EndTurnGlyph glyph = new EndTurnGlyph(
            fight,
            cast.target(),
            caster,
            spellEffect.area().size(),
            spellEffect.special(),
            spellEffect.duration(),
            spellService
                .get(spellEffect.min())
                .level(Asserter.assertPositive(spellEffect.max()))
        );

        fight.map().objects().add(glyph);
        fight.send(ActionEffect.packet(caster, new AddZones(glyph)));
    }
}
