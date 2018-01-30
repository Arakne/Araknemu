package fr.quatrevieux.araknemu.network.game.out.area;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;

import java.util.Collection;

/**
 * List all available subAreas
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Subareas.as#L19
 */
final public class SubAreaList {
    final private Collection<SubArea> subAreas;

    public SubAreaList(Collection<SubArea> subAreas) {
        this.subAreas = subAreas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("al");

        for (SubArea subArea : subAreas) {
            if(subArea.alignment() == Alignment.NONE) {
                continue;
            }

            sb.append('|').append(subArea.id()).append(';').append(subArea.alignment().id());
        }

        return sb.toString();
    }
}
