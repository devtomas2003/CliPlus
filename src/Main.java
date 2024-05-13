import Classes.*;
import Menus.Clients;
import Menus.Vets;

import javax.swing.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        GUI.showAutoAdmission();

        ArrayList<People> clients = new ArrayList<People>();
        ArrayList<Vet> vets = new ArrayList<Vet>();



        int optionSelected;

        do{
            optionSelected = Interactive.readInt( "Welcome to CliPlus\n\n1 - Clients\n2 - Vets\n3 - Animals\n4 - Appointments\n0 - Exit", "Menu");

            switch (optionSelected){
                case 1:
                    Clients.showMenu(clients);
                    break;
                case 2:
                    Vets.showMenu(vets, clients);
                    break;
                case 3:
                    Animals.showMenu(clients, vets);
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (optionSelected != 0);
    }

    private void loadData(ArrayList<People> clients){
        String dados = Files.readData("clients.csv");
    }
}