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
package fr.quatrevieux.araknemu.game.handler.social.enemy;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.social.Friend;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.handler.AbstractLoggedPacketHandler;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.social.friend.FriendService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.social.enemy.EnemyListRequest;
import fr.quatrevieux.araknemu.network.game.out.social.enemy.EnemyListResponse;

import java.util.ArrayList;
import java.util.Collection;

public class EnemyList extends AbstractLoggedPacketHandler<EnemyListRequest> {

    private final FriendService friendService;
    private final AccountService accountService;
    private final PlayerService playerService;

    public EnemyList(FriendService friendService, AccountService accountService, PlayerService playerService) {
        this.friendService = friendService;
        this.accountService = accountService;
        this.playerService = playerService;
    }

    @Override
    protected void handle(GameSession session, GameAccount account, EnemyListRequest packet) throws Exception {
        Collection<Account> accounts = new ArrayList<>();
        Collection<Friend> friends = friendService.getFriendList(account, true);
        for (Friend friend : friends) {
            accounts.add(accountService.findById(friend.contactId()));
        }
        session.send(new EnemyListResponse(accounts, playerService));
    }

    @Override
    public Class<EnemyListRequest> packet() {
        return EnemyListRequest.class;
    }
}