package game;

import engine.PositionComp;
import engine.RotationComp;
import engine.UserCharacterInputComp;
import engine.WorldContainer;
import engine.audio.AudioComp;
import engine.audio.Sound;
import engine.audio.SoundListenerComp;
import engine.character.CharacterComp;
import engine.character.CharacterInputComp;
import engine.combat.DamageableComp;
import engine.combat.DamagerComp;
import engine.combat.abilities.*;
import engine.graphics.*;
import engine.graphics.view_.ViewControlComp;
import engine.ControlledComp;
import engine.network.client.InterpolationComp;
import engine.physics.*;
import engine.visualEffect.VisualEffectComp;
import engine.visualEffect.VisualEffectUtils;
import game.server.ServerGameTeams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eirik on 05.07.2017.
 */
public class CharacterUtils {


    public static final int CHARACTER_COUNT = 3;
    public static final int SHRANK = 0, SCHMATHIAS = 1, BRAIL = 2;

    private static float hitboxDepth = 1;

    private static int characterCount;



    public static void createOfflineCharacters(WorldContainer wc, ClientGameTeams teams) {

        createClientCharacters(wc, teams);
    }

    public static void createClientCharacters(WorldContainer wc, ClientGameTeams teams) {

        int i = 0;
        for (int charEnt : teams.getCharacterIdsOnTeam(0)) {
            boolean controlled = false;
            if (teams.getControlCharacterTeam() == 0 && i == teams.getControlCharacterIndex()) {
                controlled = true;
            }
            createCharacter(charEnt, wc, controlled, GameUtils.startPositionsTeam1[i][0], GameUtils.startPositionsTeam1[i][1]);

            i++;
        }
        i = 0;
        for (int charEnt : teams.getCharacterIdsOnTeam(1)) {
            boolean controlled = false;
            if (teams.getControlCharacterTeam() == 1 && i == teams.getControlCharacterIndex()) {
                controlled = true;
            }
            createCharacter(charEnt, wc, controlled, GameUtils.startPositionsTeam2[i][0], GameUtils.startPositionsTeam2[i][1]);

            i++;
        }
    }

    public static void createServerCharacters(WorldContainer wc, ServerGameTeams teams) {
        boolean controlled = true;


        int i = 0;
        for (int charEnt : teams.getCharacterIdsOnTeam(0)) {
            createCharacter(charEnt, wc, controlled, GameUtils.startPositionsTeam1[i][0], GameUtils.startPositionsTeam1[i][1]);
            i++;
        }
        i = 0;
        for (int charEnt : teams.getCharacterIdsOnTeam(1)) {
            createCharacter(charEnt, wc, controlled, GameUtils.startPositionsTeam2[i][0], GameUtils.startPositionsTeam2[i][1]);
            i++;
        }
    }


    private static int createCharacter(int characterId, WorldContainer wc, boolean controlled, float x, float y) {
        int charEnt;

        switch(characterId) {
            case SHRANK: charEnt = createShrank(wc, controlled, x, y);
                break;
            case SCHMATHIAS: charEnt = createSchmathias(wc, controlled, x, y);
                break;
            case BRAIL: charEnt = createBrail(wc, controlled, x, y);
                break;
            default:
                throw new IllegalArgumentException("no character of id given");
        }

        return charEnt;
    }

