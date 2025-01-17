package Classes;

import java.io.Serializable;

public class Appointment implements Serializable {

    public enum AppointmentType {
        Vaccination,
        Surgery,
        Consultation
    }

    public enum AppointmentLocation {
        OnSite,
        Remote
    }

    private Slot timeSlot;
    private AppointmentType appoType;
    private AppointmentLocation appoLocal;
    private Animal animal;
    private Vet vet;
    private double distance;

    public Slot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Slot slot) {
        this.timeSlot = slot;
    }

    public AppointmentType getAppoType() {
        return appoType;
    }

    public void setAppoType(AppointmentType appoType) {
        this.appoType = appoType;
    }

    public AppointmentLocation getAppoLocal() {
        return appoLocal;
    }

    public void setAppoLocal(AppointmentLocation appoLocal) {
        this.appoLocal = appoLocal;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Vet getVet() {
        return vet;
    }

    public void setVet(Vet vet) {
        this.vet = vet;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Appointment(AppointmentType appoType, AppointmentLocation appoLocal, Animal anm, Slot timeSlot, Vet vet) {
        this.timeSlot = timeSlot;
        this.appoType = appoType;
        this.appoLocal = appoLocal;
        this.animal = anm;
        this.vet = vet;
    }

    @Override
    public String toString() {
        return
            "Appointment Details:\n" +
                "Start Time: " + timeSlot.getStartTime().toString() + "\n" +
                "End Time: " + timeSlot.getEndTime().toString() + "\n" +
                "Vet Name: " + vet.getName() + "\n" +
                "Appointment Type: " + appoType + "\n" +
                "Appointment Local: " + appoLocal;
    }
}
