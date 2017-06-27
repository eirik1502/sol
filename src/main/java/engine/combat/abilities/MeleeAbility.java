package engine.combat.abilities;

import engine.Component;
import engine.PositionComp;
import engine.RotationComp;
import engine.WorldContainer;
import engine.character.UserCharacterInputComp;
import engine.combat.DamagerComp;
import engine.graphics.ColoredMesh;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.physics.*;
import game.GameUtils;
import org.w3c.dom.css.Rect;
import utils.maths.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by eirik on 19.06.2017.
 */
public class MeleeAbility extends Ability{


    private int hitboxEntity;

    private float relativeDistance;
    private float relativeAngle;


    public MeleeAbility(WorldContainer wc, float damage, float knockbackRatio, int startupTime, int activeHitboxTime, int endlagTime, int rechargeTime, Shape hitboxShape, float relativeDistance, float relativeAngle){
        super(wc, damage, knockbackRatio, startupTime, activeHitboxTime, endlagTime, rechargeTime);

        this.relativeDistance = relativeDistance;
        this.relativeAngle = relativeAngle;

        if (hitboxShape instanceof Circle){
            hitboxEntity = GameUtils.allocateHitboxEntity(wc, (Circle)hitboxShape, damage, knockbackRatio);
        }

        if (hitboxShape instanceof Rectangle){
            throw new UnsupportedOperationException("Cannot have rectangle hitboxes as of now");
        }
    }
//    public MeleeAbility(WorldContainer wc){
//        this(wc, 5, 0.5f, new Circle(5), 0.0f, 0.0f, 10, 10, 10, 10);
//    }


    float getRelativeDistance() {
        return relativeDistance;
    }
    float getRelativeAngle() {return relativeAngle;}

    int getHitboxEntity() {
        return hitboxEntity;
    }


    public void startEffect() {

    }


}
