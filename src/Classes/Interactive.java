package Classes;


import javax.swing.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class Interactive {
    private static final ZoneId z = ZoneId.of("Europe/Lisbon");
    public static int readInt(String question, String title, int defaultValue){
        boolean success = false;
        int valor = 0;
        do{
            try{
                String input;
                if(defaultValue == 0){
                    input = JOptionPane.showInputDialog(null, question, title, JOptionPane.INFORMATION_MESSAGE);
                }else{
                    input = (String) JOptionPane.showInputDialog(null, question, title, JOptionPane.INFORMATION_MESSAGE, null, null, defaultValue);
                }
                if (input == null) {
                    break;
                }
                valor = Integer.parseInt(input);
                success = true;
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please input a number", "Wrong Type", JOptionPane.ERROR_MESSAGE);
            }
        }while (!success);
        return valor;
    }
    public static String readString(String question, String title, String defaultValue){
        String valor = "";
        do{
            valor = (String) JOptionPane.showInputDialog(null, question, title, JOptionPane.INFORMATION_MESSAGE, null, null, defaultValue);
            if (Objects.equals(valor, "")) {
                JOptionPane.showMessageDialog(null, "The text cannot be empty!", "Wrong Type", JOptionPane.ERROR_MESSAGE);
            }
        }while (Objects.equals(valor, ""));
        return valor;
    }
    public static double readDouble(String question, String title){
        boolean success = false;
        double valor = 0.00;
        do{
            try{
                String lido = JOptionPane.showInputDialog(null, question, title, JOptionPane.INFORMATION_MESSAGE);
                if (lido == null) {
                    break;
                }
                valor = Double.parseDouble(lido);
                success = true;
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please input a valid weight!", "Wrong Type", JOptionPane.ERROR_MESSAGE);
            }
        }while (!success);
        return valor;
    }
    public static LocalDateTime readDate(String title, boolean allowPast, boolean allowFuture){
        boolean isSuccess = false;
        LocalDateTime dtCh = null;

        LocalDateTime now = LocalDateTime.now(z);

        do{
            int day = Interactive.readInt("Insert an day", title, 0);
            int month = Interactive.readInt("Insert an month", title, 0);
            int year = Interactive.readInt("Insert an year", title, 0);

            try{
                dtCh = LocalDate.of(year, month, day).atStartOfDay();
                if(!allowPast){
                    if(dtCh.isBefore(now) && !dtCh.toLocalDate().equals(now.toLocalDate())){
                        JOptionPane.showMessageDialog(null, "The date cannot be in the past!", "Past Validation", JOptionPane.ERROR_MESSAGE);
                    }else {
                        if(allowFuture){
                            isSuccess = true;
                        }else{
                            if(dtCh.isAfter(now)){
                                JOptionPane.showMessageDialog(null, "The date cannot be in the future!", "Future Validation", JOptionPane.ERROR_MESSAGE);
                            }else{
                                isSuccess = true;
                            }
                        }
                    }
                }else{
                    if(allowFuture){
                        isSuccess = true;
                    }else{
                        if(dtCh.isAfter(now)){
                            JOptionPane.showMessageDialog(null, "The date cannot be in the future!", "Future Validation", JOptionPane.ERROR_MESSAGE);
                        }else{
                            isSuccess = true;
                        }
                    }
                }
            }catch (DateTimeException ex){
                JOptionPane.showMessageDialog(null, "Invalid Date", "Wrong Date", JOptionPane.ERROR_MESSAGE);
            }
        }while (!isSuccess);
        
        return dtCh;
    }
}
