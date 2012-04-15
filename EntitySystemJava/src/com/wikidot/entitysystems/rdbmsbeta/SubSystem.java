package com.wikidot.entitysystems.rdbmsbeta;

/**
 * Standard design: c.f. http://entity-systems.wikidot.com/rdbms-with-code-in-systems
 */

public interface SubSystem
{
	public void processOneGameTick( long lastFrameTime );
    
    /**
	 * Mostly used for debugging - check which system is firing, what order
	 * systems are firing in, etc
	 * 
	 * @return the human-readable name of this system
	 */
	public String getSimpleName();
}