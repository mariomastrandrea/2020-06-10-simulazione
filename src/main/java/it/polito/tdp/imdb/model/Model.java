package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import it.polito.tdp.imdb.db.ImdbDAO;
import it.polito.tdp.imdb.simulation.SimulationResult;
import it.polito.tdp.imdb.simulation.Simulator;

public class Model 
{
	private final ImdbDAO dao;
	private final List<String> allGenres;
	private Graph<Actor, DefaultWeightedEdge> graph;
	private final Map<Integer, Actor> actorsIdMap;
	private final Simulator simulator;
	
	
	public Model()
	{
		this.dao = new ImdbDAO();
		this.allGenres = new ArrayList<>();
		this.actorsIdMap = new HashMap<>();
		this.simulator = new Simulator();
	}


	public List<String> getAllGenres()
	{
		if(this.allGenres.isEmpty())
			this.allGenres.addAll(this.dao.getAllGenres());
		
		return this.allGenres;
	}


	public void createGraph(String selectedGenre)
	{
		this.graph = GraphTypeBuilder.<Actor, DefaultWeightedEdge>undirected()
									.allowingMultipleEdges(false)
									.allowingSelfLoops(false)
									.weighted(true)
									.edgeClass(DefaultWeightedEdge.class)
									.buildGraph();
		
		//add vertices
		Collection<Actor> vertices = this.dao.getAllActorsOf(selectedGenre, this.actorsIdMap);
		Graphs.addAllVertices(this.graph, vertices);
		
		//add edges
		Collection<ActorsPair> allActorsPairs = 
				this.dao.getAllActorsPairsOf(selectedGenre, this.actorsIdMap);
		
		for(ActorsPair pair : allActorsPairs)
		{
			Actor actor1 = pair.getActor1();
			Actor actor2 = pair.getActor2();
			int weight = pair.getNumMoviesInCommon();
			
			if(!this.graph.containsVertex(actor1) || !this.graph.containsVertex(actor2))
				throw new RuntimeException("Error: actor not found in graph"); //for debug
			
			Graphs.addEdge(this.graph, actor1, actor2, (double)weight);
		}
	}

	public int getNumVertices() { return this.graph.vertexSet().size(); }
	public int getNumEdges() { return this.graph.edgeSet().size(); }
	public boolean isGraphCreated() { return this.graph != null; }


	public List<Actor> getAllOrderedActors()
	{
		if(this.graph == null) return null;
		
		List<Actor> orderedActors = new ArrayList<>(this.graph.vertexSet());
		orderedActors.sort(new ActorsComparator());
		
		return orderedActors;
	}


	public List<Actor> getOrderedSimilarActorsTo(Actor selectedActor)
	{
		var graphInspector = new ConnectivityInspector<>(this.graph);
		
		List<Actor> orderedSimilarActors = 
				new ArrayList<>(graphInspector.connectedSetOf(selectedActor));
		
		orderedSimilarActors.sort(new ActorsComparator());
		
		return orderedSimilarActors;
	}


	public SimulationResult runSimulationFor(int numDays)
	{
		this.simulator.initialize(this.graph, numDays);
		
		SimulationResult result = this.simulator.run();
		return result;
	}
}
