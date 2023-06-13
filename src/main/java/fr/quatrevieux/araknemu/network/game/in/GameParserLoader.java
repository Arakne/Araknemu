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

package fr.quatrevieux.araknemu.network.game.in;

import fr.quatrevieux.araknemu.core.network.parser.ParserLoader;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;
import fr.quatrevieux.araknemu.network.game.in.account.AskBoost;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.in.account.AskGift;
import fr.quatrevieux.araknemu.network.game.in.account.AskRandomName;
import fr.quatrevieux.araknemu.network.game.in.account.AskRegionalVersion;
import fr.quatrevieux.araknemu.network.game.in.account.ChoosePlayingCharacter;
import fr.quatrevieux.araknemu.network.game.in.account.ClientUid;
import fr.quatrevieux.araknemu.network.game.in.account.DeleteCharacterRequest;
import fr.quatrevieux.araknemu.network.game.in.account.LoginToken;
import fr.quatrevieux.araknemu.network.game.in.basic.AskDate;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminCommand;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminMove;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.in.chat.SubscribeChannels;
import fr.quatrevieux.araknemu.network.game.in.chat.UseSmiley;
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
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterReady;
import fr.quatrevieux.araknemu.network.game.in.fight.KickOrLeaveRequestParser;
import fr.quatrevieux.araknemu.network.game.in.fight.ListFightsRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.ShowCellRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.TurnEnd;
import fr.quatrevieux.araknemu.network.game.in.fight.option.BlockSpectatorRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.option.LockTeamRequest;
import fr.quatrevieux.araknemu.network.game.in.fight.option.NeedHelpRequest;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.in.info.ScreenInfo;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectDeleteRequest;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectMoveRequest;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellMove;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellUpgrade;

import java.util.Arrays;
import java.util.Collection;

/**
 * Parser loader for game packets
 */
public final class GameParserLoader implements ParserLoader {
    @Override
    public Collection<SinglePacketParser> load() {
        return Arrays.asList(
            new LoginToken.Parser(),
            new AskCharacterList.Parser(),
            new AskRegionalVersion.Parser(),
            new AddCharacterRequest.Parser(),
            new ChoosePlayingCharacter.Parser(),
            new ClientUid.Parser(),
            new ScreenInfo.Parser(),
            new AskGift.Parser(),
            new CreateGameRequest.Parser(),
            new AskExtraInfo.Parser(),
            new GameActionRequest.Parser(),
            new GameActionAcknowledge.Parser(),
            new GameActionCancel.Parser(),
            new AskDate.Parser(),
            new UseSmiley.Parser(),
            new Ping.Parser(),
            new Message.Parser(),
            new DeleteCharacterRequest.Parser(),
            new SubscribeChannels.Parser(),
            new AskRandomName.Parser(),
            new AdminCommand.Parser(),
            new ObjectMoveRequest.Parser(),
            new AskBoost.Parser(),
            new ObjectDeleteRequest.Parser(),
            new ObjectUseRequest.Parser(),
            new SpellMove.Parser(),
            new SpellUpgrade.Parser(),
            new FighterChangePlace.Parser(),
            new FighterReady.Parser(),
            new TurnEnd.Parser(),
            new QuickPing.Parser(),
            new ListFightsRequest.Parser(),
            new AskFightDetails.Parser(),
            new KickOrLeaveRequestParser(),
            new SetOrientationRequest.Parser(),
            new CreateDialogRequest.Parser(),
            new LeaveDialogRequest.Parser(),
            new ChosenResponse.Parser(),
            new ExchangeRequest.Parser(),
            new LeaveExchangeRequest.Parser(),
            new AcceptExchangeRequest.Parser(),
            new KamasMovement.Parser(),
            new ItemsMovement.Parser(),
            new ExchangeReady.Parser(),
            new BuyRequest.Parser(),
            new SellRequest.Parser(),
            new AdminMove.Parser(),
            new BlockSpectatorRequest.Parser(),
            new LockTeamRequest.Parser(),
            new NeedHelpRequest.Parser(),
            new ShowCellRequest.Parser()
        );
    }
}
