package Utils;

import Classes.Animal;
import Classes.Appointment;
import Classes.Client;
import Classes.People;
import Menus.Appointments;
import Menus.Clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class KioskServer implements Runnable {
    private ArrayList<Appointment> apps;
    private ArrayList<People> persons;

    // Constructor
    public KioskServer(ArrayList<Appointment> apps, ArrayList<People> pps) {
        this.apps = apps;
        this.persons = pps;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(2345)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String nif = in.readLine();
                    ArrayList<Animal> clientAnimals = new ArrayList<>();
                    Client cltSelected = null;

                    ArrayList<Client> clients = new ArrayList<Client>();
                    for(People pp : persons){
                        if(pp instanceof Client){
                            clients.add((Client) pp);
                        }
                    }

                    for(Client clt : clients){
                        if(clt.getNif() == Integer.parseInt(nif)){
                            cltSelected = clt;
                            clientAnimals.addAll(clt.getAnimals());
                            break;
                        }
                    }

                    ZoneId z = ZoneId.of("Europe/Lisbon");
                    LocalDateTime now = LocalDateTime.now(z);

                    ArrayList<Appointment> todayApps = new ArrayList<>();
                    for(int i = 0; i < apps.size(); i++){
                        for(Animal anm : clientAnimals){
                            if(apps.get(i).getAnimal().getId() == anm.getId()){
                                if(apps.get(i).getTimeSlot().getStartTime().toLocalDate().isAfter(now.toLocalDate()) || apps.get(i).getTimeSlot().getStartTime().toLocalDate().isEqual(now.toLocalDate())){
                                    if(apps.get(i).getAppoType() == Appointment.AppointmentType.Surgery){
                                        todayApps.add(apps.get(i));
                                        i += 3;
                                    }else{
                                        todayApps.add(apps.get(i));
                                    }
                                }
                            }
                        }
                    }

                    if (cltSelected != null) {
                        StringBuilder dataToSend = new StringBuilder(cltSelected.getName() + ",");

                        for (int i = 0; i < todayApps.size(); i++) {
                            Appointment app = todayApps.get(i);
                            dataToSend.append(app.getAnimal().getName()).append(",")
                                    .append(app.getAnimal().getSpecie()).append(",")
                                    .append(app.getTimeSlot().getStartTime()).append(",")
                                    .append(app.getTimeSlot().getEndTime()).append(",")
                                    .append(app.getAppoType()).append(",")
                                    .append(app.getVet().getName());

                            if (i != todayApps.size() - 1) {
                                dataToSend.append(",");
                            }
                        }

                        out.println(dataToSend.toString());
                    } else {
                        out.println("Client not found.");
                    }
                } catch (IOException e) {
                    System.err.println("Client connection error: " + e.getMessage());
                }
            }
        } catch (IOException ioe) {
            System.err.println("Cannot create KIOSK server: " + ioe.getMessage());
        }
    }
}