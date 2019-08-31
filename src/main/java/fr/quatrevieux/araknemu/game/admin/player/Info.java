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

package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.List;

/**
 * Info command for a player
 */
final public class Info extends AbstractCommand {
    final private GamePlayer player;

    public Info(GamePlayer player) {
        this.player = player;
    }

    @Override
    protected void build(Builder builder) {
        builder
            .description("Display information on the selected player")
            .requires(Permission.MANAGE_PLAYER)
            .help("info")
        ;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        performer.success("Player info : {}", player.name());
        performer.success("==============================");
        performer.info("ID:    {}", player.id());
        performer.info("Name:  {}", player.name());
        performer.info("Level: {}", player.properties().experience().level());
        performer.info("Race:  {}", player.race().name());
        performer.info("Sex:   {}", player.spriteInfo().sex());
        performer.info("GfxID: {}", player.spriteInfo().gfxId());
        performer.success("==============================");
    }
}
