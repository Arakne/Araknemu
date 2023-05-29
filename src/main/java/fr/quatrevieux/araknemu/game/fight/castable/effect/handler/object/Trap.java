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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.AddZones;
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.RemoveZone;
import fr.quatrevieux.araknemu.network.game.out.game.UpdateCells;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Trap object, created by {@link AddTrapHandler}
 *
 * @todo handle trap damage bonus
 */
final class Trap implements BattlefieldObject {
    private static final UpdateCells.PropertyValue<?>[] CELL_PROPERTIES = new UpdateCells.PropertyValue<?>[]{
        UpdateCells.LAYER_2_OBJECT_NUMBER.set(25),
    };

    private final Fight fight;
    private final FightCell cell;
    private final Fighter caster;
    private final @NonNegative int size;
    private final int color;
    private final Spell trapSpell;
    private final Spell effectSpell;
    private boolean visible = false;

    /**
     * @param fight The current fight
     * @param cell The cell where the trap is
     * @param caster The caster of the trap
     * @param size The size of the glyph. 0 is for a single cell trap
     * @param color The color of the trap. Used by the client as layer for identifying the trap
     * @param trapSpell The spell that is creates the current trap (used for "has start trap" message and visual effect)
     * @param effectSpell The spell that is applied when a fighter trigger the trap
     */
    public Trap(Fight fight, FightCell cell, Fighter caster, @NonNegative int size, int color, Spell trapSpell, Spell effectSpell) {
        this.fight = fight;
        this.cell = cell;
        this.caster = caster;
        this.size = size;
        this.color = color;
        this.trapSpell = trapSpell;
        this.effectSpell = effectSpell;
    }

    @Override
    public FightCell cell() {
        return cell;
    }

    @Override
    public Fighter caster() {
        return caster;
    }

    @Override
    public @NonNegative int size() {
        return size;
    }

    @Override
    public int color() {
        return color;
    }

    @Override
    public UpdateCells.PropertyValue<?>[] cellsProperties() {
        return CELL_PROPERTIES;
    }

    @Override
    public void onEnterInArea(Fighter fighter) {
        final FightCastScope castScope = FightCastScope.fromCell(
            effectSpell,
            caster,
            cell,
            cell,
            effectSpell.effects()
        );

        fight.send(ActionEffect.trapTriggered(caster, fighter, cell, trapSpell));

        // Remove the trap before applying the effect, so double repulsive traps can't create infinite loop
        fight.map().objects().remove(this);

        fight.effects().apply(castScope);
    }

    @Override
    public void disappear() {
        fight.send(new RemoveZone(this));
        fight.send(new UpdateCells(UpdateCells.Data.reset(cell.id())));
    }

    @Override
    public void show(Fighter fighter) {
        visible = true;

        sendPackets(fight, fighter);
    }

    @Override
    public boolean visible(Fighter fighter) {
        // By default, only visible by teammates
        return visible || fighter.team().equals(caster.team());
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public boolean shouldStopMovement() {
        return true;
    }

    /**
     * Send packets for showing the trap to the given destination
     *
     * @param destination The destination. Use a {@link fr.quatrevieux.araknemu.game.fight.team.FightTeam} when the trap is set, and {@link Fight} when the trap is shown
     * @param caster Fighter who trigger the display of the trap (i.e. trap caster or cast of reveal spell)
     */
    public void sendPackets(Sender destination, Fighter caster) {
        destination.send(ActionEffect.packet(caster, new AddZones(this)));
        destination.send(ActionEffect.packet(caster, new UpdateCells(UpdateCells.Data.fromProperties(cell.id(), true, cellsProperties()))));
    }
}
