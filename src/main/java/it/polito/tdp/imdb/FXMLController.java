package it.polito.tdp.imdb;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Model;
import it.polito.tdp.imdb.simulation.SimulationResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController 
{
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnSimili;

    @FXML
    private Button btnSimulazione;

    @FXML
    private ComboBox<String> boxGenere;

    @FXML
    private ComboBox<Actor> boxAttore;

    @FXML
    private TextField txtGiorni;

    @FXML
    private TextArea txtResult;
    
    private Model model;
    
    
    @FXML
    void doCreaGrafo(ActionEvent event) 
    {
    	String selectedGenre = this.boxGenere.getValue();
    	
    	if(selectedGenre == null || selectedGenre.isBlank())
    	{
    		this.txtResult.setText("Errore: selezionare un genere di film dal menù a tendina");
    		return;
    	}
    	
    	selectedGenre = selectedGenre.trim();
    	
    	this.model.createGraph(selectedGenre);
    	
    	//print graph info
    	int numVertices = this.model.getNumVertices();
    	int numEdges = this.model.getNumEdges();
    	
    	String output = this.printGraphInfo(numVertices, numEdges);
    	this.txtResult.setText(output);
    	
    	//update UI
    	List<Actor> allOrderedActors = this.model.getAllOrderedActors();
    	this.boxAttore.getItems().clear();
    	this.boxAttore.getItems().addAll(allOrderedActors);
    }

    private String printGraphInfo(int numVertices, int numEdges)
	{
    	if(numVertices == 0)
			return "Il grafo creato è vuoto (#Vertici: 0)";
		
		return String.format("Grafo creato:\n#Vertici: %d\n#Archi: %d", numVertices, numEdges);
	}

	@FXML
    void doAttoriSimili(ActionEvent event) 
    {
		if(!this.model.isGraphCreated())
		{
			this.txtResult.setText("Errore: creare prima il grafo");
			return;
		}
		
		Actor selectedActor = this.boxAttore.getValue();
		
		if(selectedActor == null)
		{
			this.txtResult.setText("Errore: selezionare un attore dal menù a tendina");
    		return;
		}
		
		List<Actor> orderedSimilarActors = this.model.getOrderedSimilarActorsTo(selectedActor);
		
		String output = this.printSimilarActors(orderedSimilarActors, selectedActor);
		this.txtResult.setText(output);
    }

    private String printSimilarActors(List<Actor> orderedSimilarActors, Actor selectedActor)
	{
		if(orderedSimilarActors.isEmpty())
			return "Non esistono attori adiacenti a "+selectedActor.toString();
    	
		StringBuilder sb = new StringBuilder();
		
		sb.append("Elenco degli attori simili a ").append(selectedActor.toString()).append(":");
		
		for(Actor actor : orderedSimilarActors)
			sb.append("\n - ").append(actor.toString());
		
		return sb.toString();
	}

	@FXML
    void doSimulazione(ActionEvent event) 
    {
		if(!this.model.isGraphCreated())
		{
			this.txtResult.setText("Errore: creare prima il grafo");
			return;
		}
		
		String numDaysInput = this.txtGiorni.getText();
		
		if(numDaysInput == null || numDaysInput.isBlank())
		{
			this.txtResult.setText("Errore: inserire un valore intero di num giorni");
			return;
		}
		
		numDaysInput = numDaysInput.trim();
		
		int numDays;
		try
		{
			numDays = Integer.parseInt(numDaysInput);
		}
		catch(NumberFormatException nfe)
		{
			this.txtResult.setText("Errore: inserire un valore intero valido di num giorni");
			return;
		}
		
		if(numDays <= 0)
		{
			this.txtResult.setText("Errore: inserire un valore intero positivo di num giorni");
			return;
		}
		
		SimulationResult result = this.model.runSimulationFor(numDays);
		
		List<Actor> interviewedActors = result.getInterviewedActors();
		int numBreaks = result.getNumProducerBreaks();
		
		String output = this.printSimulationResults(interviewedActors, numBreaks, numDays);
		this.txtResult.setText(output);
    }

    private String printSimulationResults(List<Actor> interviewedActors, int numBreaks, int numDays)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Simulazione effettuata per ").append(numDays).append(" giorni:\n\n");
		sb.append("- Numero di giorni di pausa del produttore: ").append(numBreaks).append("\n");
		sb.append("- Attori intervistati (").append(interviewedActors.size()).append("):");
		
		for(Actor a : interviewedActors)
			sb.append("\n\t").append(a.toString());
    	
		return sb.toString();
	}

	@FXML
    void initialize() 
    {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimili != null : "fx:id=\"btnSimili\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxGenere != null : "fx:id=\"boxGenere\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxAttore != null : "fx:id=\"boxAttore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGiorni != null : "fx:id=\"txtGiorni\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model)
    {
    	this.model = model;
    	
    	List<String> allGenres = this.model.getAllGenres();
    	this.boxGenere.getItems().clear();
    	this.boxGenere.getItems().addAll(allGenres);
    }
}
