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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.handler.EnsureExploring;
import fr.quatrevieux.araknemu.network.game.in.dialog.ChosenResponse;
import fr.quatrevieux.araknemu.network.game.in.dialog.CreateDialogRequest;
import fr.quatrevieux.araknemu.network.game.in.dialog.LeaveDialogRequest;
import fr.quatrevieux.araknemu.network.game.in.emote.SetOrientationRequest;
import fr.quatrevieux.araknemu.network.game.in.exchange.AcceptExchangeRequest;
import fr.quatrevieux.araknemu.network.game.in.exchange.ExchangeReady;
import fr.quatrevieux.araknemu.network.game.in.exchange.ExchangeRequest;
import fr.quatrevieux.araknemu.network.game.in.exchange.LeaveExchangeRequest;
import fr.quatrevieux.araknemu.network.game.in.exchange.movement.ItemsMovement;
import fr.quatrevieux.araknemu.network.game.in.exchange.movement.KamasMovement;
import fr.quatrevieux.araknemu.network.game.in.exchange.store.BuyRequest;
import fr.quatrevieux.araknemu.network.game.in.exchange.store.SellRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.AskFightDetails;
import fr.quatrevieux.araknemu.network.game.in.fight.ListFightsRequest;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExploringLoaderTest extends LoaderTestCase {
    private ExploringLoader loader;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        loader = new ExploringLoader();
    }

    @Test
    void load() throws ContainerException {
        PacketHandler[] handlers = loader.load(container);

        assertContainsOnly(EnsureExploring.class, handlers);

        assertHandlePacket(AskExtraInfo.class, handlers);
        assertHandlePacket(GameActionCancel.class, handlers);
        assertHandlePacket(ListFightsRequest.class, handlers);
        assertHandlePacket(AskFightDetails.class, handlers);
        assertHandlePacket(SetOrientationRequest.class, handlers);
        assertHandlePacket(CreateDialogRequest.class, handlers);
        assertHandlePacket(LeaveDialogRequest.class, handlers);
        assertHandlePacket(ChosenResponse.class, handlers);
        assertHandlePacket(ExchangeRequest.class, handlers);
        assertHandlePacket(LeaveExchangeRequest.class, handlers);
        assertHandlePacket(AcceptExchangeRequest.class, handlers);
        assertHandlePacket(KamasMovement.class, handlers);
        assertHandlePacket(ItemsMovement.class, handlers);
        assertHandlePacket(ExchangeReady.class, handlers);
        assertHandlePacket(BuyRequest.class, handlers);
        assertHandlePacket(SellRequest.class, handlers);
    }
}
