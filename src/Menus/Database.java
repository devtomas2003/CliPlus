package Menus;

import Classes.*;
import Utils.FilesDAT;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class Database {
    public static void showMenu(ArrayList<People> persons, ArrayList<Appointment> apps) {
        int opcao;

        do {
            opcao = Interactive.readInt("Database Operations\n\n1 - Export .dat\n2 - Import .dat\n3 - Overwrite text files from memory\n0 - Previus Menu", "Animals");

            switch (opcao) {
                case 1:
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Please select a folder to save the files");
                    fc.setCurrentDirectory(new File("."));
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fc.showSaveDialog(null);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                        File folder = fc.getSelectedFile();
                        FilesDAT.writerAppointments(apps, folder);
                        FilesDAT.writerPeoples(persons, folder);

                        JOptionPane.showMessageDialog(null, "All data exported with success!", "Objects Export", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                case 2:
                    File peoplesFile = null;
                    File appsFile = null;

                    JFileChooser fcReaderClients = new JFileChooser();
                    fcReaderClients.setDialogTitle("Please select the people file to load");
                    fcReaderClients.setCurrentDirectory(new File("."));
                    fcReaderClients.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnValClts = fcReaderClients.showDialog(null, "Open");

                    if(returnValClts == JFileChooser.APPROVE_OPTION) {
                        peoplesFile = fcReaderClients.getSelectedFile();
                    }

                    JFileChooser fcReaderApss = new JFileChooser();
                    fcReaderApss.setDialogTitle("Please select the appointments file to load");
                    fcReaderApss.setCurrentDirectory(new File("."));
                    fcReaderApss.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnValApps = fcReaderApss.showDialog(null, "Open");

                    if(returnValApps == JFileChooser.APPROVE_OPTION) {
                        appsFile = fcReaderApss.getSelectedFile();
                    }

                    if(appsFile != null && peoplesFile != null){
                        persons.clear();
                        apps.clear();

                        persons.addAll(FilesDAT.readPeoples(peoplesFile));
                        apps.addAll(FilesDAT.readAppointments(appsFile));
                        JOptionPane.showMessageDialog(null, "All data imported with success!", "Objects Import", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "At least one file cannot be load!", "Objects Import Error", JOptionPane.ERROR_MESSAGE);
                    }
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