    private static int createShrank(WorldContainer wc, boolean controlled, float x, float y) {
        Sound sndPowershot = new Sound("audio/powershot.ogg");
        Sound sndBoom = new Sound ("audio/boom-bang.ogg");
        Sound sndRapidsShot = new Sound("audio/click4.ogg");
        Sound sndHit = new Sound("audio/laser_hit.ogg");

        float[] color1 = {1, 1, 0};
        float[] color2 = {1, 0, 1};
        int proj1Entity = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 8, color1, sndBoom);
        int proj2Entity = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 20, color2, sndHit);

        int rapidShotSoundIndex = 0;
        int powershotSoundIndex = 1;
        int boomSoundIndex = 2;

        //rapidshot
        ProjectileAbility abRapidshot = new ProjectileAbility(wc, rapidShotSoundIndex, proj1Entity, 2, 2, 30, 1200, 30 );
        abRapidshot.setDamagerValues(wc, 100, 180, 0.5f, -128, false);

        //hyperbeam3
        ProjectileAbility abHyperbeam = new ProjectileAbility(wc, powershotSoundIndex, proj2Entity, 15, 10, 120, 1500, 120);
        abHyperbeam.setDamagerValues( wc, 350,900, 1.1f, -256, false);

        //puffer
        MeleeAbility abPuffer = new MeleeAbility(wc, boomSoundIndex, 8, 2, 8, 60*3, new Circle(128f), 0f, sndBoom);
        abPuffer.setDamagerValues(wc, 20, 900f, 0.1f, 0f, false);


       List<Sound> soundList = new ArrayList<Sound>();
       soundList.add(rapidShotSoundIndex, sndRapidsShot);
       soundList.add(powershotSoundIndex, sndPowershot);
       soundList.add(boomSoundIndex, sndBoom);


        return createCharacter(wc, controlled, x, y, "sol_frank.png", 160f/2f, 512, 256, 180, 130, 32, 1800f,
                abRapidshot, abHyperbeam, abPuffer,
                soundList);
    }

    private static int createSchmathias(WorldContainer wc, boolean controlled, float x, float y) {

        //frogpunch
        int suhSoundIndex = 0;

        MeleeAbility abFrogpunch = new MeleeAbility(wc, suhSoundIndex, 3, 5, 3, 20, new Circle(64f),48.0f, null);
        abFrogpunch.setDamagerValues(wc, 150, 700, 0.8f, -48f, false);

        //hook
        int hookProjEntity = ProjectileUtils.allocateImageProjectileEntity(wc, "hook.png", 256/2, 512, 256, 24, null); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility abHook = new ProjectileAbility(wc, suhSoundIndex, hookProjEntity, 5, 18, 50, 900, 30);
        abHook.setDamagerValues(wc, 200f, 1400f, 0.2f, -128, true);

        //meteorpunch
        MeleeAbility abMeteorpunch = new MeleeAbility(wc, suhSoundIndex, 15, 3, 4, 60, new Circle(32), 64, null);
        abMeteorpunch.setDamagerValues(wc, 500, 1000, 1.5f, -128f, false);

        List<Sound> sounds = new ArrayList<>();
        sounds.add(suhSoundIndex, new Sound("audio/si.ogg") );


        return createCharacter(wc, controlled, x, y, "Schmathias.png", 228f/2f, 720, 400, 267, 195, 32, 2000f,
                abFrogpunch, abHook, abMeteorpunch, sounds);
    }

    private static int createBrail(WorldContainer wc, boolean controlled, float x, float y) {
        Sound snd1 = new Sound("audio/click4.ogg");
        Sound snd2 = new Sound("audio/laser02.ogg");
        Sound snd3 = new Sound("audio/snabbe.ogg");

        int ab1CharSnd = 0;
        int ab2CharSnd = 1;
        int ab3CharSnd = 2;

        float[] purple = {1.0f, 0f, 1.0f};

        //lightForce
        MeleeAbility abFrogpunch = new MeleeAbility(wc, ab1CharSnd, 6, 6, 6, 30, new Circle(64f),64.0f, null);
        abFrogpunch.setDamagerValues(wc, 200, 800, 0.9f, -48f, false);

        //chagger
        int chaggProjectile = ProjectileUtils.allocateSinglecolorProjectileAbility(wc, 64f, purple,null); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility abHook = new ProjectileAbility(wc, ab2CharSnd, chaggProjectile, 20, 6, 50, 650, 30);
        abHook.setDamagerValues(wc, 200f, 400, 0.6f, -100, false);

        //scatter
        MeleeAbility abMeteorpunch = new MeleeAbility(wc, ab3CharSnd, 10, 3, 7, 60, new Circle(96), 128, null);
        abMeteorpunch.setDamagerValues(wc, 100, 800, 0.5f, 0, true);

        List<Sound> sounds = new ArrayList<>();
        sounds.add( snd1 );
        sounds.add( snd2 );
        sounds.add( snd3 );

        return createCharacter(wc, controlled, x, y, "Schmathias.png", 228f/2f, 720, 400, 267, 195, 32, 2000f,
                abFrogpunch, abHook, abMeteorpunch, sounds);
    }

    private static int createShitface(WorldContainer wc, boolean controlled, float x, float y) {

        //frogpunch
        MeleeAbility abFrogpunch = new MeleeAbility(wc, -1, 3, 5, 3, 20, new Circle(64f),48.0f, null);
        abFrogpunch.setDamagerValues(wc, 15, 70, 0.8f, -48f, false);

        //hook
        int hookProjEntity = ProjectileUtils.allocateImageProjectileEntity(wc, "hook.png", 256/2, 512, 256, 24, null); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
        ProjectileAbility abHook = new ProjectileAbility(wc, -1, hookProjEntity, 5, 18, 50, 900, 30);
        abHook.setDamagerValues(wc, 20f, 140f, 0.2f, -128, true);

        //meteorpunch
        MeleeAbility abMeteorpunch = new MeleeAbility(wc, -1, 15, 3, 4, 60, new Circle(32), 64, null);
        abMeteorpunch.setDamagerValues(wc, 50, 100, 1.5f, -128f, false);


        List<Sound> sounds = new ArrayList<Sound>();
        sounds.add( new Sound("audio/si.ogg") );

        return createCharacter(wc, controlled, x, y, "Schmathias.png", 228f/2f, 720, 400, 267, 195, 32, 2000f,
                abFrogpunch, abHook, abMeteorpunch, sounds );
    }


    public static int allocateHitboxEntity(WorldContainer wc, Circle shape, Sound onHitSound){
        int e = wc.createEntity();

        wc.addComponent(e, new PositionComp(0, 0, hitboxDepth));
        wc.addInactiveComponent(e, new RotationComp());

        //wc.addInactiveComponent(e, new PhysicsComp());
        wc.addInactiveComponent(e, new HitboxComp());

        wc.addInactiveComponent(e, new DamagerComp());

        float[] redColor = {1.0f, 0f,0f};
        wc.addInactiveComponent(e, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(shape.getRadius(), 16, redColor)) );

        wc.addInactiveComponent(e, new CollisionComp(shape));

        wc.addComponent(e, new VisualEffectComp(VisualEffectUtils.createOnHitEffect()));

        if (onHitSound != null) {
            wc.addComponent(e, new AudioComp(onHitSound));
        }

        return e;
    }



    private static int createCharacter(WorldContainer wc, boolean controlled, float x, float y, String imagePath, float radiusOnImage, float imageWidth, float imageHeight, float offsetXOnImage, float offsetYOnImage, float radius, float moveAccel, Ability ab1, Ability ab2, Ability ab3, List<Sound> soundList) {
        int characterEntity = wc.createEntity();

        float scale = radius / radiusOnImage;
        float width = imageWidth * scale;
        float height = imageHeight * scale;
        float offsetX = offsetXOnImage * scale;
        float offsetY = offsetYOnImage * scale;

        wc.addComponent(characterEntity, new CharacterComp(moveAccel));//1500f));
        wc.addComponent(characterEntity, new PositionComp(x, y, (float) (characterCount++) / 100f)); //z value is a way to make draw ordering and depth positioning correspond. Else alpha images will appear incorrect.
        wc.addComponent(characterEntity, new RotationComp());

        wc.addComponent(characterEntity, new TexturedMeshComp(TexturedMeshUtils.createRectangle(imagePath, width, height)));
        wc.addComponent(characterEntity, new MeshCenterComp(offsetX, offsetY));

        wc.addComponent(characterEntity, new AbilityComp(ab1, ab2, ab3));

        //server and offline
        wc.addComponent(characterEntity, new PhysicsComp(80, 5f, 0.3f, PhysicsUtil.FRICTION_MODEL_VICIOUS));
        wc.addComponent(characterEntity, new CollisionComp(new Circle(radius)));
        wc.addComponent(characterEntity, new NaturalResolutionComp());

        wc.addComponent(characterEntity, new AffectedByHoleComp());

        wc.addComponent(characterEntity, new DamageableComp());
        wc.addComponent(characterEntity, new CharacterInputComp());

        //client
        wc.addComponent(characterEntity, new InterpolationComp());

        wc.addComponent(characterEntity, new AudioComp(soundList, 1, 100, 2000));

        if (controlled) {
            wc.addComponent(characterEntity, new UserCharacterInputComp());
            wc.addComponent(characterEntity, new ViewControlComp(-GameUtils.VIEW_WIDTH / 2f, -GameUtils.VIEW_HEIGHT / 2f));
            wc.addComponent(characterEntity, new ControlledComp());
            wc.addComponent(characterEntity, new SoundListenerComp());

        }


        return characterEntity;

    }
}