package engine;

import java.util.*;

/**
 * Created by eirik on 13.06.2017.
 *
 * Contains all entities and components
 */
public class WorldContainer {

    private static int ENTITY_COUNT = 32;



    //an overview of entity id's in use
    private boolean[] entities;

    //A mapping between entities and components for each component type.
    //A TreeMap is used to keep the map sorted on its keyValues.
    private Map<Class<? extends Component>, TreeMap<Integer, Component>> components = new HashMap<>();

    private List<Sys> systems = new ArrayList<>();




    public WorldContainer() {
        entities = new boolean[ENTITY_COUNT];

    }


    //---------SETUP

    //assign component types to be used during execution
    public void assignComponentType(Class<? extends Component> compType) {
        components.put(compType, new TreeMap<>());
    }

    //add system instances to be updated/run on each update frame
    public void addSystem(Sys system) {
        system.setWorldContainer(this);
        systems.add(system);
    }


    //----------EXECUTION

    public void updateSystems() {
        for (Sys s : systems) {
            s.update();
        }
    }


    //----------ENTITY HANDLING

    public int createEntity() {
        int e = allocateEntity();

        return e;
    }
    public void destroyEntity(int entity) {
        if (! entityExists(entity)) throw new IllegalArgumentException("Trying to destroy an entity that doesnt exist");

        deallocateEntity(entity);
    }

    /**
     * Retrieve the entities that contains a given component  in ascending order based on the entities id.
     * That is, the set's values are ordered
     * @param compType
     * @return
     */
    public Set<Integer> getEntitiesWithComponentType(Class<? extends Component> compType) {
        return components.get(compType).keySet();
    }

    private int allocateEntity() {
        for (int i = 0; i < ENTITY_COUNT; i++) {
            if (!entities[i]) {
                entities[i] = true;
                return i;
            }
        }
        throw new IllegalStateException("There is not allocated enough space for more entities");
    }
    private void deallocateEntity(int entity) {
        entities[entity] = false;
    }
    private boolean entityExists(int entity) {
        return entities[entity];
    }



    //----------COMPONENT HANDLING

    public void addComponent(int entity, Component comp) {
        validateComponentType(comp);

        components.get(comp.getClass()).put(entity, comp);
    }
    public Map<Integer, Component> getComponentsOfType(Class<? extends Component> compType) {
        validateComponentType(compType);

        return components.get(compType);
    }
    public Component getComponent(int entity, Class<? extends Component> compType) {
        Component c = getComponentsOfType(compType).get(entity);
        if (c == null) throw new IllegalStateException("No component of the given type is assigned to the given entity, type="+compType);
        return c;
    }
    public boolean hasComponent(int entity, Class<? extends Component> compType) {
        return getComponentsOfType(compType).containsKey(entity);
   }

    private void validateComponentType(Component comp) {
        validateComponentType(comp.getClass());
    }
    private void validateComponentType(Class<? extends Component> compType) {
        if (!components.containsKey(compType)) throw new IllegalStateException("Trying to use a component of a type that is not assigned, type="+compType);
    }


}


//
//    public int createEntity() {
//        int e = allocateEntity();
//        resetEntityMask(e); // should not be needed
//        initAllocatedEntity(e);
//
//        return e;
//    }
//    public void destroyEntity(int entity) {
//        if (! entityExists(entity)) throw new IllegalArgumentException("Trying to destroy an entity that doesnt exist");
//
//        resetEntityMask(entity);
//    }
//
//
//    private int allocateEntity() {
//        for (int i = 0; i < ENTITY_COUNT; i++) {
//            if (!entityExists(i)) {
//                return i;
//            }
//        }
//        throw new IllegalStateException("There is not allocated enough space for more entities");
//    }
//    private void initAllocatedEntity(int entity) {
//        addEntityMask(entity, COMPMASK_ENTITY_EXISTS); //have to make sure that hasComponent operates on mask-level
//    }
//    public boolean entityExists(int entity) {
//        if (getEntityMask(entity) == 0) {
//            return true;
//        }
//        //debug test
//        else if (! hasComponent(entity, COMPMASK_ENTITY_EXISTS)) {
//            throw new IllegalStateException("An entity has a nonzero mask, but no exist component");
//        }
//
//        return false;
//    }
//    private int getEntityMask(int entity) {
//        return entityMask[entity];
//    }
//    private void addEntityMask(int entity, int mask) {
//        entityMask[entity] = entityMask[entity] | mask;
//    }
//    private void removeEntityMask(int entity, int mask) {
//        entityMask[entity] = entityMask[entity] & ~mask;
//    }
//
//
//    /**
//     * Update entity mask and components map
//     * If no mapping is assigned for given compmask, create one
//     * @param entity
//     * @param comp
//     */
//
//    public void addComponent(int entity, Component comp) {
//        int compmask = comp.getMask();
//
//        addEntityMask(entity, compmask);
//        if (!components.containsKey(compmask)) {
//            components.put(compmask, new HashMap<Integer, Component>() );
//        }
//        components.get(compmask).put(entity, comp);
//    }
//
//    /**
//     * Update entity mask and components map
//     * @param entity
//     * @param compmask
//     */
//    public void removeComponent(int entity, int compmask) {
//        removeEntityMask(entity, compmask);
//        components.get( compmask ).remove( entity );
//        //Maybe remove mapping if no entities are left
//    }
//
////    public boolean hasComponent(int compmask, int entity) {
////        if (components.containsKey(component)) { //if there is at least one entity with this component
////            if (components.get(component).containsKey(entity)) {
////                return true;
////            }
////        }
////        return false;
////    }
//    private boolean hasComponent(int entity, int compmask) {
//        return (entityMask[entity] & compmask) == compmask;
//    }
//
//    public Component getComponent(int compmask, int entity) {
//        return components.get(entity).get(compmask);
//    }
//
//    public Map<Integer, Component> getComponents(int compmask) {
//        return components.get(compmask);
//    }
//
//
//
//
//
//
//
//    private void resetEntityMask(int entity) { //cleans entity masks ++
//        entityMask[entity] = 0;
//    }
//}
//
