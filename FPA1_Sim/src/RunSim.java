import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RunSim {

	public static void main(String[] args) {
		
		// ################ Zeit messen ###########################
		
		DateTimeFormatter dtf_DateTimeObject = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		System.out.println("Ausführung 'RunSim' beginnt /" + dtf_DateTimeObject.format(LocalDateTime.now()));
		
		
		// ################ Parameter ###########################
		
		//Simulationen aktivieren/deaktivieren
		boolean b_Aufgabe_Generiere_Population = true;
		boolean b_Aufgabe_CountPositivProSimulation = true;
		boolean b_ZählePositivProFeld = true;
		boolean b_BurstError = false;
		
				
		//Generate i_arr2d_Population
		int i_Inzidenz = 300; // 7-Tage-i_Inzidenz der i_arr2d_Population aus 100.000
		float f_Clusterwahrscheinlichkeit = 0.0f; // Wahrscheinlichkeit, dass ein Infizierter einen anderen Infiziert und ein Cluster bildet.
		float i_PInfected = i_Inzidenz / 100000f; //Individuelle Infektionswahrscheinlichkeit (pro Person)
		
		//i_arr2d_Populationsgröße
		int x = 8; //Breite der i_arr2d_Population
		int y = 8; //Höhe der i_arr2d_Population
		
		//Simulationsarrays generieren
		int i_AnzahlSimulationen = 10000;
		int[] i_arr1d_ErgebnisSimulationen = new int[i_AnzahlSimulationen];
		int[][] i_arr2d_PopulationPosition = new int[x][y];
		
		
		// ################ Fortschrittsanzeige generieren ###########################
		//Aufgaben zählen
		int i_AnzahlAufgaben = 0;
		int i_ZählerAktuelleAufgabe = 0;
		
		//Boolean wird zu '1' oder '0' gecasted und addiert
		i_AnzahlAufgaben += (b_Aufgabe_Generiere_Population) ? 1 : 0;
		i_AnzahlAufgaben += (b_Aufgabe_CountPositivProSimulation) ? 1 : 0;
		i_AnzahlAufgaben += (b_ZählePositivProFeld) ? 1 : 0;
		i_AnzahlAufgaben += (b_BurstError) ? 1 : 0;
		
		//Anzahl der Outputschritte festlegen und den Simulationsindex errechnen
		int i_AnzahlSchritte = 20;
		int i_NextOutputIndex = 0;
		int[] i_arr1d_NextOutput = new int[i_AnzahlSchritte];
		for(int i_IndexSchritte=0; i_IndexSchritte<(i_AnzahlSchritte-1); i_IndexSchritte++) {
			i_arr1d_NextOutput[i_IndexSchritte] = i_AnzahlSimulationen / i_AnzahlSchritte * i_IndexSchritte;
		}
		
		System.out.println("Anzahl Aufgben: " + i_AnzahlAufgaben);
		
		// ################ Aufgabe_Generiere_Population ###########################
		if(b_Aufgabe_Generiere_Population) {
			i_ZählerAktuelleAufgabe++;
			i_NextOutputIndex = 0;
			System.out.println("Generiere i_arr2d_Population beginnt");
			for(int i=0; i<i_AnzahlSimulationen; i++) {
			
				i_arr1d_ErgebnisSimulationen[i]=Generate_i_arr2d_Population(x, y, i_Inzidenz, f_Clusterwahrscheinlichkeit, i_PInfected, b_BurstError, i_arr2d_PopulationPosition);
							
				if(i==i_arr1d_NextOutput[i_NextOutputIndex]) {
					PrintFortschritt("i_arr2d_Population generieren", i_AnzahlSimulationen, i, i_ZählerAktuelleAufgabe, i_AnzahlAufgaben, dtf_DateTimeObject);
					i_NextOutputIndex++;
				}
			}
			System.out.println("Generiere i_arr2d_Population beendet");
		}
		
		System.out.println("---");
		
		//Print i_arr2d_Population Stat
		System.out.println("Populatiosstatistik anzeigen. Position Positivfälle:");
		String Zeile = "";
		for(int a = 0; a < x; a++){
			for(int b = 0; b < y; b++){
				Zeile = Zeile + i_arr2d_PopulationPosition[a][b] + ",";
			}//End for y
			System.out.println(Zeile);
			Zeile = "";
		}//End for x
		
		System.out.println("---");
		// ################ Aufgabe_CountPositivProSimulation ###########################
		if(b_Aufgabe_CountPositivProSimulation) {
			System.out.println("Beginne Fehlerverteilung zählen");
		
			String s_OutputErgebnisSimulationen = "";
			i_ZählerAktuelleAufgabe++;
			i_NextOutputIndex = 0;
			for(int i=0; i<i_AnzahlSimulationen; i++) {
				s_OutputErgebnisSimulationen = s_OutputErgebnisSimulationen + i_arr1d_ErgebnisSimulationen[i] + ",";
			
				if(i==i_arr1d_NextOutput[i_NextOutputIndex]) {
					PrintFortschritt("Fehlerverteilung zählen", i_AnzahlSimulationen, i, i_ZählerAktuelleAufgabe, i_AnzahlAufgaben, dtf_DateTimeObject);
					i_NextOutputIndex++;
				}
			}
			System.out.println("Fehlerverteilung zählen beendet");
				
			// ################ Output ###########################
			int i_PopulationsAnzahl = x * y;
			float f_ErwarteteInfektionen = i_PopulationsAnzahl * i_PInfected;
			int i_MaxErwartung = (int) ((f_ErwarteteInfektionen+1) * (3 + Math.sqrt(Math.sqrt(Math.sqrt(i_AnzahlSimulationen)))));
			int[] i_arr1d_ZählerErgebnisse = new int[i_MaxErwartung];
		
			System.out.println("------------------------");
			System.out.println("Ergebnis");
			System.out.println("i_Inzidenz: " + i_Inzidenz + " / f_Clusterwahrscheinlichkeit: " + f_Clusterwahrscheinlichkeit + " / i_PInfected: " + i_PInfected);
			System.out.println("i_arr2d_Populationsgröße: " + x + "x" + y + " / Anzahl: " + i_PopulationsAnzahl);
			System.out.println("Erwartete Infektionen: " + f_ErwarteteInfektionen);
			System.out.println("Ergebnis Simulationen: " + s_OutputErgebnisSimulationen);
		
			System.out.println("i_PopulationsAnzahl: " + i_PopulationsAnzahl);
			System.out.println("f_ErwarteteInfektionen: " + f_ErwarteteInfektionen);
			System.out.println("i_MaxErwartung: " + i_MaxErwartung);
			int i_SummeAnzahlGruppen = 0;
			String s_PrüfstringVerteilung = "";
			
			//Count Ergebnis
			for(int i_ErgebnisWert=0; i_ErgebnisWert<i_MaxErwartung; i_ErgebnisWert++) {
				for(int i_IndexSim = 0; i_IndexSim < i_arr1d_ErgebnisSimulationen.length; i_IndexSim++){
					if(i_arr1d_ErgebnisSimulationen[i_IndexSim]==i_ErgebnisWert) {
						i_arr1d_ZählerErgebnisse[i_ErgebnisWert]++;
					}
				}//Ende for_i_IndexSim
			
				System.out.println("Anzahl Simulationen mit Ergebnis " + i_ErgebnisWert + ": " + i_arr1d_ZählerErgebnisse[i_ErgebnisWert]);
				i_SummeAnzahlGruppen = i_SummeAnzahlGruppen + i_arr1d_ZählerErgebnisse[i_ErgebnisWert];
				s_PrüfstringVerteilung = s_PrüfstringVerteilung + i_arr1d_ZählerErgebnisse[i_ErgebnisWert] + ",";
			}//Ende for_i_ErgebnisWert
		
			System.out.println("Gesamtzahl Simulationen " + i_SummeAnzahlGruppen);
			System.out.println("Prüfstring: " + s_PrüfstringVerteilung);
			
		}
		
		System.out.println("Ausführung 'RunSim' endete");
	}
	
	
	public static void PrintFortschritt(String in_Aufgabenname, int in_Fallanzahl, int in_Fallzähler, int in_i_ZählerAktuelleAufgabe, int in_i_AnzahlAufgaben, DateTimeFormatter dtf_DateTimeObject) {
		double d_FortschrittProzent = (double)(in_Fallzähler / (double)in_Fallanzahl);
		String s_Fortschrittsbalken = "[";
	
		for(int i_IndexSymbole=0; i_IndexSymbole<30; i_IndexSymbole++) {
			if((double)((double)i_IndexSymbole/(double)30)<d_FortschrittProzent) {
				s_Fortschrittsbalken = s_Fortschrittsbalken + "#";
			}
			else {
				s_Fortschrittsbalken = s_Fortschrittsbalken + "-";
			}
		}
		s_Fortschrittsbalken = s_Fortschrittsbalken + "]";
		System.out.println(in_Aufgabenname + " (" + in_i_ZählerAktuelleAufgabe + "/" + in_i_AnzahlAufgaben + ") / Fortschritt " + (int)(100*d_FortschrittProzent) + "% " + s_Fortschrittsbalken + "   " + dtf_DateTimeObject.format(LocalDateTime.now()));
	}
	
	
	public static int Generate_i_arr2d_Population(
			int in_x,
			int in_y,
			int in_i_Inzidenz,
			float in_f_Clusterwahrscheinlichkeit,
			float in_i_PInfected,
			boolean in_b_BurstError,
			int[][] in_i_arr2d_PopulationPosition
			){
	
		Random object_random = new Random();
		int i_InfectionCounter = 0;
		int[][] i_arr2d_Population = new int[in_x][in_y];
			
		//Infecti_arr2d_Population
		for(int x = 0; x < i_arr2d_Population.length; x++){
			if(in_x < 0) { //Kein Spam bei großen i_arr2d_Populationen
				System.out.println("Infiziere Zeile " + x);
			}
			
			for(int y = 0; y < i_arr2d_Population[x].length; y++){
				if(in_y < 0) { //Kein Spam bei großen i_arr2d_Populationen
					System.out.println("Infiziere Zeile " + x + ", Spalte " + y);
				}
							
				float f_Zufallszahl;
				//Generiere Zufallsfloat Zwischen 0.0 und 1.0
				f_Zufallszahl = object_random.nextFloat();
				
				//Vergleicht mit Inzidenz-Wahrscheinlichkeit
				if(f_Zufallszahl<in_i_PInfected) {
					//Wurde infiziert
					// System.out.println("####### INFIZIERT " + x + "/" + y + " ########");
					i_InfectionCounter++;
					in_i_arr2d_PopulationPosition[x][y]++;
				}//End If
				
			}//End for y
		
		}//End for x
		
		
		
		
		return i_InfectionCounter;
		
	}//End Generate i_arr2d_Population()
	

}
