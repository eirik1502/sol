package engine.combat.abilities;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.physics.PhysicsComp;
import javafx.geometry.Pos;
import utils.maths.Vec2;

import java.awt.geom.RoundRectangle2D;
import java.util.Set;

/**
 * Created by haraldvinje on 21-Jun-17.
 */
public class AbilitySys implements Sys {

    WorldContainer wc;

    @Override

    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        Set<Integer> abilityCompEntities = wc.getEntitiesWithComponentType(AbilityComp.class);

        for (int entity: abilityCompEntities){
            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
            RotationComp rotComp = (RotationComp) wc.getComponent(entity, RotationComp.class);
            AbilityComp abComp = (AbilityComp) wc.getComponent(entity, AbilityComp.class);

            if (abComp.isAbortExecution()) {
                abComp.resetAbortExecution();
                abortAbilityExecution(abComp);
            }

            abComp.streamAbilities().forEach(a -> updateMeleeAbility(entity, a, abComp, posComp, rotComp ) );
        }
    }

    @Override
    public void terminate() {

    }

    private void abortAbilityExecution(AbilityComp abComp) {
        if (abComp.getOccupiedBy() != null) {
            MeleeAbility ab = (MeleeAbility)abComp.getOccupiedBy();

            //deactivate hitbox
            //wc.deactivateEntity(ab.getHitboxEntity());
            ab.setRecharging(false);

            abComp.setOccupiedBy(null);
        }
    }

    private void updateMeleeAbility(int entity, Ability ability, AbilityComp abComp, PositionComp posComp, RotationComp rotComp){

        int startupTime = ability.getStartupTime();
        int effectTime = ability.getEffectTime();
        int endingLagTime = ability.getEndlagTime();
        int rechargeTime = ability.getRechargeTime();


        //move to next frame. Even thoug it is not executing
        ability.counter++;

        //if this ability is recharging, continue recharging and do nothing else
        if (! ability.isRecharging()) {

            //if no ability is executing, check if this one should be executed
            if (abComp.getOccupiedBy() == null) {
                //is ability is requested, execute it
                if (ability.isRequestingExecution()) {

                    System.out.println("Activating ability");

                    startExecution(abComp, ability);
                }
            }

            //if this ability should execute, do it
            if (abComp.getOccupiedBy() == ability) {

                if (ability.counter < startupTime) {
                    //do nothing, but keeps the flow straight
                } else if (ability.counter == startupTime) {
                    startEffect(ability, entity);
                } else if (ability.counter < startupTime + effectTime) {
                    duringEffect(ability, entity);
                } else if (ability.counter == startupTime + effectTime) {
                    endEffect(ability, entity);
                } else if (ability.counter == startupTime + effectTime + endingLagTime) {
                    endExecution(abComp, ability);
                }

            }
        }

        //cannot use else, because rechargeTime may be 0
        if (ability.counter == startupTime + effectTime + endingLagTime + rechargeTime) {
            endRecharge(ability);
        }

        ability.setRequestExecution(false); //checking if a request is made each frame.
    }


    private void startExecution(AbilityComp abComp, Ability ability) {
        abComp.setOccupiedBy(ability);
        ability.counter = 0;
    }

    private void startEffect(Ability ability, int entity){
        ability.startEffect(wc, entity);
    }


    private void duringEffect(Ability ability, int entity ){
        ability.duringEffect(wc, entity);
    }

    private void endEffect(Ability ability, int entity){
        ability.endEffect(wc, entity);
    }

    private void endExecution(AbilityComp abComp, Ability ability) {
        ability.setRecharging(true);
        abComp.setOccupiedBy(null); //release abComp
    }

    private void endRecharge(Ability ability){
        ability.setRecharging(false);

    }



}
