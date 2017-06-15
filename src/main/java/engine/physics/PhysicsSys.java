package engine.physics;

import com.sun.javafx.collections.VetoableListDecorator;
import engine.Component;
import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import engine.maths.Vec2;
import javafx.geometry.Pos;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by haraldvinje on 15-Jun-17.
 */
public class PhysicsSys implements Sys {

    private WorldContainer worldContainer;
    private Set<Integer> physicsEntities;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.worldContainer = wc;
    }

    @Override
    public void update() {
        this.physicsEntities = worldContainer.getEntitiesWithComponentType(PhysicsComp.class);
        updateVelocities();         //accelerating
        applyFriction();            //adding friction acceleration vector
        updatePositions();
    }

    private void applyFriction() {
        for (int entity: physicsEntities){
            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
            Vec2 frictionVector = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getVelocity().negative();
            float frictionConst = physicsComp.getFrictionConst();
            frictionVector.scale(physicsComp.getVelocity().getLength()*frictionConst);
            physicsComp.addVelocity(frictionVector);
        }
    }

    private void updateVelocities(){
        for (int entity: physicsEntities){
            PhysicsComp physicsComp = (PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class);
            Vec2 acceleration = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getAcceleration();
            physicsComp.addVelocity(acceleration);
        }
    }



    private void updatePositions(){
        for (int entity: physicsEntities){
            PositionComp posComp = (PositionComp) worldContainer.getComponent(entity, PositionComp.class);
            Vec2 velocity = ((PhysicsComp) worldContainer.getComponent(entity, PhysicsComp.class)).getVelocity();
            posComp.addVector(velocity);
        }
    }
}
