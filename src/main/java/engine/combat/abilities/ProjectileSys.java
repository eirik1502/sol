package engine.combat.abilities;

import engine.Sys;
import engine.WorldContainer;
import engine.combat.DamagerComp;

/**
 * Created by eirik on 28.06.2017.
 */
public class ProjectileSys implements Sys {

    private WorldContainer wc;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        for (int entity : wc.getEntitiesWithComponentType(ProjectileComp.class)) {
            updateProjectile(entity);
        }
    }

    private void updateProjectile(int projEntity){
        ProjectileComp projComp = (ProjectileComp) wc.getComponent(projEntity, ProjectileComp.class);
        DamagerComp dmgerComp = (DamagerComp) wc.getComponent(projEntity, DamagerComp.class);

        //deactivate projectile it is told to
        if (projComp.isShouldDeactivateFlag()) {
            projComp.resetShouldDeactivateFlag();

            deactivateProj(projEntity);
            return;
        }

        //deactivate projectile if it delt damage
        if (dmgerComp.hasDeltDamage()) {
            projComp.setShouldDeactivateFlag();
        }

        //decrement lifetime and deactivate if below zero
        projComp.decrementLifeTime();
        //System.out.println("Projectile lifetime: "+projComp.getLifeTime());
        if (projComp.getLifeTime() <= 1) {
            projComp.setShouldDeactivateFlag();
        }
    }

    private void deactivateProj(int projEntity) {
        wc.deactivateEntity(projEntity);
    }

    @Override
    public void terminate() {

    }
}
