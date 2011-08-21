package com.wikidot.entitysystems.rdbmsbeta.examplecomponents;

import com.wikidot.entitysystems.rdbmsbeta.*;

/**
 * Example of a single component - a 2D position component with an x and a y
 * 
 * Note: all fields should be public, so you can direct-access them!
 * 
 * c.f. http://entity-systems.wikidot.com/rdbms-with-code-in-systems
 */
public class Position implements Component
{
	public float x, y;
	
	@Override
	public String toString()
	{
		return "("+x+","+y+")";
	}
}