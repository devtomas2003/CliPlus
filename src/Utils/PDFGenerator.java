package Utils;

import Classes.*;
import Menus.Appointments;
import Menus.Clients;
import Menus.Vets;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

public class PDFGenerator {
    private static final ZoneId z = ZoneId.of("Europe/Lisbon");
    private static final DecimalFormat formato = new DecimalFormat("#.##");
    public static void reportByVetAndDate(ArrayList<Appointment> apps, ArrayList<People> persons){
        Vet vetDataDay = null;

        do{
            int vetOmvID = Interactive.readInt("Please insert the OMV ID (0 - To Cancel)", "Find Vet", 0);
            if(vetOmvID == 0){
                break;
            }else{
                vetDataDay = Vets.findVetByOMV(vetOmvID, persons);
                if(vetDataDay == null){
                    JOptionPane.showMessageDialog(null, "This OMV ID does not exists!", "Find Vet", JOptionPane.ERROR_MESSAGE);
                }
            }
        }while (vetDataDay == null);

        if(vetDataDay == null){
            return;
        }

        DateTimeFormatter formatterDayVet = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        DateTimeFormatter formatterTimeVetDay = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime lDateForVet = Interactive.readDate("Generate day report for vet", true, true);
        lDateForVet.format(formatterDayVet);

        try{
            Document document = new Document();
            Date now = new Date();
            Long longTime = now.getTime()/1000;
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();

                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                PdfPTable tableNormal = new PdfPTable(6);
                PdfPTable tableVacination = new PdfPTable(6);
                PdfPTable tableSurgery = new PdfPTable(6);

                Stream.of("Time Slot", "Animal Name (Chip ID)", "Owner (Contact)", "OnSite/Remote", "Specie", "Weight (KG)")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Paragraph(columnTitle, fontTable));
                            tableNormal.addCell(header);
                            tableVacination.addCell(header);
                            tableSurgery.addCell(header);
                        });
                for(int i = 0; i < apps.size(); i++){
                    if(apps.get(i).getVet().getIdOV() == vetDataDay.getIdOV() && apps.get(i).getTimeSlot().getStartTime().toLocalDate().equals(lDateForVet.toLocalDate()))
                        if(apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation){
                            tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTimeVetDay) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTimeVetDay), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            Client clt = null;
                            for(People pp : persons){
                                if(pp instanceof Client){
                                    if(((Client) pp).getAnimals().contains(apps.get(i).getAnimal())){
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            tableNormal.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableNormal.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                        }else if(apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination){
                            tableVacination.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTimeVetDay) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTimeVetDay), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            Client clt = null;
                            for(People pp : persons){
                                if(pp instanceof Client){
                                    if(((Client) pp).getAnimals().contains(apps.get(i).getAnimal())){
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            tableVacination.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableVacination.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                        }else{
                            Client clt = null;
                            for(People pp : persons){
                                if(pp instanceof Client){
                                    if(((Client) pp).getAnimals().contains(apps.get(i).getAnimal())){
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                            tableSurgery.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTimeVetDay) + " - " + sEnd.format(formatterTimeVetDay), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            tableSurgery.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableSurgery.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                            i += 3;
                        }
                }

                tableNormal.setWidthPercentage(100);
                tableNormal.setSpacingBefore(10);
                tableVacination.setWidthPercentage(100);
                tableVacination.setSpacingBefore(10);
                tableSurgery.setWidthPercentage(100);
                tableSurgery.setSpacingBefore(10);

                document.add(new Paragraph("CliPlus - Vet and Date Report By Types", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Interventions for " + vetDataDay.getName() + " (" + vetDataDay.getIdOV() + ") at " + lDateForVet.toLocalDate().format(formatterDayVet), font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Normal", font));
                document.add(tableNormal);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Vacination", font));
                document.add(tableVacination);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Surgery", font));
                document.add(tableSurgery);

                DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtCh = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtCh.format(formatterTime), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);

                document.close();

                Desktop.getDesktop().open(pdfFile);
            }

        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void reportByVet(ArrayList<Appointment> apps, ArrayList<People> persons){
        DateTimeFormatter formatterTimeVet = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Vet vetData = null;

        do{
            int vetOmvID = Interactive.readInt("Please insert the OMV ID (0 - To Cancel)", "Find Vet", 0);
            if(vetOmvID == 0){
                break;
            }else{
                vetData = Vets.findVetByOMV(vetOmvID, persons);
                if(vetData == null){
                    JOptionPane.showMessageDialog(null, "This OMV ID does not exists!", "Find Vet", JOptionPane.ERROR_MESSAGE);
                }
            }
        }while (vetData == null);

        if(vetData == null){
            return;
        }

        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                PdfPTable tableNormal = new PdfPTable(6);
                PdfPTable tableVacination = new PdfPTable(6);
                PdfPTable tableSurgery = new PdfPTable(6);

                Stream.of("Time Slot", "Animal Name (Chip ID)", "Owner (Contact)", "OnSite/Remote", "Specie", "Weight (KG)")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Paragraph(columnTitle, fontTable));
                            tableNormal.addCell(header);
                            tableVacination.addCell(header);
                            tableSurgery.addCell(header);
                        });
                for (int i = 0; i < apps.size(); i++) {
                    if (apps.get(i).getVet().getIdOV() == vetData.getIdOV()) {
                        if (apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation) {
                            tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTimeVet) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTimeVet), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            Client clt = null;
                            for (People pp : persons) {
                                if (pp instanceof Client) {
                                    if (((Client) pp).getAnimals().contains(apps.get(i).getAnimal())) {
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            tableNormal.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableNormal.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                        } else if (apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination) {
                            tableVacination.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTimeVet) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTimeVet), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            Client clt = null;
                            for (People pp : persons) {
                                if (pp instanceof Client) {
                                    if (((Client) pp).getAnimals().contains(apps.get(i).getAnimal())) {
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            tableVacination.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableVacination.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                        } else {
                            Client clt = null;
                            for (People pp : persons) {
                                if (pp instanceof Client) {
                                    if (((Client) pp).getAnimals().contains(apps.get(i).getAnimal())) {
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                            tableSurgery.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTimeVet) + " - " + sEnd.format(formatterTimeVet), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            tableSurgery.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableSurgery.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                            i += 3;
                        }
                    }
                }

                tableNormal.setWidthPercentage(100);
                tableNormal.setSpacingBefore(10);
                tableVacination.setWidthPercentage(100);
                tableVacination.setSpacingBefore(10);
                tableSurgery.setWidthPercentage(100);
                tableSurgery.setSpacingBefore(10);

                document.add(new Paragraph("CliPlus - Vet Report By Types", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Interventions for " + vetData.getName() + " (" + vetData.getIdOV() + ")", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Normal", font));
                document.add(tableNormal);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Vacination", font));
                document.add(tableVacination);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Surgery", font));
                document.add(tableSurgery);
                DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtCh = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtCh.format(formatterTime), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void reportByDate(ArrayList<Appointment> apps, ArrayList<People> persons){
        LocalDateTime lDate = Interactive.readDate("Generate day report", true, true);
        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                PdfPTable tableNormal = new PdfPTable(7);
                PdfPTable tableVacination = new PdfPTable(7);
                PdfPTable tableSurgery = new PdfPTable(7);

                Stream.of("Time Slot", "Vet Name", "Animal Name (Chip ID)", "Owner (Contact)", "OnSite/Remote", "Specie", "Weight (KG)")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Paragraph(columnTitle, fontTable));
                            tableNormal.addCell(header);
                            tableVacination.addCell(header);
                            tableSurgery.addCell(header);
                        });
                for (int i = 0; i < apps.size(); i++) {
                    if (apps.get(i).getTimeSlot().getStartTime().toLocalDate().equals(lDate.toLocalDate())) {
                        if (apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation) {
                            tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            Client clt = null;
                            for (People pp : persons) {
                                if (pp instanceof Client) {
                                    if (((Client) pp).getAnimals().contains(apps.get(i).getAnimal())) {
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            tableNormal.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableNormal.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                        } else if (apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination) {
                            tableVacination.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            Client clt = null;
                            for (People pp : persons) {
                                if (pp instanceof Client) {
                                    if (((Client) pp).getAnimals().contains(apps.get(i).getAnimal())) {
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            tableVacination.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableVacination.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                        } else {
                            Client clt = null;
                            for (People pp : persons) {
                                if (pp instanceof Client) {
                                    if (((Client) pp).getAnimals().contains(apps.get(i).getAnimal())) {
                                        clt = (Client) pp;
                                    }
                                }
                            }
                            LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                            tableSurgery.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTime) + " - " + sEnd.format(formatterTime), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                            tableSurgery.addCell(new Paragraph(clt.getName() + " (" + clt.getContact() + ")", fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAnimal().getSpecie(), fontTable));
                            tableSurgery.addCell(new Paragraph(formato.format(apps.get(i).getAnimal().getWeight()), fontTable));
                            i += 3;
                        }
                    }
                }

                tableNormal.setWidthPercentage(100);
                tableNormal.setSpacingBefore(10);
                tableVacination.setWidthPercentage(100);
                tableVacination.setSpacingBefore(10);
                tableSurgery.setWidthPercentage(100);
                tableSurgery.setSpacingBefore(10);

                document.add(new Paragraph("CliPlus - Day Report By Types", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Interventions at " + lDate.toLocalDate().format(formatterDay), font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Normal", font));
                document.add(tableNormal);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Vacination", font));
                document.add(tableVacination);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Surgery", font));
                document.add(tableSurgery);
                DateTimeFormatter formatterTimeSig = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtCh = LocalDateTime.now(z);

                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtCh.format(formatterTimeSig), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void reportPastInterventionsByAnimal(ArrayList<Appointment> apps, Animal anm, Client clt){
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dtCh = LocalDateTime.now(z).minusDays(1);

        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                PdfPTable tableNormal = new PdfPTable(3);
                PdfPTable tableVacination = new PdfPTable(3);
                PdfPTable tableSurgery = new PdfPTable(3);

                Stream.of("Time Slot", "Vet Name", "OnSite/Remote")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Paragraph(columnTitle, fontTable));
                            tableNormal.addCell(header);
                            tableVacination.addCell(header);
                            tableSurgery.addCell(header);
                        });

                for (int i = 0; i < apps.size(); i++) {
                    if (apps.get(i).getAnimal().getId() == anm.getId() && apps.get(i).getTimeSlot().getStartTime().toLocalDate().isBefore(dtCh.toLocalDate())) {
                        if (apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation) {
                            tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                        } else if (apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination) {
                            tableVacination.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                        } else {
                            LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                            tableSurgery.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTime) + " - " + sEnd.format(formatterTime), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            i += 3;
                        }
                    }
                }

                tableNormal.setWidthPercentage(100);
                tableNormal.setSpacingBefore(10);
                tableVacination.setWidthPercentage(100);
                tableVacination.setSpacingBefore(10);
                tableSurgery.setWidthPercentage(100);
                tableSurgery.setSpacingBefore(10);
                document.add(new Paragraph("CliPlus - Past Animal Interventions Report By Types", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Interventions for " + anm.getName() + " (" + anm.getId() + ")", font));
                document.add(new Paragraph("Owner: " + clt.getName(), font));
                document.add(new Paragraph("Specie: " + anm.getSpecie(), font));
                document.add(new Paragraph("Weight: " + formato.format(anm.getWeight()), font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Normal", font));
                document.add(tableNormal);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Vacination", font));
                document.add(tableVacination);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Surgery", font));
                document.add(tableSurgery);
                DateTimeFormatter formatterTimeSig = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime sign = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + sign.format(formatterTimeSig), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + fileName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void reportTodayInterventionsByAnimal(ArrayList<Appointment> apps, Animal anm, Client clt){
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dtCh = LocalDateTime.now(z);

        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                PdfPTable tableNormal = new PdfPTable(3);
                PdfPTable tableVacination = new PdfPTable(3);
                PdfPTable tableSurgery = new PdfPTable(3);

                Stream.of("Time Slot", "Vet Name", "OnSite/Remote")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Paragraph(columnTitle, fontTable));
                            tableNormal.addCell(header);
                            tableVacination.addCell(header);
                            tableSurgery.addCell(header);
                        });

                for (int i = 0; i < apps.size(); i++) {
                    if (apps.get(i).getAnimal().getId() == anm.getId() && apps.get(i).getTimeSlot().getStartTime().toLocalDate().equals(dtCh.toLocalDate())) {
                        if (apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation) {
                            tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                        } else if (apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination) {
                            tableVacination.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                        } else {
                            LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                            tableSurgery.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTime) + " - " + sEnd.format(formatterTime), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            i += 3;
                        }
                    }
                }

                tableNormal.setWidthPercentage(100);
                tableNormal.setSpacingBefore(10);
                tableVacination.setWidthPercentage(100);
                tableVacination.setSpacingBefore(10);
                tableSurgery.setWidthPercentage(100);
                tableSurgery.setSpacingBefore(10);
                document.add(new Paragraph("CliPlus - Today Animal Interventions Report By Types", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Interventions for " + anm.getName() + " (" + anm.getId() + ")", font));
                document.add(new Paragraph("Owner: " + clt.getName(), font));
                document.add(new Paragraph("Specie: " + anm.getSpecie(), font));
                document.add(new Paragraph("Weight: " + formato.format(anm.getWeight()), font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Normal", font));
                document.add(tableNormal);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Vacination", font));
                document.add(tableVacination);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Surgery", font));
                document.add(tableSurgery);
                DateTimeFormatter formatterTimeSign = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtChSign = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtChSign.format(formatterTimeSign), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void reportFutureInterventionsByAnimal(ArrayList<Appointment> apps, Animal anm, Client clt){
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dtCh = LocalDateTime.now(z).plusDays(1);

        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                PdfPTable tableNormal = new PdfPTable(3);
                PdfPTable tableVacination = new PdfPTable(3);
                PdfPTable tableSurgery = new PdfPTable(3);

                Stream.of("Time Slot", "Vet Name", "OnSite/Remote")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Paragraph(columnTitle, fontTable));
                            tableNormal.addCell(header);
                            tableVacination.addCell(header);
                            tableSurgery.addCell(header);
                        });

                for (int i = 0; i < apps.size(); i++) {
                    if (apps.get(i).getAnimal().getId() == anm.getId() && apps.get(i).getTimeSlot().getStartTime().toLocalDate().isAfter(dtCh.toLocalDate())) {
                        if (apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation) {
                            tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                        } else if (apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination) {
                            tableVacination.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableVacination.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                        } else {
                            LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                            tableSurgery.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTime) + " - " + sEnd.format(formatterTime), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                            tableSurgery.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                            i += 3;
                        }
                    }
                }

                tableNormal.setWidthPercentage(100);
                tableNormal.setSpacingBefore(10);
                tableVacination.setWidthPercentage(100);
                tableVacination.setSpacingBefore(10);
                tableSurgery.setWidthPercentage(100);
                tableSurgery.setSpacingBefore(10);
                document.add(new Paragraph("CliPlus - Future Animal Interventions Report By Types", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Interventions for " + anm.getName() + " (" + anm.getId() + ")", font));
                document.add(new Paragraph("Owner: " + clt.getName(), font));
                document.add(new Paragraph("Specie: " + anm.getSpecie(), font));
                document.add(new Paragraph("Weight: " + formato.format(anm.getWeight()), font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Normal", font));
                document.add(tableNormal);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Vacination", font));
                document.add(tableVacination);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Surgery", font));
                document.add(tableSurgery);
                DateTimeFormatter formatterTimeZone = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtChSign = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtChSign.format(formatterTimeZone), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void reportTodayAndPastCostsByClient(ArrayList<Appointment> apps, ArrayList<Animal> anms, Client clt){
        LocalDateTime dtCh = LocalDateTime.now(z);

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                document.add(new Paragraph("CliPlus - Last Costs By Client", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Costs for " + clt.getName() + " (" + clt.getNif() + ")", font));
                document.add(new Paragraph("Contact: " + clt.getContact(), font));
                document.add(new Paragraph("Address Line One: " + clt.getAddress().getNstreet() + ", nº" + clt.getAddress().getndoor(), font));
                StringBuilder sb = new StringBuilder();

                String ccPostal = Integer.toString(clt.getAddress().getZipCode());
                char[] ccPostalArr = ccPostal.toCharArray();

                for (int i = 0; i < ccPostalArr.length; i++) {
                    if (i == 3) {
                        sb.append(ccPostalArr[i]).append("-");
                    } else {
                        sb.append(ccPostalArr[i]);
                    }
                }

                document.add(new Paragraph("Address Line Two: " + clt.getAddress().getNlocality() + " (" + sb.toString() + ")", font));
                document.add(Chunk.createWhitespace(""));

                double masterTotal = 0;
                for (Animal anm : anms) {
                    PdfPTable tableNormal = new PdfPTable(5);
                    double animalTotal = 0;
                    Stream.of("Time Slot", "Vet Name", "Appointment Type", "OnSite/Remote", "Total")
                            .forEach(columnTitle -> {
                                PdfPCell header = new PdfPCell();
                                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                                header.setBorderWidth(1);
                                header.setPhrase(new Paragraph(columnTitle, fontTable));
                                tableNormal.addCell(header);
                            });
                    for (int i = 0; i < apps.size(); i++) {
                        if (apps.get(i).getAnimal().getId() == anm.getId()) {
                            if (apps.get(i).getTimeSlot().getStartTime().isBefore(dtCh)) {
                                double totalToPay = 0;

                                if (apps.get(i).getAppoLocal() == Appointment.AppointmentLocation.Remote) {
                                    totalToPay = 40 + apps.get(i).getDistance();
                                }

                                if (apps.get(i).getAppoType() == Appointment.AppointmentType.Surgery) {
                                    if (anm.getWeight() >= 10) {
                                        totalToPay += 400;
                                    } else {
                                        totalToPay += 200;
                                    }
                                }

                                if (apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination) {
                                    if (anm.getWeight() >= 10) {
                                        totalToPay += 50;
                                    } else {
                                        totalToPay += 100;
                                    }
                                }

                                if (apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation) {
                                    if (anm.getWeight() >= 10) {
                                        totalToPay += 25;
                                    } else {
                                        totalToPay += 50;
                                    }
                                }

                                totalToPay *= 1.23;
                                animalTotal += totalToPay;

                                if (apps.get(i).getAppoType() == Appointment.AppointmentType.Surgery) {
                                    LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                                    tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTime) + " - " + sEnd.format(formatterTime), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getAppoType().toString(), fontTable));
                                    tableNormal.addCell(new Paragraph(formato.format(totalToPay), fontTable));
                                    i += 3;
                                } else {
                                    tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getAppoType().toString(), fontTable));
                                    tableNormal.addCell(new Paragraph(formato.format(totalToPay) + " €", fontTable));
                                }
                            }
                        }
                    }
                    tableNormal.setWidthPercentage(100);
                    tableNormal.setSpacingBefore(10);

                    document.add(new Paragraph(anm.getName() + " (" + anm.getId() + ") - " + anm.getSpecie(), font));
                    document.add(tableNormal);
                    document.add(new Paragraph("Animal Report Total: " + formato.format(animalTotal) + " €", fontTable));
                    document.add(Chunk.createWhitespace(""));
                    masterTotal += animalTotal;
                }

                document.add(new Paragraph("Client Total: " + formato.format(masterTotal) + " €", font));
                DateTimeFormatter formatterTimeSign = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtChSign = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtChSign.format(formatterTimeSign), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void reportFutureCostsByClient(ArrayList<Appointment> apps, ArrayList<Animal> anms, Client clt){
        LocalDateTime dtCh = LocalDateTime.now(z).plusDays(1);

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                document.add(new Paragraph("CliPlus - Future Costs By Client", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Costs for " + clt.getName() + " (" + clt.getNif() + ")", font));
                document.add(new Paragraph("Contact: " + clt.getContact(), font));
                document.add(new Paragraph("Address Line One: " + clt.getAddress().getNstreet() + ", nº" + clt.getAddress().getndoor(), font));
                StringBuilder sb = new StringBuilder();

                String ccPostal = Integer.toString(clt.getAddress().getZipCode());
                char[] ccPostalArr = ccPostal.toCharArray();

                for (int i = 0; i < ccPostalArr.length; i++) {
                    if (i == 3) {
                        sb.append(ccPostalArr[i]).append("-");
                    } else {
                        sb.append(ccPostalArr[i]);
                    }
                }

                document.add(new Paragraph("Address Line Two: " + clt.getAddress().getNlocality() + " (" + sb.toString() + ")", font));
                document.add(Chunk.createWhitespace(""));

                double masterTotal = 0;
                for (Animal anm : anms) {
                    PdfPTable tableNormal = new PdfPTable(5);
                    double animalTotal = 0;
                    Stream.of("Time Slot", "Vet Name", "Appointment Type", "OnSite/Remote", "Total")
                            .forEach(columnTitle -> {
                                PdfPCell header = new PdfPCell();
                                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                                header.setBorderWidth(1);
                                header.setPhrase(new Paragraph(columnTitle, fontTable));
                                tableNormal.addCell(header);
                            });
                    for (int i = 0; i < apps.size(); i++) {
                        if (apps.get(i).getAnimal().getId() == anm.getId()) {
                            if (apps.get(i).getTimeSlot().getStartTime().isAfter(dtCh)) {
                                double totalToPay = 0;

                                if (apps.get(i).getAppoLocal() == Appointment.AppointmentLocation.Remote) {
                                    totalToPay = 40 + apps.get(i).getDistance();
                                }

                                if (apps.get(i).getAppoType() == Appointment.AppointmentType.Surgery) {
                                    if (anm.getWeight() >= 10) {
                                        totalToPay += 400;
                                    } else {
                                        totalToPay += 200;
                                    }
                                }

                                if (apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination) {
                                    if (anm.getWeight() >= 10) {
                                        totalToPay += 50;
                                    } else {
                                        totalToPay += 100;
                                    }
                                }

                                if (apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation) {
                                    if (anm.getWeight() >= 10) {
                                        totalToPay += 25;
                                    } else {
                                        totalToPay += 50;
                                    }
                                }

                                totalToPay *= 1.23;
                                animalTotal += totalToPay;

                                if (apps.get(i).getAppoType() == Appointment.AppointmentType.Surgery) {
                                    LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                                    tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTime) + " - " + sEnd.format(formatterTime), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getAppoType().toString(), fontTable));
                                    tableNormal.addCell(new Paragraph(formato.format(totalToPay) + " €", fontTable));
                                    i += 3;
                                } else {
                                    tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                                    tableNormal.addCell(new Paragraph(apps.get(i).getAppoType().toString(), fontTable));
                                    tableNormal.addCell(new Paragraph(formato.format(totalToPay) + " €", fontTable));
                                }
                            }
                        }
                    }
                    tableNormal.setWidthPercentage(100);
                    tableNormal.setSpacingBefore(10);

                    document.add(new Paragraph(anm.getName() + " (" + anm.getId() + ") - " + anm.getSpecie(), font));
                    document.add(tableNormal);
                    document.add(new Paragraph("Animal Report Total: " + formato.format(animalTotal) + " €", fontTable));
                    document.add(Chunk.createWhitespace(""));
                    masterTotal += animalTotal;
                }

                document.add(new Paragraph("Client Total: " + formato.format(masterTotal) + " €", font));
                DateTimeFormatter formatterTimeSign = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtChSign = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtChSign.format(formatterTimeSign), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void GenerateInvoice(Animal anm, ArrayList<Appointment> apps, Client clt){
        LocalDateTime lDateStart = Interactive.readDate("Generate Invoice - Start Date", true, false);
        LocalDateTime lDateEnd = Interactive.readDate("Generate Invoice - End Date", true, false);
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dtCh = LocalDateTime.now(z);

        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String nInvoice = Files.readData("invoices.txt").trim();
            int codeToShow = Integer.parseInt(nInvoice);
            String fileName = "Invoice FT" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + codeToShow + ".pdf";
            String internalName = "Invoice FT" +  Calendar.getInstance().get(Calendar.YEAR) + "/" + codeToShow;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                DateTimeFormatter formatterTimeIssued = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtChIssued = LocalDateTime.now(z);

                document.add(new Paragraph("CliPlus - Invoice " + Calendar.getInstance().get(Calendar.YEAR) + "/" + codeToShow, font));
                document.add(new Paragraph("AT-CUD: CBR600MT07-" + codeToShow, font));
                document.add(new Paragraph("Issued At: " + dtChIssued.format(formatterTimeIssued), font));

                Files.saveData("invoices.txt", String.valueOf(codeToShow + 1));

                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Exmo(a), Sr(a) " + clt.getName(), font));
                document.add(new Paragraph("VAT: " + clt.getNif(), font));
                document.add(new Paragraph("Contact: " + clt.getContact(), font));
                document.add(new Paragraph("Address Line One: " + clt.getAddress().getNstreet() + ", nº" + clt.getAddress().getndoor(), font));
                StringBuilder sb = new StringBuilder();

                String ccPostal = Integer.toString(clt.getAddress().getZipCode());
                char[] ccPostalArr = ccPostal.toCharArray();

                for (int i = 0; i < ccPostalArr.length; i++) {
                    if (i == 3) {
                        sb.append(ccPostalArr[i]).append("-");
                    } else {
                        sb.append(ccPostalArr[i]);
                    }
                }

                document.add(new Paragraph("Address Line Two: " + clt.getAddress().getNlocality() + " (" + sb.toString() + ")", font));
                document.add(Chunk.createWhitespace(""));

                PdfPTable tableNormal = new PdfPTable(7);

                Stream.of("Time Slot", "Animal Name", "Vet Name", "Appointment Type", "Distance", "OnSite/Remote", "Total")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Paragraph(columnTitle, fontTable));
                            tableNormal.addCell(header);
                        });

                double total = 0;
                for (int i = 0; i < apps.size(); i++) {
                    if ((apps.get(i).getTimeSlot().getStartTime().toLocalDate().isAfter(lDateStart.toLocalDate().minusDays(1)) && apps.get(i).getTimeSlot().getEndTime().toLocalDate().isBefore(lDateEnd.toLocalDate())) || apps.get(i).getTimeSlot().getStartTime().toLocalDate().isEqual(dtCh.toLocalDate())) {
                        if (apps.get(i).getAnimal().getId() == anm.getId()) {

                            double totalToPay = 0;

                            if (apps.get(i).getAppoLocal() == Appointment.AppointmentLocation.Remote) {
                                totalToPay = 40 + apps.get(i).getDistance();
                            }

                            if (apps.get(i).getAppoType() == Appointment.AppointmentType.Surgery) {
                                if (anm.getWeight() >= 10) {
                                    totalToPay += 400;
                                } else {
                                    totalToPay += 200;
                                }
                            }

                            if (apps.get(i).getAppoType() == Appointment.AppointmentType.Vaccination) {
                                if (anm.getWeight() >= 10) {
                                    totalToPay += 50;
                                } else {
                                    totalToPay += 100;
                                }
                            }

                            if (apps.get(i).getAppoType() == Appointment.AppointmentType.Consultation) {
                                if (anm.getWeight() >= 10) {
                                    totalToPay += 25;
                                } else {
                                    totalToPay += 50;
                                }
                            }

                            totalToPay *= 1.23;
                            total += totalToPay;
                            if (apps.get(i).getAppoType() == Appointment.AppointmentType.Surgery) {
                                LocalDateTime sEnd = apps.get(i).getTimeSlot().getStartTime().plusHours(2).minusMinutes(30);
                                tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().minusMinutes(30).format(formatterTime) + " - " + sEnd.format(formatterTime), fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getAppoType().toString(), fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getDistance() + " KMs", fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                                tableNormal.addCell(new Paragraph(formato.format(totalToPay) + " €", fontTable));
                                i += 3;
                            } else {
                                tableNormal.addCell(new Paragraph(apps.get(i).getTimeSlot().getStartTime().format(formatterTime) + " - " + apps.get(i).getTimeSlot().getEndTime().format(formatterTime), fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getAnimal().getName() + " (" + apps.get(i).getAnimal().getId() + ")", fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getVet().getName(), fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getAppoType().toString(), fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getDistance() + " KMs", fontTable));
                                tableNormal.addCell(new Paragraph(apps.get(i).getAppoLocal().toString(), fontTable));
                                tableNormal.addCell(new Paragraph(formato.format(totalToPay) + " €", fontTable));
                            }
                        }
                    }
                }

                tableNormal.setWidthPercentage(100);
                tableNormal.setSpacingBefore(5);

                document.add(tableNormal);
                document.add(new Paragraph("Invoice Total: " + formato.format(total) + " €", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. XihCBR - Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void ClientReportRecord(Client c, ArrayList<Animal> anms){
        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

                PdfPTable tableNormal = new PdfPTable(6);
                Stream.of("Chip ID", "Name", "Specie", "Gender", "Weight (KG)", "Is Alive")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(1);
                            header.setPhrase(new Paragraph(columnTitle, fontTable));
                            tableNormal.addCell(header);
                        });

                for(Animal anm : anms){
                    tableNormal.addCell(new Paragraph(String.valueOf(anm.getId()), fontTable));
                    tableNormal.addCell(new Paragraph(anm.getName(), fontTable));
                    tableNormal.addCell(new Paragraph(anm.getSpecie(), fontTable));
                    tableNormal.addCell(new Paragraph(anm.getGender(), fontTable));
                    tableNormal.addCell(new Paragraph(formato.format(anm.getWeight()), fontTable));
                    tableNormal.addCell(new Paragraph(String.valueOf(anm.getIsActive()), fontTable));
                }

                tableNormal.setWidthPercentage(100);
                tableNormal.setSpacingBefore(10);

                document.add(new Paragraph("CliPlus - Client Record Report", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Name: " + c.getName(), font));
                document.add(new Paragraph("VAT: " + c.getNif(), font));
                document.add(new Paragraph("Address Line One: " + c.getAddress().getNstreet() + ", nº" + c.getAddress().getndoor(), font));
                String ccPostal = Integer.toString(c.getAddress().getZipCode());
                char[] ccPostalArr = ccPostal.toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ccPostalArr.length; i++) {
                    if (i == 3) {
                        sb.append(ccPostalArr[i]).append("-");
                    } else {
                        sb.append(ccPostalArr[i]);
                    }
                }

                document.add(new Paragraph("Address Line Two: " + c.getAddress().getNlocality() + " (" + sb.toString() + ")", font));
                document.add(new Paragraph("Contact: " + c.getContact(), font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("List Of Animals", font));
                document.add(tableNormal);
                DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtCh = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtCh.format(formatterTime), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void AppointmentReportRecord(Appointment app, Client c){
        try{
            Date now = new Date();
            Long longTime = now.getTime() / 1000;
            Document document = new Document();
            String fileName = "report-" + longTime + ".pdf";
            String internalName = "report-" + longTime;
            JFileChooser fc = new JFileChooser();

            fc.setDialogTitle("Save Report");
            fc.setCurrentDirectory(new File("."));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setSelectedFile(new File(fileName));
            int returnVal = fc.showSaveDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                File pdfFile = fc.getSelectedFile();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                Font fontTable = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);
                DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                document.add(new Paragraph("CliPlus - Appointment Record Report", font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Appointment", font));
                if(app.getAppoType() == Appointment.AppointmentType.Surgery){
                    document.add(new Paragraph("Time Slot: " + app.getTimeSlot().getStartTime().format(formatterTime) + " - " + app.getTimeSlot().getEndTime().plusMinutes(90).format(formatterTime), font));
                }else{
                    document.add(new Paragraph("Time Slot: " + app.getTimeSlot().getStartTime().format(formatterTime) + " - " + app.getTimeSlot().getEndTime().format(formatterTime), font));
                }
                document.add(new Paragraph("Appointment Type: " + app.getAppoType(), font));
                document.add(new Paragraph("Appointment Local: " + app.getAppoLocal(), font));
                document.add(new Paragraph("Distance: " + formato.format(app.getDistance()), font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Animal Data", font));
                document.add(new Paragraph("Animal Name (Chip ID): " + app.getAnimal().getName() + " (" + app.getAnimal().getId() + ")", font));
                document.add(new Paragraph("Specie: " + app.getAnimal().getSpecie(), font));
                document.add(new Paragraph("Gender: " + app.getAnimal().getGender(), font));
                document.add(new Paragraph("Weight: " + formato.format(app.getAnimal().getWeight()), font));
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Client Data", font));
                document.add(new Paragraph("Name: " + c.getName(), font));
                document.add(new Paragraph("VAT: " + c.getNif(), font));
                String ccPostal = Integer.toString(c.getAddress().getZipCode());
                char[] ccPostalArr = ccPostal.toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ccPostalArr.length; i++) {
                    if (i == 3) {
                        sb.append(ccPostalArr[i]).append("-");
                    } else {
                        sb.append(ccPostalArr[i]);
                    }
                }

                document.add(new Paragraph("Address Line One: " + c.getAddress().getNstreet() + ", nº" + c.getAddress().getndoor(), font));
                document.add(new Paragraph("Address Line Two: " + c.getAddress().getNlocality() + " (" + sb.toString() + ")", font));
                document.add(new Paragraph("Contact: " + c.getContact(), font));

                document.add(Chunk.createWhitespace(""));
                DateTimeFormatter formatterTimeReport = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime dtCh = LocalDateTime.now(z);
                document.add(Chunk.createWhitespace(""));
                document.add(new Paragraph("Report created at " + dtCh.format(formatterTimeReport), fontTable));
                document.add(new Paragraph(Calendar.getInstance().get(Calendar.YEAR) + " © CliPlus. Processado por programa certificado.", fontTable));
                document.addTitle("CliPLus - " + internalName);
                document.close();

                Desktop.getDesktop().open(pdfFile);
            }
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "PDF Generation", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong!", "Opening PDF", JOptionPane.ERROR_MESSAGE);
        }
    }
}