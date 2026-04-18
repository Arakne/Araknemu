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
package fr.quatrevieux.araknemu.game.social.friend;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.social.Friend;
import fr.quatrevieux.araknemu.data.living.repository.social.friend.FriendRepository;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.handler.social.exception.FriendException;

import java.util.Collection;

public class FriendService implements EventsSubscriber {
    private final FriendRepository repository;

    public FriendService(FriendRepository repository) {
        this.repository = repository;
    }

    public Collection<Friend> getFriendList(GameAccount account, boolean isEnemy) {
        return repository.getAll(account, isEnemy);
    }

    public Friend add(GameAccount account, Friend friend, boolean isEnemy) throws FriendException {
        return repository.add(account, friend, isEnemy);
    }

    public boolean has(Friend friend) {
        return repository.has(friend);
    }

    public void remove(Friend friend) { repository.delete(friend); }

    @Override
    public Listener[] listeners() {
        return new Listener[0];
    }
}
