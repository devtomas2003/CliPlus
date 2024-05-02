import Classes.People;
import Menus.Animals;
import Menus.Clients;

import javax.swing.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<People> clients = new ArrayList<People>();

        int optionSelected;

        do{
            optionSelected = Integer.parseInt(JOptionPane.showInputDialog(null, "Welcome to CliPlus\n\n1 - Clients\n2 - Vets\n3 - Animals\n4 - Appointments\n0 - Exit", "Menu", JOptionPane.INFORMATION_MESSAGE));

            switch (optionSelected){
                case 1:
                    Clients.showMenu(clients);
                    break;
                case 2:

                    break;
                case 3:
                    Animals.showMenu(clients);
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (optionSelected != 0);
    }
}