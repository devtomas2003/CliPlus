package Classes;

import java.util.ArrayList;
import java.util.Date;

public class Appointment {

    enum AppointmentType {
        Vaccination,
        Surgery,
        Consultation
    }

    enum AppointmentLocation {
        OnSite,
        Remote
    }

    private Date startTime;
    private Date endTime;
    private AppointmentType appoType;
    private AppointmentLocation appoLocal;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public Appointment(Date startTime, Date endTime, AppointmentType appoType, AppointmentLocation appoLocal) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.appoType = appoType;
        this.appoLocal = appoLocal;
    }

    @Override
    public String toString() {
        return
            "Appointment Details:\n" +
                "Start Time: " + startTime + "\n" +
                "End Time: " + endTime + "\n" +
                "Appointment Type: " + appoType + "\n" +
                "Appointment Local: " + appoLocal;
    }
}
