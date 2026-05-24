import java.sql.*;
import java.util.*;

class BookingThread extends Thread {
    private int seatNumber;
    private String userName;

    public BookingThread(int seatNumber, String userName) {
        this.seatNumber = seatNumber;
        this.userName = userName;
    }

    public void run() {
        MovieTicketBooking.bookSeat(seatNumber, userName);
    }
}

public class MovieTicketBooking {

    static ArrayList<Integer> bookedSeats = new ArrayList<>();

    static final String URL = "jdbc:mysql://localhost:3306/movie_booking";
    static final String USER = "root";
    static final String PASSWORD = "Root@123";

    public static synchronized void bookSeat(int seatNumber, String userName) {

        try {

            if (bookedSeats.contains(seatNumber)) {
                System.out.println("Seat " + seatNumber + " is already booked!");
                return;
            }

            System.out.println("\nProcessing payment for " + userName + "...");
            Thread.sleep(2000);

            bookedSeats.add(seatNumber);

            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);

            String query = "INSERT INTO bookings(user_name, seat_number, payment_status) VALUES (?, ?, ?)";

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, userName);
            pst.setInt(2, seatNumber);
            pst.setString(3, "PAID");

            pst.executeUpdate();

            System.out.println("\nTicket Booked Successfully!");
            System.out.println("User Name: " + userName);
            System.out.println("Seat Number: " + seatNumber);
            System.out.println("Payment Status: PAID");

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void displaySeats() {

        System.out.println("\nAvailable Seats:");

        for (int i = 1; i <= 10; i++) {

            if (bookedSeats.contains(i)) {
                System.out.print("[X] ");
            } else {
                System.out.print("[" + i + "] ");
            }
        }

        System.out.println();
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            displaySeats();

            System.out.println("\nEnter User Name:");
            String name = sc.nextLine();

            System.out.println("Enter Seat Number:");
            int seat = sc.nextInt();
            sc.nextLine();

            BookingThread bt = new BookingThread(seat, name);
            bt.start();

            try {
                bt.join();
            } catch (Exception e) {
                System.out.println(e);
            }

            System.out.println("\nDo you want to continue? (yes/no)");
            String choice = sc.nextLine();

            if (choice.equalsIgnoreCase("no")) {
                break;
            }
        }

        System.out.println("\nFinal Booking Details:");

        try {

            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM bookings");

            while (rs.next()) {

                System.out.println(
                        "ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("user_name") +
                        " | Seat: " + rs.getInt("seat_number") +
                        " | Payment: " + rs.getString("payment_status"));
            }

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        sc.close();
    }
}