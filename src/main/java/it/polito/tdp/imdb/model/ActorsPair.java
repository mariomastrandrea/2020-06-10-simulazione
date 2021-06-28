package it.polito.tdp.imdb.model;

public class ActorsPair
{
	private final Actor actor1;
	private final Actor actor2;
	private final int numMoviesInCommon;
	
	
	public ActorsPair(Actor actor1, Actor actor2, int numMoviesInCommon)
	{
		this.actor1 = actor1;
		this.actor2 = actor2;
		this.numMoviesInCommon = numMoviesInCommon;
	}

	public Actor getActor1()
	{
		return this.actor1;
	}

	public Actor getActor2()
	{
		return this.actor2;
	}

	public int getNumMoviesInCommon()
	{
		return this.numMoviesInCommon;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actor1 == null) ? 0 : actor1.hashCode());
		result = prime * result + ((actor2 == null) ? 0 : actor2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActorsPair other = (ActorsPair) obj;
		if (actor1 == null)
		{
			if (other.actor1 != null)
				return false;
		}
		else
			if (!actor1.equals(other.actor1))
				return false;
		if (actor2 == null)
		{
			if (other.actor2 != null)
				return false;
		}
		else
			if (!actor2.equals(other.actor2))
				return false;
		return true;
	}
}
