import Classes.*;
import Menus.Animals;
import Menus.Clients;
import Menus.Vets;

import javax.swing.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        GUI.showAutoAdmission();

        ArrayList<People> people = new ArrayList<People>();

        loadData(people);

        int optionSelected;

        do{
            optionSelected = Interactive.readInt( "Welcome to CliPlus\n\n1 - Clients\n2 - Vets\n3 - Animals\n4 - Appointments\n0 - Exit", "Menu");

            switch (optionSelected){
                case 1:
                    Clients.showMenu(people);
                    break;
                case 2:
                    Vets.showMenu(people);
                    break;
                case 3:
                    Animals.showMenu(people);
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (optionSelected != 0);
    }

    private static void loadData(ArrayList<People> people){
        String dadosClients = Files.readData("clients.csv");
        String linesClients[] = dadosClients.split("\n");
        for(int i = 0; i < linesClients.length; i++){
            String lineClt[] = linesClients[i].split(",");
            Client clt = new Client(Integer.parseInt(lineClt[0]), lineClt[1], lineClt[2]);
            Address addr = new Address(lineClt[3], Integer.parseInt(lineClt[5]), Integer.parseInt(lineClt[4]), lineClt[6]);
            clt.setAddress(addr);
            people.add(clt);
        }
        String dados = Files.readData("vets.csv");
        String lines[] = dados.split("\n");
        for(int i = 0; i < lines.length; i++){
            String line[] = lines[i].split(",");
            Vet vt = new Vet(Integer.parseInt(line[0]), line[1], Integer.parseInt(line[7]), line[2]);
            Address addr = new Address(line[3], Integer.parseInt(line[5]), Integer.parseInt(line[4]), line[6]);
            vt.setAddress(addr);
            people.add(vt);
        }
        String dadosAnimals = Files.readData("animals.csv");
        String linesAnimals[] = dadosAnimals.split("\n");
        for(int i = 0; i < linesAnimals.length; i++){
            String lineAnimal[] = linesAnimals[i].split(",");
            Animal anm = new Animal(Integer.parseInt(lineAnimal[1]), lineAnimal[0], lineAnimal[3], lineAnimal[2], Float.parseFloat(lineAnimal[4]));
            Client clt = Clients.findClient(Integer.parseInt(lineAnimal[5]), people);
            clt.addAnimal(anm);
        }
    }
}