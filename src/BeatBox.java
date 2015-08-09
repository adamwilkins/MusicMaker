import java.awt.*; //imports
import javax.swing.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;

public class BeatBox {
	JPanel mainPanel;					//for all the objects we are going to use and share
	ArrayList<JCheckBox> checkboxList;
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame theFrame;
										//arrays for the names and values of our instruments
	String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-Mid Tom", "High Agogo", "Open Hi Conga"};
	int[] instruments = {73,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
	
										//main class creates an object and calls the GUI building method
	public static void main(String args[]){
		new BeatBox().buildGUI();
	}
										//method to make a beautiful screen
	public void buildGUI(){
		theFrame = new JFrame("MusicMaker");		//name of the application
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//so we can exit
		BorderLayout layout = new BorderLayout();	
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); //aesthetics
		
		checkboxList = new ArrayList<JCheckBox>();	//for my beats
		Box buttonBox = new Box(BoxLayout.Y_AXIS);	//positioning of the buttonBox
		
		JButton start = new JButton("Start");		//button to start the sounds
		start.addActionListener(new MyStartListener());	//add event listening
		buttonBox.add(start);						//adds it to my layout for the buttons
		
		JButton stop = new JButton("Stop");			//button to stop the sequencer
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		
		JButton upTempo = new JButton("Tempo Up");	//button to speed up the music
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		
		JButton downTempo = new JButton("Tempo Down");	//button to slow down the music
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		
		Box nameBox = new Box(BoxLayout.Y_AXIS);	//makes rows of checkboxes
		for (int i = 0; i < 16; i++){
			nameBox.add(new Label(instrumentNames[i]));
		}

		background.add(BorderLayout.EAST, buttonBox);	
		background.add(BorderLayout.WEST, nameBox);
		
		theFrame.getContentPane().add(background);	
		
		GridLayout grid = new GridLayout(16,16);	
		grid.setVgap(1);							//gap of 1 vertically
		grid.setHgap(2);							//gap of 2 horizontally
		mainPanel = new JPanel(grid);		
		background.add(BorderLayout.CENTER, mainPanel);
		
		for (int i = 0; i < 256; i++){				//for loop to place the checkboxes
			JCheckBox c = new JCheckBox();			//creates an object of the checkboxes
			c.setSelected(false);					//makes them default not selected
			checkboxList.add(c);					//adds to the checkboxlist
			mainPanel.add(c);						//adds to the mainpanel
		}
		
		setUpMidi();
		
		theFrame.setBounds(50,50,30,296);			//sets size
		theFrame.pack();					
		theFrame.setVisible(true);					//so we can see it
	}
	
	public void setUpMidi(){						//standard midi things
		try{
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ,4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120);
			
		}
		catch(Exception e){e.printStackTrace();}	//in case of errors
	}
	
	public void buildTrackAndStart(){
		int[] trackList = null;
		
		sequence.deleteTrack(track);				//deletes previous track
		track = sequence.createTrack();				//creates track
		
		for (int i = 0; i < 16; i++){				//creates track out of the instruments
			trackList= new int[16];
			
			int key = instruments[i];
			
			for (int j = 0; j < 16; j++){
				JCheckBox jc = checkboxList.get(j + 16*i);
				if (jc.isSelected()){
					trackList[j] = key;
				}
				else{
					trackList[j] = 0;
				}
			}
			
			makeTracks (trackList);
			track.add(makeEvent(176,1,127,0,16));
		}
		
		track.add(makeEvent(192,9,1,0,15));
		try{
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public class MyStartListener implements ActionListener{	//action listener for start button
		public void actionPerformed(ActionEvent a){
			buildTrackAndStart();
		}
		
	}
	
	public class MyStopListener implements ActionListener{	//action listener for stop button
		public void actionPerformed(ActionEvent a){
			sequencer.stop();
		}
	}
	
	public class MyUpTempoListener implements ActionListener{	//actionlistener for speedingup
		public void actionPerformed(ActionEvent a){
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor * 1.1));	//10% increase
		}
	}
	
	public class MyDownTempoListener implements ActionListener{	//actionlistener for speedingdown
		public void actionPerformed(ActionEvent a){
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor * 0.9));	//10% decrease
		}
	}


	public void makeTracks(int[] list){	
		for (int i = 0; i < 16; i++){
			int key = list[i];
			
			if (key != 0){
				track.add(makeEvent(144,9,key,100,i));
				track.add(makeEvent(128,9,key,100,i+1));
			}
				
		}
			
	}
	
	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
		MidiEvent event = null;
		try{
			ShortMessage a = new ShortMessage();
			a.setMessage(comd, chan, one, two);
			event = new MidiEvent(a, tick);
		}catch (Exception e) {e.printStackTrace();}
		return event;
		
	}
		
}