package it.polito.tdp.imdb.model;

import java.util.Comparator;

public class ActorsComparator implements Comparator<Actor>
{
	@Override
	public int compare(Actor a1, Actor a2)
	{
		return (a1.getLastName() + a1.getFirstName()).compareTo(a2.getLastName() + a2.getFirstName());
	}
}
