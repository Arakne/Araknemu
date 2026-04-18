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
 * Copyright (c) 2017-2026 Leor Finacre
 */
package fr.quatrevieux.araknemu.game.handler.social.friend;

import fr.quatrevieux.araknemu.data.living.entity.social.Friend;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.handler.AbstractLoggedPacketHandler;
import fr.quatrevieux.araknemu.game.social.friend.FriendService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.social.friend.FriendRemoveRequest;
import fr.quatrevieux.araknemu.network.game.out.social.friend.FriendRemoveResponse;

public class FriendRemove extends AbstractLoggedPacketHandler<FriendRemoveRequest> {
    private final FriendService friendService;
    private final AccountService accountService;

    public FriendRemove(FriendService friendService, AccountService accountService) {
        this.friendService = friendService;
        this.accountService = accountService;
    }

    @Override
    protected void handle(GameSession session, GameAccount account, FriendRemoveRequest packet) throws Exception {
        GameAccount friendAccount = accountService.findByPseudo(packet.accountName()).get();
        Friend f = new Friend(account.id(), account.serverId(), friendAccount.id(), false);
        friendService.remove(f);
        session.send(new FriendRemoveResponse(true));
    }

    @Override
    public Class<FriendRemoveRequest> packet() {
        return FriendRemoveRequest.class;
    }
}
