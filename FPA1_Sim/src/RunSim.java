import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RunSim {

	public static void main(String[] args) {
		
		// ################ Zeit messen ###########################
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		System.out.println("Ausf�hrung 'RunSim' beginnt /" + dtf.format(LocalDateTime.now()));
		
		
		// ################ Parameter ###########################
		
		//Simulationen aktivieren/deaktivieren
		boolean b_GenerierePopulation = true;
		boolean b_Z�hlePositivProSimulationFehleranzahl = true;
		boolean b_Z�hlePositivProFeld = true;
		boolean b_BurstError = false;
		
				
		//Generate Population
		int Inzidenz = 300; // 7-Tage-Inzidenz der Population aus 100.000
		float P_Cluster = 0.0f; // Wahrscheinlichkeit, dass ein Infizierter einen anderen Infiziert und ein Cluster bildet.
		float P_Infected = Inzidenz / 100000f;
		
		//Verfahrensdaten
		int x = 8;
		int y = 8;
		
		int AnzahlSimulationen = 1000000;
		int[] ErgebnisSimulationen = new int[AnzahlSimulationen];
		
		int[][] PopulationPosition = new int[x][y];
		
		
		// ################ Fortschrittsanzeige generieren ###########################
		int AnzahlAufgaben = 0;
		int Z�hlerAufgabe = 0;
		AnzahlAufgaben += (b_GenerierePopulation) ? 1 : 0;
		AnzahlAufgaben += (b_Z�hlePositivProSimulationFehleranzahl) ? 1 : 0;
		AnzahlAufgaben += (b_Z�hlePositivProFeld) ? 1 : 0;
		AnzahlAufgaben += (b_BurstError) ? 1 : 0;
		int nOi = 0;
		int AnzahlSchritte = 20;
		int[] nO = new int[AnzahlSchritte];
		for(int h=0; h<(AnzahlSchritte-1); h++) {
			nO[h] = AnzahlSimulationen / AnzahlSchritte * h;
		}
		
		System.out.println("Anzahl Aufgben: " + AnzahlAufgaben);
		
		// ################ Population Generieren ###########################
		if(b_GenerierePopulation) {
			Z�hlerAufgabe++;
			nOi = 0;
			System.out.println("Generiere Population beginnt");
			for(int i=0; i<AnzahlSimulationen; i++) {
			
				ErgebnisSimulationen[i]=GeneratePopulation(x, y, Inzidenz, P_Cluster, P_Infected, b_BurstError, PopulationPosition);
							
				if(i==nO[nOi]) {
					PrintFortschritt("Population generieren", AnzahlSimulationen, i, Z�hlerAufgabe, AnzahlAufgaben, dtf);
					nOi++;
				}
			}
			System.out.println("Generiere Population beendet");
		}
		
		//Print Population Stat
		String Zeile = "";
		for(int a = 0; a < x; a++){
			for(int b = 0; b < y; b++){
				Zeile = Zeile + PopulationPosition[a][b] + ",";
			}//End for y
			System.out.println(Zeile);
			Zeile = "";
		}//End for x
		
		
		// ################ Ausz�hlung ###########################
		if(b_Z�hlePositivProSimulationFehleranzahl) {
			System.out.println("Beginne Fehlerverteilung z�hlen");
		
			String s_ErgebnisSimulationen = "";
			Z�hlerAufgabe++;
			nOi = 0;
			for(int i=0; i<AnzahlSimulationen; i++) {
				s_ErgebnisSimulationen = s_ErgebnisSimulationen + ErgebnisSimulationen[i] + ",";
			
				if(i==nO[nOi]) {
					PrintFortschritt("Fehlerverteilung z�hlen", AnzahlSimulationen, i, Z�hlerAufgabe, AnzahlAufgaben, dtf);
					nOi++;
				}
			}
		
			
			System.out.println("Fehlerverteilung z�hlen beendet");
		
			s_ErgebnisSimulationen = "Ergebnis Simulationen: " + s_ErgebnisSimulationen + ")";
		
			// ################ Output ###########################
			int PopulationsAnzahl = x * y;
			float ErwarteteInfektionen = PopulationsAnzahl * P_Infected;
			int MaxErwartung = (int) ((ErwarteteInfektionen+1) * (3 + Math.sqrt(Math.sqrt(Math.sqrt(AnzahlSimulationen)))));
			int[] Z�hlerErgebnisse = new int[MaxErwartung];
		
			System.out.println("------------------------");
			System.out.println("Ergebnis");
			System.out.println("Inzidenz: " + Inzidenz + " / P_Cluster: " + P_Cluster + " / P_Infected: " + P_Infected);
			System.out.println("Populationsgr��e: " + x + "x" + y + " / Anzahl: " + PopulationsAnzahl);
			System.out.println("Erwartete Infektionen: " + ErwarteteInfektionen);
			System.out.println(s_ErgebnisSimulationen);
		
		
			System.out.println("PopulationsAnzahl: " + PopulationsAnzahl);
			System.out.println("ErwarteteInfektionen: " + ErwarteteInfektionen);
			System.out.println("MaxErwartung: " + MaxErwartung);
			int SummeAnzahlGruppen = 0;
			String Pr�fstringVerteilung = "";
			//Count Ergebnis
			for(int i=0; i<MaxErwartung; i++) {
			
			for(int SimNr = 0; SimNr < ErgebnisSimulationen.length; SimNr++){
				if(ErgebnisSimulationen[SimNr]==i) {
					Z�hlerErgebnisse[i]++;
				}
			}//End for SimNr
			System.out.println("Anzahl Simulationen mit Ergebnis " + i + ": " + Z�hlerErgebnisse[i]);
			SummeAnzahlGruppen = SummeAnzahlGruppen + Z�hlerErgebnisse[i];
			Pr�fstringVerteilung = Pr�fstringVerteilung + Z�hlerErgebnisse[i] + ",";
			}
		
			System.out.println("Gesamtzahl Simulationen " + SummeAnzahlGruppen);
			System.out.println("Pr�fstring: " + Pr�fstringVerteilung);
			
		}
		
		System.out.println("Ausf�hrung 'RunSim' endete");
	}
	
	
	public static void PrintFortschritt(String in_Aufgabenname, int in_Fallanzahl, int in_Fallz�hler, int in_Z�hlerAufgabe, int in_AnzahlAufgaben, DateTimeFormatter dtf) {
		double Percent = (double)(in_Fallz�hler / (double)in_Fallanzahl);
		String Fortschrittsbalken = "[";
	
		for(int Sym=0; Sym<30; Sym++) {
			if((double)((double)Sym/(double)30)<Percent) {
				Fortschrittsbalken = Fortschrittsbalken + "#";
			}
			else {
				Fortschrittsbalken = Fortschrittsbalken + "-";
			}
		}
		Fortschrittsbalken = Fortschrittsbalken + "]";
		System.out.println(in_Aufgabenname + " (" + in_Z�hlerAufgabe + "/" + in_AnzahlAufgaben + ") / Fortschritt " + (int)(100*Percent) + "% " + Fortschrittsbalken + "   " + dtf.format(LocalDateTime.now()));
	}
	
	
	public static int GeneratePopulation(int in_x, int in_y, int in_Inzidenz, float in_P_Cluster, float in_P_Infected, boolean in_b_BurstError, int[][] PopulationPosition){
	
		Random random = new Random();
		int InfectionCounter = 0;
		int[][] Population = new int[in_x][in_y];
			
	
		//InfectPopulation
		for(int x = 0; x < Population.length; x++){
			if(in_x < 0) { //Kein Spam bei gro�en Populationen
				System.out.println("Infiziere Zeile " + x);
			}
			
			
			for(int y = 0; y < Population[x].length; y++){
				if(in_y < 0) { //Kein Spam bei gro�en Populationen
					System.out.println("Infiziere Zeile " + x + ", Spalte " + y);
				}
							
				
				float Zufallszahl;
				//Generiere Zufallsfloat Zwischen 0.0 und 1.0
				Zufallszahl = random.nextFloat();
				
				//Vergleicht mit Inzidenz-Wahrscheinlichkeit
				if(Zufallszahl<in_P_Infected) {
					//Wurde infiziert
					// System.out.println("####### INFIZIERT " + x + "/" + y + " ########");
					InfectionCounter++;
					PopulationPosition[x][y]++;
				}//End If
				
			}//End for y
		
		}//End for x
		
		
		
		
		return InfectionCounter;
		
	}//End GeneratePopulation()
	

}
