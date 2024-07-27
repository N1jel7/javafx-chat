package chat.javafx.server;

import chat.javafx.server.dao.SimpleConnectionProvider;
import chat.javafx.server.dao.UserDao;
import chat.javafx.server.dao.UserDaoImpl;
import chat.javafx.server.dao.UserDto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class main {
    public static void main(String[] args) {
        UserDao userDaoImpl = new UserDaoImpl(new SimpleConnectionProvider());
/*        LocalDate date = LocalDate.of(2005, 3, 10);
        UserDto userDto = new UserDto("N1jel", true, "Vanya", "Prokopovich", date);
        userDaoImpl.save(userDto);*/

        System.out.println(userDaoImpl.findUserByLogin("N1jel"));

    }

}
