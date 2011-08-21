package com.wikidot.entitysystems.rdbmsbeta;

import java.util.*;

import sun.tools.tree.*;

/**
 * The MetaEntity is a smarter, more powerful way of creating and handling Entities in an Entity System.
 * 
 * It takes a lot of the boilerplate code for creating, editing, and deleting entities, and packages it up in a neat OOP class that's easy to use, and
 * easy to pass around from method to method, system to system.
 * 
 * NB: it is LESS EFFICIENT than manually handling your Entities - it adds the overhead of:
 * <ol>
 * <li>A Java Object instance per entity
 * <li>A reference to the EntityManager where the Entity lives (if you only have a single EntityManager in your app, then this is just wasting memory)
 * <li>A (possibly null) internal "name" that is easier for humans to read than the UUID when debugging
 * </ol>
 * 
 * <h2>Usage suggestions</h2>
 * To avoid performance degradation, it's expected that you'll only use MetaEntity objects sparingly, and temporarily.
 * 
 * When you have a system that isn't performance limited, you might write it to use MetaEntity objects, to increase readability.
 * 
 * If you have performance problems, you can re-write critical sections directly using Entity's, removing the overhead of this class.
 */
public class MetaEntity
{
	/** Global {@link EntityManager} that will be used if the user doesn't specify which EM they want to add this
	 * entity to, and want to manage this entity.
	 * 
	 * Should be set once only at program-start
	 */
	public static EntityManager defaultEntityManager;
	
	public static MetaEntity loadFromEntityManager( UUID e )
	{
		MetaEntity metaEntity = new MetaEntity( e );
		
		return metaEntity;
	}
	
	/** Initialized to null, signifying "invalid; NOT registered in any EntityManager yet" */
	public UUID entity = null;
	
	public EntityManager parentEntityManager;
	
	public String internalName;
	
	/**
	 * Invoke this to use the global default {@link EntityManager} as the source
	 */
	public MetaEntity()
	{
		if( defaultEntityManager == null )
			throw new IllegalArgumentException( "There is no global EntityManager; create a new EntityManager before creating Entity's" );
		
		entity = defaultEntityManager.createEntity();	
	}
	
	/**
	 * This should NEVER be called by external classes - it's used by the static method loadFromEntityManager
	 */
	protected MetaEntity( UUID e )
	{
		if( defaultEntityManager == null )
			throw new IllegalArgumentException( "There is no global EntityManager; create a new EntityManager before creating Entity's" );
		
		entity = e;	
	}
	
	/**
	 * This is the main constructor for Entities - usually, you'll know which Components you want them to have
	 * 
	 * NB: this is a NON-lazy way of instantiating Entities - in low-mem situations, you may want to
	 * use an alternative constructor that accepts the CLASS of each Component, rather than the OBJECT, and
	 * which only instantiates / allocates the memory for the data of each component when that component is
	 * (eventually) initialized.
	 * 
	 * @param n the internal name that will be attached to this entity, and reported in debugging info
	 * @param components
	 */
	public MetaEntity( String n, Component... components )
	{
		this( components ); 
		
		internalName = n;
	}
	
	/**
	 * This is the main constructor for Entities - usually, you'll know which Components you want them to have
	 * 
	 * NB: this is a NON-lazy way of instantiating Entities - in low-mem situations, you may want to
	 * use an alternative constructor that accepts the CLASS of each Component, rather than the OBJECT, and
	 * which only instantiates / allocates the memory for the data of each component when that component is
	 * (eventually) initialized.
	 * 
	 * @param components
	 */
	public MetaEntity( Component... components )
	{
		this(); // takes care of getting the initial "entity" part
		
		for( Component c : components )
		{
			this.add( c );
		}
	}
	
	/**
	 * CONVENIENCE METHOD: delegates to the source {@link EntityManager} to do the add
	 * 
	 * @param c {@link Component} to add to this entity (only added within the {@link EntityManager}, does NOT modify "this" object!)
	 */
	public void add( Component c )
	{
		parentEntityManager.addComponent( entity, c );
	}
	
	/**
	 * CONVENIENCE METHOD: delegates to the source {@link EntityManager} to do the get
	 * 
	 * @param c component to add to this entity (only added within the EntitySystem, does NOT modify "this" object!)
	 * @param <T> {@link Component} fetched from the {@link EntityManager}
	 * @param type Class object representing the particular {@link Component} you need
	 * @return
	 */
	public <T extends Component> T get( Class<T> type )
	{
		return parentEntityManager.getComponent( entity, type );
	}
	
	/**
	 * This implementation has a low-performance simple check - it fetches the component, and checks if it's null
	 * 
	 * @param type Class object representing the particular {@link Component} you want to check the existence of
	 * @return true if this entity has at least one of these components
	 */
	public <T extends Component> boolean has( Class<T> type )
	{
		return null != get( type );
	}
	
	/**
	 * CONVENIENCE METHOD: delegates to the source {@link EntityManager} to do the get
	 * 
	 * @return
	 */
	public List<? extends Component> getAll()
	{
		return parentEntityManager.getAllComponentsOnEntity( entity );
	}
	
	/**
	 * WARNING: low-performance implementation! This fetches the components using getAll(), and then deletes them one by one!
	 * 
	 */
	public void removeAll()
	{
		for( Component c : getAll() )
		{
			remove(c);
		}
	}
	
	/**
	 * CONVENIENCE METHOD: delegates to the source {@link EntityManager} to do the remove
	 * 
	 * @param <T> {@link Component} fetched from the {@link EntityManager}
	 * @param c The *actual* {@link Component} you want to remove from this entity
	 */
	public <T extends Component> void remove( Component c )
	{
		parentEntityManager.removeComponent( entity, c );
	}
	
	/**
	 * Creates a new Entity that is exactly the same in terms of Components and Component-values,
	 * differing only in that it has a unique Entity-id (and that all its data is private, non-shared,
	 * of course!)
	 * 
	 * @return the new Entity
	 *
/*	public Entity duplicate()
	{
		return source.duplicate( this );
	}*/
	
	@Override public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for( Component c : parentEntityManager.getAllComponentsOnEntity( entity ) )
		{
			if( sb.length() > 0 )
				sb.append(  ", " );
			sb.append( c.toString() );
		}
		return "Entity["+entity+":"+internalName+"]("+sb.toString()+")";
	}
	
	/**
	 * This method states that we no longer care about this object, and that we would
	 * like the Entity itself to be Garbage-Collected; yes, it may still have some active
	 * components, but we want it to DIE, and we want it to SIMPLY GO AWAY, NOW PLEASE
	 */
	public void kill()
	{
		parentEntityManager.killEntity( entity );
	}
}