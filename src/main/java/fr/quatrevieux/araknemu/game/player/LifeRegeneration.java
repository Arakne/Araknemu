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

package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;

public class LifeRegeneration implements Runnable{
    private boolean run;
        private GamePlayer player;
        private Player entity;
        public LifeRegeneration(GamePlayer player, Player entity) {
            this.entity = entity;
            this.player = player;
            this.run = false;
        }
        @Override
        public void run() {
            if (!this.run) return;

            if (this.player.isFighting() || this.player.properties().life().isFull()) {
                this.player.stopLifeRegeneration();
                return;
            }
            this.entity.setLife(this.entity.life() + 1);
        }
        
        public void setRun(boolean run) {
            this.run = run;
        }
}
