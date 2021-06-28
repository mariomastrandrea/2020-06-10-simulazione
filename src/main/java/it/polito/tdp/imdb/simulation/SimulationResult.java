package it.polito.tdp.imdb.simulation;

import java.util.List;

import it.polito.tdp.imdb.model.Actor;

public interface SimulationResult
{
	List<Actor> getInterviewedActors();
	int getNumProducerBreaks();
}
