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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Info command for a player
 */
public final class Info extends AbstractCommand<Void> {
    private final GamePlayer player;

    public Info(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Display information on the selected player")
            .help(
                formatter -> formatter
                    .synopsis("[context] info")
                    .line("<i>Note: this command takes no arguments, the account is only resolved by the context</i>")

                    .example("${player:Alan} info", "Display information about the player Alan")
                    .example("info", "Display self player information")
            )
            .requires(Permission.MANAGE_PLAYER)
        ;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public void execute(AdminPerformer performer, Void arguments) {
        performer.success("Player info : {}", player.name());
        performer.success("==============================");
        performer.info("ID:    {}", player.id());
        performer.info("Name:  {}", player.name());
        performer.info("Level: {}", player.properties().experience().level());
        performer.info("Race:  {}", player.race().name());
        performer.info("Sex:   {}", player.spriteInfo().gender());
        performer.info("GfxID: {}", player.spriteInfo().gfxId());
        performer.success("==============================");
    }
}
