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

package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.handler.EnsureFightingOrSpectator;
import fr.quatrevieux.araknemu.game.handler.EnsureInactiveFight;
import fr.quatrevieux.araknemu.game.handler.account.BoostCharacteristic;
import fr.quatrevieux.araknemu.game.handler.chat.SaveSubscription;
import fr.quatrevieux.araknemu.game.handler.chat.SendMessage;
import fr.quatrevieux.araknemu.game.handler.chat.SpamCheckAttachment;
import fr.quatrevieux.araknemu.game.handler.fight.LeaveFight;
import fr.quatrevieux.araknemu.game.handler.fight.LeaveSpectatorFight;
import fr.quatrevieux.araknemu.game.handler.social.enemy.EnemyAdd;
import fr.quatrevieux.araknemu.game.handler.social.enemy.EnemyList;
import fr.quatrevieux.araknemu.game.handler.social.enemy.EnemyRemove;
import fr.quatrevieux.araknemu.game.handler.social.friend.FriendAdd;
import fr.quatrevieux.araknemu.game.handler.social.friend.FriendList;
import fr.quatrevieux.araknemu.game.handler.social.friend.FriendRemove;
import fr.quatrevieux.araknemu.game.handler.game.CreateGame;
import fr.quatrevieux.araknemu.game.handler.object.MoveObject;
import fr.quatrevieux.araknemu.game.handler.object.RemoveObject;
import fr.quatrevieux.araknemu.game.handler.spell.MoveSpell;
import fr.quatrevieux.araknemu.game.handler.spell.UpgradeSpell;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.social.friend.FriendService;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Loader for playing packets
 */
public final class PlayingLoader implements Loader {
    @Override
    @SuppressWarnings("unchecked")
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        return new PacketHandler[] {
            new CreateGame(
                container.get(ExplorationService.class)
            ),
            new SendMessage(
                container.get(ChatService.class),
                container.get(SpamCheckAttachment.Key.class)
            ),
            new SaveSubscription(),
            new EnsureInactiveFight<>(new MoveObject()),
            new EnsureInactiveFight<>(new RemoveObject()),
            new EnsureInactiveFight<>(new BoostCharacteristic()),
            new EnsureInactiveFight<>(new UpgradeSpell()),
            new MoveSpell(),
            new EnsureFightingOrSpectator(new LeaveFight(), new LeaveSpectatorFight()), // @todo ? how to handle this case
            new FriendAdd(container.get(FriendService.class), container.get(PlayerService.class)),
            new FriendList(container.get(FriendService.class), container.get(AccountService.class), container.get(PlayerService.class)),
            new FriendRemove(container.get(FriendService.class), container.get(AccountService.class)),
            new EnemyAdd(container.get(FriendService.class), container.get(PlayerService.class)),
            new EnemyList(container.get(FriendService.class), container.get(AccountService.class), container.get(PlayerService.class)),
            new EnemyRemove(container.get(FriendService.class), container.get(AccountService.class)),
        };
    }
}
