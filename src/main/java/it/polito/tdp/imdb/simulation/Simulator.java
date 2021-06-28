package it.polito.tdp.imdb.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.simulation.ProducerDayEvent.EventType;

public class Simulator implements SimulationResult
{
	private static final double BREAK_PROBABILITY = 0.9;
	private static final double RANDOM_CHOICE_PROBABILITY = 0.6;

	//input
	private Graph<Actor, DefaultWeightedEdge> graph;
	@SuppressWarnings("unused")
	private int numDays;
	
	//queue
	private PriorityQueue<ProducerDayEvent> eventsQueue;
	
	//output data
	private List<Actor> interviewedActors;
	private Set<Actor> interviewedActorsSet;
	private int numProducerBreaks;
	

	public void initialize(Graph<Actor, DefaultWeightedEdge> graph, int numDays)
	{
		this.graph = graph;
		this.numDays = numDays;
		
		//create and fill the events queue
		this.eventsQueue = new PriorityQueue<>();
		
		for(int i=1; i<=numDays; i++)
		{
			int numDay = i;
			ProducerDayEvent newProducerDay = new ProducerDayEvent(numDay);
			this.eventsQueue.add(newProducerDay);
		}
		
		//setting first day
		ProducerDayEvent firstDay = this.eventsQueue.peek();
		firstDay.setType(EventType.START_INTERVIEW);
		
		//initialization
		this.interviewedActors = new LinkedList<>();
		this.interviewedActorsSet = new HashSet<>();
		this.numProducerBreaks = 0;
	}

	public SimulationResult run()
	{
		ProducerDayEvent currentEvent;
		
		while((currentEvent = this.eventsQueue.poll()) != null)
		{
			EventType producerDayType = currentEvent.getType();
			Actor actorToInterview;
			
			switch(producerDayType)
			{
				case START_INTERVIEW:
					actorToInterview = this.getRandomActor();
					this.interviewedActors.add(actorToInterview);
					this.interviewedActorsSet.add(actorToInterview);
					
					this.setEventAfterInterview();
					break;
					
				case ORDINARY_INTERVIEW:
					if(Math.random() < RANDOM_CHOICE_PROBABILITY)
						actorToInterview = this.getRandomActor();
					else
					{
						Actor previousActorInterviewed = 
								this.interviewedActors.get(this.interviewedActors.size() - 1);
						
						actorToInterview = this.getAdviceFrom(previousActorInterviewed);
					}
					
					this.interviewedActors.add(actorToInterview);
					this.interviewedActorsSet.add(actorToInterview);
		
					this.setEventAfterInterview();
					break;
				
				case BREAK:
					this.numProducerBreaks++;
					ProducerDayEvent nextEvent = this.eventsQueue.peek();
					if(nextEvent != null)
						nextEvent.setType(EventType.START_INTERVIEW);
					break;
			}
		}
		
		return this;
	}

	private void setEventAfterInterview()
	{
		ProducerDayEvent nextEvent = this.eventsQueue.peek();
		
		if(nextEvent == null) return; //last day
		
		if(this.twoActorsInterviewedWithSameGender())
		{
			if(Math.random() < BREAK_PROBABILITY)
				nextEvent.setType(EventType.BREAK);
			else
				nextEvent.setType(EventType.ORDINARY_INTERVIEW);
		}
		else 
			nextEvent.setType(EventType.ORDINARY_INTERVIEW);
	}

	private Actor getAdviceFrom(Actor previousActorInterviewed)
	{
		int maxWeight = Integer.MIN_VALUE;
		List<Actor> adjacentBestActors = new ArrayList<>(); 
		
		for(var edge : this.graph.edgesOf(previousActorInterviewed))
		{
			int weight = (int)this.graph.getEdgeWeight(edge);
			Actor adjacentActor = Graphs.getOppositeVertex(this.graph, edge, previousActorInterviewed);
			
			if(weight >= maxWeight)
			{
				if(weight > maxWeight)
				{
					maxWeight = weight;
					adjacentBestActors.clear();
				}
				
				adjacentBestActors.add(adjacentActor);
			}
		}
		
		adjacentBestActors.removeIf(a -> this.interviewedActorsSet.contains(a));
		
		if(adjacentBestActors.isEmpty())
			return this.getRandomActor();
		
		int randomIndex = (int)(Math.random() * adjacentBestActors.size());
		
		return adjacentBestActors.get(randomIndex);
	}

	private boolean twoActorsInterviewedWithSameGender()
	{
		if(this.interviewedActors.size() < 2)
			return false;
		
		Actor last = this.interviewedActors.get(this.interviewedActors.size() - 1);
		Actor lastlast = this.interviewedActors.get(this.interviewedActors.size() - 2);
		
		return last.getGender().equals(lastlast.getGender());
	}

	private Actor getRandomActor()
	{
		int numActors = this.graph.vertexSet().size();
		Actor actor = null;
		int loops = 0;
		
		while(actor == null)
		{
			loops++;
			int random = (int)(Math.random() * numActors);	//from 0 to numActors - 1

			Iterator<Actor> iterator = this.graph.vertexSet().iterator();
			int count = 0;
			
			while(count < random)
			{
				iterator.next();
				count++;
			}
			
			Actor a = iterator.next();
			
			if(!this.interviewedActors.contains(a))
				actor = a;
			
			if(loops == numActors)
				break;
		}
		
		return actor;
	}

	@Override
	public List<Actor> getInterviewedActors()
	{
		return this.interviewedActors;
	}

	@Override
	public int getNumProducerBreaks()
	{
		return this.numProducerBreaks;
	}

}
