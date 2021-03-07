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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter;

import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Resolve the bank cost
 */
public final class BankCostResolver implements VariableResolver {
    private final BankService service;

    public BankCostResolver(BankService service) {
        this.service = service;
    }

    @Override
    public String name() {
        return "bankCost";
    }

    @Override
    public Object value(ExplorationPlayer player) {
        return service.cost(player.account());
    }
}
