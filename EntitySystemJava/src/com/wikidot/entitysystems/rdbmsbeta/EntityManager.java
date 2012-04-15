package com.wikidot.entitysystems.rdbmsbeta;

import java.io.*;
import java.util.*;

/**
 * Standard design: c.f.
 * http://entity-systems.wikidot.com/rdbms-with-code-in-systems
 * 
 * Modified in java to use Generics: instead of having a "ComponentType" enum,
 * we use the class type of each subclass instead. This is safer.
 */

public class EntityManager implements Serializable
{
	boolean frozen;
	List<UUID> allEntities;
	HashMap<UUID, String> entityHumanReadableNames;
	HashMap<Class, HashMap<UUID, ? extends Component>> componentStores;

	public EntityManager()
	{
		frozen = false;
		allEntities = new LinkedList<UUID>();
		entityHumanReadableNames = new HashMap<UUID, String>();
		componentStores = new HashMap<Class, HashMap<UUID, ? extends Component>>();
	}

	public <T extends Component> T getComponent(UUID entity,
			Class<T> componentType)
	{
		synchronized (componentStores)
		{
			HashMap<UUID, ? extends Component> store = componentStores
					.get(componentType);

			if (store == null)
				throw new IllegalArgumentException(
						"GET FAIL: there are no entities with a Component of class: "
								+ componentType);

			T result = (T) store.get(entity);
			
			if (result == null)
			{
				/** DEFAULT: normal debug info:
				*/
				throw new IllegalArgumentException("GET FAIL: " + entity
						+ "(name:"+nameFor(entity)+")"
						+ " does not possess Component of class\n   missing: "
						+ componentType);
				/** OPTIONAL: more detailed debug info:
				 * 
				 *
				StringBuffer sb = new StringBuffer();
				for( UUID e : store.keySet() )
				{
					sb.append( "\nUUID: "+e+" === "+store.get(e) );
				}
				
				throw new IllegalArgumentException("GET FAIL: " + entity
						+ "(name:"+nameFor(entity)+")"
						+ " does not possess Component of class\n   missing: "
						+ componentType
						+ "TOTAL STORE FOR THIS COMPONENT CLASS : "+ sb.toString()
						);
						*/
			}

			return result;
		}
	}
	
	public <T extends Component> void removeComponent(UUID entity,
			T component)
	{
		synchronized (componentStores)
		{
			HashMap<UUID, ? extends Component> store = componentStores
					.get(component.getClass());

			if (store == null)
				throw new IllegalArgumentException(
						"REMOVE FAIL: there are no entities with a Component of class: "
								+ component.getClass());

			T result = (T) store.remove(entity);
			if (result == null)
				throw new IllegalArgumentException("REMOVE FAIL: " + entity
						+ "(name:"+nameFor(entity)+")"
						+ " does not possess Component of class\n   missing: "
						+ component.getClass());
		}
	}
	
	public <T extends Component> boolean hasComponent(UUID entity,
			Class<T> componentType)
	{
		synchronized (componentStores)
		{
			HashMap<UUID, ? extends Component> store = componentStores
					.get(componentType);

			if (store == null)
				return false;
			else
				return store.containsKey(entity);
		}
	}
	
	/**
	 * WARNING: low performance implementation!
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends Component> List<T> getAllComponentsOnEntity(UUID entity )
	{
		synchronized (componentStores)
		{
			LinkedList<T> components = new LinkedList<T>();

			for (HashMap<UUID, ? extends Component> store : componentStores.values())
			{
				if (store == null)
					continue;

				T componentFromThisEntity = (T) store.get(entity);
				
				if (componentFromThisEntity != null)
					components.addLast(componentFromThisEntity);
			}
			
			return components;
		}
	}

	public <T extends Component> Collection<T> getAllComponentsOfType(
			Class<T> componentType)
	{
		synchronized (componentStores)
		{
			HashMap<UUID, ? extends Component> store = componentStores
					.get(componentType);

			if (store == null)
				return new LinkedList<T>();

			return (Collection<T>) store.values();
		}
	}

	public <T extends Component> Set<UUID> getAllEntitiesPossessingComponent(
			Class<T> componentType)
	{
		synchronized (componentStores)
		{
			HashMap<UUID, ? extends Component> store = componentStores
					.get(componentType);

			if (store == null)
				return new HashSet<UUID>();

			return store.keySet();
		}
	}

	public <T extends Component> void addComponent(UUID entity, T component)
	{
		if (frozen)
			return;

		synchronized (componentStores)
		{
			HashMap<UUID, ? extends Component> store = componentStores
					.get(component.getClass());

			if (store == null)
			{
				store = new HashMap<UUID, T>();
				componentStores.put(component.getClass(), store);
			}

			((HashMap<UUID, T>) store).put(entity, component);
		}
	}

	public UUID createEntity()
	{
		if (frozen)
			return null;

		final UUID uuid = UUID.randomUUID();
		allEntities.add(uuid);

		return uuid;
	}
	
	public UUID createEntity( String name )
	{
		if (frozen)
			return null;

		final UUID uuid = UUID.randomUUID();
		allEntities.add(uuid);
		entityHumanReadableNames.put(uuid, name);

		return uuid;
	}
	
	public void setEntityName( UUID entity, String name )
	{
		entityHumanReadableNames.put(entity, name);
	}
	
	public String nameFor( UUID entity )
	{
		return entityHumanReadableNames.get( entity );
	}

	public void killEntity(UUID entity)
	{
		if (frozen)
			return;

		synchronized (componentStores)
		{

			for (HashMap<UUID, ? extends Component> componentStore : componentStores
					.values())
			{
				componentStore.remove(entity);
			}
			allEntities.remove(entity);
		}
	}

	public void freeze()
	{
		frozen = true;
	}

	public void unFreeze()
	{
		frozen = false;
	}
}