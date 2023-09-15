package hu.krtn.brigad.engine.ecs;

import hu.krtn.brigad.engine.serialization.Serializable;

/**
 * The base class for all components.
 * Theoretically a component can be anything, but it is recommended to use
 * components for data only. (No logic)
 * This class is only a marker class, it has no methods or fields.
 * It's used to group components together.
 */
public abstract class Component extends Serializable {

}
