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
                this.player.stopLifeRegen();
                return;
            }
            this.entity.setLife(this.entity.life() + 1);
        }
        
        public void setRun(boolean run) {
            this.run = run;
        }
}
