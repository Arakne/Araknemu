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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.constant.Emote;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.emote.EmoteBook;
import fr.quatrevieux.araknemu.network.game.in.emote.SetEmoteRequest;
import org.kohsuke.args4j.Argument;

public class LearnEmote extends AbstractCommand<LearnEmote.Arguments> {
    GamePlayer player;

    public LearnEmote(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(AbstractCommand<Arguments>.Builder builder) {
        builder
            .help(
        formatter -> formatter
                .description("Add the given emote to the player")
                .synopsis("learnemote")
                .example("@John learnemote 19", "John will learn the emote rest ")
            )
            .requires(Permission.MANAGE_PLAYER)
            .arguments(Arguments::new)
        ;
    }

    @Override
    public String name() {
        return "learnemote";
    }

    @Override
    public void execute(AdminPerformer performer, Arguments arguments) throws AdminException {
        final Emote emoteToLearn;

        emoteToLearn = Emote.fromId(arguments.emoteId);
        if(emoteToLearn == Emote.NONE) {
            performer.error("Emote {} not found", arguments.emoteId);
            return;
        }
        
        final EmoteBook emote = player.getEmotes();

        if(!emote.canLearn(emoteToLearn)){
            performer.error("You already know this emote ({})", arguments.emoteId);
            return;
        }

        emote.learn(emoteToLearn);
        performer.success("The emote {} ({}) has been learned", emoteToLearn.name(), arguments.emoteId);
    }

    public static final class Arguments {
        @Argument(required = true, metaVar = "EMOTEID", usage = "The emote ID to learn.")
        private int emoteId;
    }
}
