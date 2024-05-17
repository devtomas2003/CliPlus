package Menus;

import Classes.*;
import pt.gov.cartaodecidadao.PTEID_EId;
import pt.gov.cartaodecidadao.PTEID_ExNoCardPresent;
import pt.gov.cartaodecidadao.PTEID_ExNoReader;
import pt.gov.cartaodecidadao.PTEID_Exception;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Appointments {
    public static void showMenu(ArrayList<People> persons, ArrayList<Appointment> apps){
        int opcao;

        do {
            opcao = Interactive.readInt("Appointment Operations\n\n1 - Create Appointment\n2 - List Types of Interventions\n0 - Previus Menu", "Appointments");

            switch (opcao) {
                case 1:
                    int findType = Interactive.readInt("How to find the animal?\n\n1 - By Client\n2 - By Chip ID", "Find Animal");
                    if(findType == 1){
                        int ccRead = JOptionPane.showConfirmDialog(null, "Do you want to read the client from the citizen card?", "Create Client", JOptionPane.YES_NO_OPTION);
                        if(ccRead == 0) {
                            try {
                                PTEID_EId eid = CitizenCard.initiate();
                                int nif = Integer.parseInt(eid.getTaxNo());
                                Client pp = Clients.findClient(nif, persons);
                                if(pp == null){
                                    JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                                int chipId = selectAnimalFromClient(pp);
                                createAppointment(chipId, apps, persons);
                            }catch (PTEID_ExNoReader ex){
                                JOptionPane.showMessageDialog(null, "No Reader Found!", "Find Client", JOptionPane.ERROR_MESSAGE);
                            }catch (PTEID_ExNoCardPresent ex) {
                                JOptionPane.showMessageDialog(null, "No Card Found!", "Find Client", JOptionPane.ERROR_MESSAGE);
                            } catch (PTEID_Exception e) {
                                JOptionPane.showMessageDialog(null, "GOV PT SDK Problem!", "Find Client", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                CitizenCard.release();
                            }
                        }else{
                            int nif = Interactive.readInt("Enter the client NIF", "Find Client");
                            Client pp = Clients.findClient(nif, persons);
                            if(pp == null){
                                JOptionPane.showMessageDialog(null, "This client does not exists!", "Find Client", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            int chipId = selectAnimalFromClient(pp);
                            createAppointment(chipId, apps, persons);
                        }
                    }else{
                        int chipId = 0;
                        boolean isAValidChip = false;

                        do{
                            chipId = Interactive.readInt("Enter the chip ID", "Find Animal");

                            for(People pp : persons){
                                if(pp instanceof Client){
                                    ArrayList<Animal> anms = ((Client) pp).getAnimals();
                                    for(Animal anm : anms){
                                        if (anm.getId() == chipId && anm.getIsActive()) {
                                            isAValidChip = true;
                                        }
                                    }
                                }
                            }
                            if(!isAValidChip){
                                JOptionPane.showMessageDialog(null, "This animal does not exists!", "Find Animal", JOptionPane.ERROR_MESSAGE);
                            }
                        }while (!isAValidChip);

                        createAppointment(chipId, apps, persons);
                    }

                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "List all types of interventions\n\n- Normal\n- Vaccination\n- Surgery", "Interventions", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 0:
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "The option selected is invalid!", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }while (opcao != 0);
    }

    private static int selectAnimalFromClient(Client client) {
        HashMap<Integer, Integer> codigos = new HashMap<Integer, Integer>();
        String txtToShow = "Choose an animal to select:\n\n";
        int codigo = 1;
        for (Animal anm : client.getAnimals()) {
            if (anm.getIsActive()) {
                txtToShow += codigo + " - " + anm.getName() + "\n";
                codigos.put(codigo, anm.getId());
                codigo++;
            }
        }
        int selectedCode = 0;
        do {
            int selectedCodeOpt = Interactive.readInt(txtToShow, "Select Animal");
            if (!codigos.containsKey(selectedCodeOpt)){
                JOptionPane.showMessageDialog(null, "Invalid Code", "Select Animal", JOptionPane.ERROR_MESSAGE);
            }else{
                selectedCode = codigos.get(selectedCodeOpt);
            }
        } while (selectedCode == 0);
        return selectedCode;
    }

    private static void createAppointment(int chipId, ArrayList<Appointment> apps, ArrayList<People> persons){
        Animal anm = null;

        for(People person : persons){
            if(person instanceof Client){
                for(Animal anmItr : ((Client) person).getAnimals()){
                    if(anmItr.getId() == chipId){
                        anm = anmItr;
                    }
                }
            }
        }

        int opcao = -1;
        do{
            opcao = Interactive.readInt("What appointment do you want to schedule?\n\n1 - Normal\n2 - Vaccination\n3 - Surgery\n0 - Cancel", "Appointments");
            if(opcao != 1 && opcao != 2 && opcao != 3){
                JOptionPane.showMessageDialog(null, "Invalid option!", "Scheduler", JOptionPane.ERROR_MESSAGE);
                break;
            }

        }while (opcao == -1);

        int opcaoLocation = -1;
        do{
            opcaoLocation = Interactive.readInt("Where this appointmen should happen?\n\n1 - On Site\n2 - Remote\n0 - Cancel", "Appointments");
            if(opcaoLocation != 1 && opcao != 2){
                JOptionPane.showMessageDialog(null, "Invalid option!", "Scheduler", JOptionPane.ERROR_MESSAGE);
                break;
            }

        }while (opcaoLocation == -1);

        Vet vetSelected = null;
        do{
            int vetId = Interactive.readInt("ID of Ordem dos Medicos Veterinarios", "Find Vet");
            Vet vetFounded = Vets.findVetByOMV(vetId, persons);
            if(vetFounded == null){
                JOptionPane.showMessageDialog(null, "This OMV ID does not exists!", "Find Vet", JOptionPane.ERROR_MESSAGE);
            }else{
                vetSelected = vetFounded;
            }
        }while (vetSelected == null);

        int opcaoTimeSlot = -1;
        do{
            opcaoTimeSlot = Interactive.readInt("When do you want to schedule?\n\n1 - Check a day\n2 - Next Available\n0 - Cancel", "Appointments");
            if(opcaoTimeSlot != 1 && opcaoTimeSlot != 2){
                JOptionPane.showMessageDialog(null, "Invalid option!", "Scheduler", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }while (opcaoTimeSlot == -1);

        if(opcaoTimeSlot == 1){
            int day = Interactive.readInt("Select an day", "Scheduler");
            int month = Interactive.readInt("Select an month", "Scheduler");
            int year = Interactive.readInt("Select an year", "Scheduler");

            LocalDateTime dtCh = LocalDate.of(year, month, day).atStartOfDay();

            Appointment.AppointmentType aptType;

            if(opcao == 1){
                aptType = Appointment.AppointmentType.Consultation;
            }else if(opcao == 2){
                aptType = Appointment.AppointmentType.Vaccination;
            }else{
                aptType = Appointment.AppointmentType.Surgery;
            }

            Appointment.AppointmentLocation aptLocal;

            if(opcao == 1){
                aptLocal = Appointment.AppointmentLocation.OnSite;
            }else{
                aptLocal = Appointment.AppointmentLocation.Remote;
            }

            ArrayList<Slot> availableSlots = getSlotsAvailable(dtCh, apps, aptType, vetSelected);

            if(availableSlots.isEmpty()){
                JOptionPane.showMessageDialog(null, "There is no slots available for this type of schedule!", "Slots Manager", JOptionPane.ERROR_MESSAGE);
            }else{
                HashMap<Integer, Slot> codigos = new HashMap<Integer, Slot>();

                String txtToShow = "Please choose an slot:\n\n";
                int codigo = 1;
                for(Slot slt : availableSlots){
                    if(aptType == Appointment.AppointmentType.Surgery){
                        LocalDateTime endTime = slt.getStartTime();
                        endTime = endTime.plusHours(2);

                        txtToShow += codigo + " - " + slt.getStartTime().toLocalTime() + " unitl " + endTime.toLocalTime() + "\n";
                        codigos.put(codigo, slt);
                        codigo++;
                    }else{
                        txtToShow += codigo + " - " + slt.getStartTime().toLocalTime() + " unitl " + slt.getEndTime().toLocalTime() + "\n";
                        codigos.put(codigo, slt);
                        codigo++;
                    }
                }

                int codigoToSelect = Interactive.readInt(txtToShow, "Select Schedule");
                if(codigos.containsKey(codigoToSelect)){
                    Appointment appt = new Appointment(aptType, aptLocal, anm, codigos.get(codigoToSelect), vetSelected);
                    if(aptType == Appointment.AppointmentType.Surgery) {
                        Slot lastSlot = codigos.get(codigoToSelect);
                        LocalDateTime startSch = lastSlot.getStartTime().plusMinutes(30);
                        for(int i = 0; i < 3; i++) {
                            LocalDateTime endSch = startSch.plusMinutes(30);
                            Appointment apptNew = new Appointment(aptType, aptLocal, anm, new Slot(startSch, endSch), vetSelected);
                            apps.add(apptNew);
                            startSch = endSch;
                        }
                    }
                    apps.add(appt);
                    String fileData = "";
                    for(Appointment app : apps){
                        fileData += app.getAppoType() + "," + app.getAppoLocal() + "," + app.getVet().getNif() + "," + app.getAnimal().getId() + "," + app.getTimeSlot().getStartTime() + "," + app.getTimeSlot().getEndTime() + "\n";
                    }
                    Files.saveData("appointments.csv", fileData);
                    JOptionPane.showMessageDialog(null,  aptType + " scheduled with success!", "Scheduler", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Invalid Code", "Scheduler", JOptionPane.ERROR_MESSAGE);
                }
            }
        }else{
            ZoneId z = ZoneId.of("Europe/Lisbon");
            LocalDateTime dtCh = LocalDateTime.now(z);

            Appointment.AppointmentType aptType;

            if(opcao == 1){
                aptType = Appointment.AppointmentType.Consultation;
            }else if(opcao == 2){
                aptType = Appointment.AppointmentType.Vaccination;
            }else{
                aptType = Appointment.AppointmentType.Surgery;
            }

            Appointment.AppointmentLocation aptLocal;

            if(opcao == 1){
                aptLocal = Appointment.AppointmentLocation.OnSite;
            }else{
                aptLocal = Appointment.AppointmentLocation.Remote;
            }

            ArrayList<Slot> availableSlots;

            do{
                availableSlots = getSlotsAvailable(dtCh, apps, aptType, vetSelected);
                dtCh = dtCh.plusDays(1);
            }while (availableSlots.isEmpty());

                HashMap<Integer, Slot> codigos = new HashMap<Integer, Slot>();
                DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = dtCh.toLocalDate().format(formatterDay);
                String txtToShow = "Day: " + formattedDate + "\nPlease choose an slot:\n\n";
                int codigo = 1;
                for(Slot slt : availableSlots){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    if(aptType == Appointment.AppointmentType.Surgery){
                        LocalDateTime endTime = slt.getStartTime();
                        endTime = endTime.plusHours(2);
                        txtToShow += codigo + " - " +  slt.getStartTime().toLocalTime().format(formatter) + " unitl " + endTime.toLocalTime().format(formatter) + "\n";
                        codigos.put(codigo, slt);
                        codigo++;
                    }else{
                        txtToShow += codigo + " - " + slt.getStartTime().toLocalTime().format(formatter) + " unitl " + slt.getEndTime().toLocalTime().format(formatter) + "\n";
                        codigos.put(codigo, slt);
                        codigo++;
                    }
                }

                int codigoToSelect = Interactive.readInt(txtToShow, "Select Schedule");
                if(codigos.containsKey(codigoToSelect)){
                    Appointment appt = new Appointment(aptType, aptLocal, anm, codigos.get(codigoToSelect), vetSelected);
                    if(aptType == Appointment.AppointmentType.Surgery) {
                        Slot lastSlot = codigos.get(codigoToSelect);
                        LocalDateTime startSch = lastSlot.getStartTime().plusMinutes(30);
                        for(int i = 0; i < 3; i++) {
                            LocalDateTime endSch = startSch.plusMinutes(30);
                            Appointment apptNew = new Appointment(aptType, aptLocal, anm, new Slot(startSch, endSch), vetSelected);
                            apps.add(apptNew);
                            startSch = endSch;
                        }
                    }
                    apps.add(appt);
                    String fileData = "";
                    for(Appointment app : apps){
                        fileData += app.getAppoType() + "," + app.getAppoLocal() + "," + app.getVet().getNif() + "," + app.getAnimal().getId() + "," + app.getTimeSlot().getStartTime() + "," + app.getTimeSlot().getEndTime() + "\n";
                    }
                    Files.saveData("appointments.csv", fileData);
                    JOptionPane.showMessageDialog(null,  aptType + " scheduled with success!", "Scheduler", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Invalid Code", "Scheduler", JOptionPane.ERROR_MESSAGE);
                }

        }
    }

    private static ArrayList<Slot> getSlotsAvailable(LocalDateTime chDate, ArrayList<Appointment> apps, Appointment.AppointmentType aptType, Vet vet) {
        LocalDateTime selectedDate = chDate.toLocalDate().atStartOfDay();
        ArrayList<Appointment> appointmentsSelectedDay = new ArrayList<>();

        for (Appointment app : apps) {
            if (app.getTimeSlot().getStartTime().toLocalDate().equals(selectedDate.toLocalDate()) && app.getVet().getNif() == vet.getNif()) {
                appointmentsSelectedDay.add(app);
            }
        }

        ArrayList<Slot> checkSlots = generatePossibleSlots(chDate);
        ArrayList<Slot> possibleSlots = new ArrayList<>();

        if (appointmentsSelectedDay.isEmpty()) {
            possibleSlots.addAll(checkSlots);
        } else {
            boolean[] slotOccupied = new boolean[checkSlots.size()];
            for (Appointment appt : appointmentsSelectedDay) {
                for (int i = 0; i < checkSlots.size(); i++) {
                    Slot slot = checkSlots.get(i);
                    if (slot.getStartTime().isEqual(appt.getTimeSlot().getStartTime()) || slot.getEndTime().isEqual(appt.getTimeSlot().getEndTime()) || (slot.getStartTime().isBefore(appt.getTimeSlot().getEndTime()) && slot.getEndTime().isAfter(appt.getTimeSlot().getStartTime()))) {
                        slotOccupied[i] = true;
                    }
                }
            }

            for (int i = 0; i < checkSlots.size(); i++) {
                if (!slotOccupied[i]) {
                    possibleSlots.add(checkSlots.get(i));
                }
            }
        }

        if (aptType == Appointment.AppointmentType.Surgery) {
            return findConsecutiveSlots(possibleSlots, 4);
        }

        return possibleSlots;
    }

    private static ArrayList<Slot> findConsecutiveSlots(ArrayList<Slot> slots, int requiredConsecutive) {
        ArrayList<Slot> result = new ArrayList<>();
        for (int i = 0; i <= slots.size() - requiredConsecutive; i++) {
            boolean consecutive = true;
            for (int j = 0; j < requiredConsecutive - 1; j++) {
                if (!slots.get(i + j).isAdjacent(slots.get(i + j + 1))) {
                    consecutive = false;
                    break;
                }
            }
            if (consecutive) {
                result.add(slots.get(i));
            }
        }
        return result;
    }

    private static ArrayList<Slot> generatePossibleSlots(LocalDateTime day) {
        ArrayList<Slot> possibleSlots = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(day.toLocalDate(), day.toLocalTime().withHour(9).withMinute(0).withSecond(0));

        for (int itr = 0; itr < 18; itr++) {
            if (!(startTime.getHour() == 12 && (startTime.getMinute() == 0 || startTime.getMinute() == 30))) {
                Slot tmSlot = new Slot(startTime, startTime.plusMinutes(30));
                possibleSlots.add(tmSlot);
            }
            startTime = startTime.plusMinutes(30);
        }

        return possibleSlots;
    }

    private static String generateTimeString(ArrayList<Slot> slots) {
        StringBuilder txtToShow = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        int codigo = 1;
        for (Slot slt : slots) {
            txtToShow.append(codigo)
                    .append(" - ")
                    .append(slt.getStartTime().toLocalTime().format(formatter))
                    .append(" until ")
                    .append(slt.getEndTime().toLocalTime().format(formatter))
                    .append("\n");
            codigo++;
        }

        return txtToShow.toString();
    }
}
