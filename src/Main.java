import Classes.*;
import Menus.*;
import Utils.KioskServer;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        ArrayList<People> people = new ArrayList<People>();
        ArrayList<Appointment> apps = new ArrayList<Appointment>();

        loadData(people, apps);

        Thread serverThread = new Thread(new KioskServer(apps, people));
        serverThread.start();

        String username = Interactive.readString("Username", "Authentication");

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Password:");
        JPasswordField pass = new JPasswordField(10);
        panel.add(label);
        panel.add(pass);
        String[] options = new String[]{"OK", "Cancel"};
        String password = "";
        do{
            int option = JOptionPane.showOptionDialog(null, panel, "Authentication", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,null, options, options[1]);
            if(option == 0)
            {
                char[] passwordArr = pass.getPassword();
                password = new String(passwordArr);
            }
        }while (password.equals(""));

        if(Objects.equals(username, "admin") && Objects.equals(password, "admin")){
            int optionSelected;

            do{
                optionSelected = Interactive.readInt( "Welcome to CliPlus\n\n1 - Clients\n2 - Vets\n3 - Animals\n4 - Appointments\n5 - Database\n0 - Exit", "Menu");

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
                    case 4:
                        Appointments.showMenu(people, apps);
                        break;
                    case 5:
                        Database.showMenu(people, apps);
                        break;
                    case 0:
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
                }
            }while (optionSelected != 0);
        }else{
            JOptionPane.showMessageDialog(null, "Wrong username/password", "Authentication", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void loadData(ArrayList<People> people, ArrayList<Appointment> apps){
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
            Animal anm = new Animal(Integer.parseInt(lineAnimal[1]), lineAnimal[0], lineAnimal[3], lineAnimal[2], Float.parseFloat(lineAnimal[4]), Boolean.parseBoolean(lineAnimal[7]));
            Client clt = Clients.findClient(Integer.parseInt(lineAnimal[5]), people);
            clt.addAnimal(anm);
            int codVet = Integer.parseInt(lineAnimal[6]);
            Vet vetData = Vets.findVetByOMV(codVet, people);
            vetData.addAnimal(anm.getId());
        }
        String dadosAppointments = Files.readData("appointments.csv");
        String linesApps[] = dadosAppointments.split("\n");
        for(int i = 0; i < linesApps.length; i++){
            String linesApp[] = linesApps[i].split(",");

            ArrayList<Animal> anms = new ArrayList<Animal>();

            for(People pp : people){
                if(pp instanceof Client){
                    anms.addAll(((Client) pp).getAnimals());
                }
            }

            Animal anm = Animals.findAnimal(Integer.parseInt(linesApp[3]), anms);

            Appointment.AppointmentType aptType = Appointment.AppointmentType.valueOf(linesApp[0]);
            Appointment.AppointmentLocation aptLocal = Appointment.AppointmentLocation.valueOf(linesApp[1]);

            LocalDateTime lStart = LocalDateTime.parse(linesApp[4]);
            LocalDateTime lEnd = LocalDateTime.parse(linesApp[5]);

            Slot slt = new Slot(lStart, lEnd);

            int codVet = Integer.parseInt(linesApp[2]);
            Vet vetData = Vets.findVetByNif(codVet, people);

            Appointment appt = new Appointment(aptType, aptLocal, anm, slt, vetData);
            appt.setDistance(Double.parseDouble(linesApp[6]));
            apps.add(appt);
        }
    }
}