package Menus;

import Classes.*;
import Utils.FilesDAT;

import javax.swing.*;
import java.util.ArrayList;

public class Database {
    public static void showMenu(ArrayList<People> persons, ArrayList<Appointment> apps) {
        int opcao;

        do {
            opcao = Interactive.readInt("Database Operations\n\n1 - Export .dat\n2 - Import .dat\n3 - Overwrite text files from memory\n0 - Previus Menu", "Animals");

            switch (opcao) {
                case 1:
                    ArrayList<Animal> anms = new ArrayList<Animal>();
                    ArrayList<Client> clients = new ArrayList<Client>();
                    ArrayList<Vet> vets = new ArrayList<Vet>();

                    for(People pp : persons){
                        if(pp instanceof Client){
                            anms.addAll(((Client) pp).getAnimals());
                            clients.add((Client) pp);
                        }else{
                            vets.add((Vet) pp);
                        }
                    }

                    FilesDAT.writerAppointments(apps);
                    FilesDAT.writerPeoples(persons);

                    JOptionPane.showMessageDialog(null, "All data exported with success!", "Objects Export", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 2:
                    persons.clear();
                    apps.clear();

                    persons.addAll(FilesDAT.readPeoples());
                    apps.addAll(FilesDAT.readAppointments());
                    JOptionPane.showMessageDialog(null, "All data imported with success!", "Objects Import", JOptionPane.INFORMATION_MESSAGE);

                    break;
                case 3:
                    Clients.ExportClients(persons);
                    Vets.ExportVets(persons);
                    Animal.ExportAllData(persons);
                    Appointments.saveAppointmentsInFile(apps);

                    JOptionPane.showMessageDialog(null, "All text files replaced with success!", "Text Files Replace", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao != 0);
    }
}
