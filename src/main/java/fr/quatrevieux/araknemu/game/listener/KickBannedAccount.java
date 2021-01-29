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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener;

import fr.quatrevieux.araknemu.common.account.banishment.event.AccountBanned;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.network.out.ServerMessage;

/**
 * Kick the banned account
 */
final public class KickBannedAccount implements Listener<AccountBanned<GameAccount>> {
    @Override
    public void on(AccountBanned<GameAccount> event) {
        event.account().kick(
            ServerMessage.kick(
                event.banisher().map(GameAccount::pseudo).orElse("system"),
                event.entry().cause()
            )
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<AccountBanned<GameAccount>> event() {
        return (Class<AccountBanned<GameAccount>>) (Class<?>) AccountBanned.class;
    }
}
