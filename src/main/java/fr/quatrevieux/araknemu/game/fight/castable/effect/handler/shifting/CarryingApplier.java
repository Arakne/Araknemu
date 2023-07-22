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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Apply carry and throw effect
 */
public final class CarryingApplier {
    private final Fight fight;

    /**
     * State to apply to the fighter who carry another fighter
     */
    private final int carryingState;

    /**
     * State to apply to the fighter who is carried
     */
    private final int carriedState;

    /**
     * @param fight The fight
     * @param carryingState The state to apply to the fighter who carry another fighter
     * @param carriedState The state to apply to the fighter who is carried
     */
    public CarryingApplier(Fight fight, int carryingState, int carriedState) {
        this.fight = fight;
        this.carryingState = carryingState;
        this.carriedState = carriedState;
    }

    /**
     * Carry a fighter
     * This method will do nothing if the fighter is already carried, or if the carrier is already carrying
     *
     * @param carrier The fighter who try to carry (spell caster=
     * @param carried The fighter to carry
     */
    public void carry(Fighter carrier, Fighter carried) {
        // Ignore if already carried
        if (carrier.attachment(Attachment.class) != null || carried.attachment(Attachment.class) != null) {
            return;
        }

        final Attachment attachment = new Attachment(carried, carrier);

        carried.setCell(carrier.cell());

        carrier.states().push(carryingState);
        carried.states().push(carriedState);

        carrier.attach(attachment);
        carried.attach(attachment);

        fight.send(new ActionEffect(50, carrier, carried.id()));
    }

    /**
     * Throw a carried fighter to a target cell
     *
     * If the carrier is not carrying a fighter, this method will do nothing
     * If on success, the carried fighter will be moved to the target cell, and all states and attachments will be removed
     *
     * @param carrier The fighter who throw
     * @param target The target cell
     */
    public void throwCarriedToCell(Fighter carrier, FightCell target) {
        final Attachment attachment = carrier.detach(Attachment.class);

        if (attachment == null) {
            return;
        }

        final Fighter carried = attachment.carried;
        carried.detach(Attachment.class);

        carrier.states().remove(carryingState);
        carried.states().remove(carriedState);

        carried.move(target);

        fight.send(new ActionEffect(51, carrier, target.id()));
    }

    /**
     * Try to stop carrying effect
     * This method will do nothing if the fighter is not carrying or carried
     * If the carried fighter is not on a cell, added to its current cell, triggering a {@link fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved} event
     *
     * @param fighter The carrier or the carried fighter
     */
    public void stop(Fighter fighter) {
        final Attachment attachment = fighter.attachment(Attachment.class);

        if (attachment == null) {
            return;
        }

        stop(attachment);
    }

    /**
     * Synchronize the cells between the carrier and the carried after a move
     * This method will do nothing if the fighter is not carrying or carried
     *
     * If the carrier is moved, the carried will be moved to the same cell
     * If the carried is moved, the carrying effect will be stopped
     *
     * @param fighter The fighter who moved. Can be the carrier or the carried.
     */
    public void synchronizeMove(Fighter fighter) {
        final Attachment attachment = fighter.attachment(Attachment.class);

        if (attachment == null) {
            return;
        }

        final Fighter carried = attachment.carried;
        final Fighter carrier = attachment.carrier;

        if (fighter == carrier) {
            carried.setCell(carrier.cell());
        }

        if (fighter == carried) {
            stop(attachment);
        }
    }

    /**
     * Check if the carrying effect is active on the given fighter
     * This method will return false if the fighter is not carrying or carried
     *
     * @param fighter The fighter to check
     *
     * @return true if the fighter is carrying or carried
     */
    public boolean active(Fighter fighter) {
        return fighter.attachment(Attachment.class) != null;
    }

    private void stop(Attachment attachment) {
        final Fighter carried = attachment.carried;
        final Fighter carrier = attachment.carrier;

        carrier.detach(Attachment.class);
        carried.detach(Attachment.class);

        carrier.states().remove(carryingState);
        carried.states().remove(carriedState);

        if (!carried.dead()) {
            final FightCell cell = carried.cell();

            if (!cell.hasFighter()) {
                carried.move(cell);
            }
        }
    }

    private static final class Attachment {
        private final Fighter carried;
        private final Fighter carrier;

        public Attachment(Fighter carried, Fighter carrier) {
            this.carried = carried;
            this.carrier = carrier;
        }
    }
}
