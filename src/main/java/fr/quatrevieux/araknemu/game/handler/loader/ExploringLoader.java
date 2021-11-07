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
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.map.GeolocationService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.handler.EnsureExploring;
import fr.quatrevieux.araknemu.game.handler.basic.admin.GoToGeolocation;
import fr.quatrevieux.araknemu.game.handler.dialog.PerformResponseAction;
import fr.quatrevieux.araknemu.game.handler.dialog.StartDialog;
import fr.quatrevieux.araknemu.game.handler.dialog.StopDialog;
import fr.quatrevieux.araknemu.game.handler.emote.ChangeOrientation;
import fr.quatrevieux.araknemu.game.handler.exchange.AcceptExchange;
import fr.quatrevieux.araknemu.game.handler.exchange.AskExchange;
import fr.quatrevieux.araknemu.game.handler.exchange.LeaveExchange;
import fr.quatrevieux.araknemu.game.handler.exchange.StartExchange;
import fr.quatrevieux.araknemu.game.handler.exchange.movement.SetExchangeItems;
import fr.quatrevieux.araknemu.game.handler.exchange.movement.SetExchangeKamas;
import fr.quatrevieux.araknemu.game.handler.exchange.store.BuyItem;
import fr.quatrevieux.araknemu.game.handler.exchange.store.SellItem;
import fr.quatrevieux.araknemu.game.handler.fight.ListFights;
import fr.quatrevieux.araknemu.game.handler.fight.ShowFightDetails;
import fr.quatrevieux.araknemu.game.handler.game.CancelGameAction;
import fr.quatrevieux.araknemu.game.handler.game.LoadExtraInfo;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Loader for exploration packets
 */
public final class ExploringLoader extends AbstractLoader {
    public ExploringLoader() {
        super(EnsureExploring::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new LoadExtraInfo(container.get(FightService.class)),
            new CancelGameAction(),
            new ListFights(container.get(FightService.class)),
            new ShowFightDetails(container.get(FightService.class)),
            new ChangeOrientation(),
            new StartDialog(),
            new StopDialog(),
            new PerformResponseAction(),
            new AskExchange(container.get(ExchangeFactory.class)),
            new LeaveExchange(),
            new StartExchange(),
            new SetExchangeKamas(),
            new SetExchangeItems(),
            new AcceptExchange(),
            new BuyItem(),
            new SellItem(),
            new GoToGeolocation(container.get(GeolocationService.class)),
        };
    }
}
