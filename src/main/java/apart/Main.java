package apart;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/mydb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "";

    static Connection conn;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            try {
                // create connection
                conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();
                while (true) {
                    System.out.println("1: add apartment");
                    System.out.println("2: delete apartment");
                    System.out.println("3: change apartment");
                    System.out.println("4: view apartments");
                    System.out.println("5: fill apartments with random data");
                    System.out.println("6 filter apartments by district");
                    System.out.println("7: filter apartments by price range");
                    System.out.println("8: filter apartments by number of rooms");
                    System.out.println("9: filter apartments by area range");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addApartment(sc);
                            break;
                        case "2":
                            deleteApartment(sc);
                            break;
                        case "3":
                            changeApartment(sc);
                            break;
                        case "4":
                            viewApartments();
                            break;
                        case "5":
                            fillApartments(sc);
                            break;
                        case "6":
                            filterByDistrict(sc);
                            break;
                        case "7":
                            filterByPriceRange(sc);
                            break;
                        case "8":
                            filterByRooms(sc);
                            break;
                        case "9":
                            filterByAreaRange(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                if (conn != null) conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void initDB() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Apartments");
            st.execute("CREATE TABLE Apartments (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "district VARCHAR(100) NOT NULL, " +
                    "address VARCHAR(255) NOT NULL, " +
                    "area FLOAT NOT NULL, " +
                    "rooms INT NOT NULL, " +
                    "price DECIMAL(15, 2) NOT NULL)");
        } finally {
            st.close();
        }
    }

    private static void addApartment(Scanner sc) throws SQLException {
        System.out.print("Enter district: ");
        String district = sc.nextLine();
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter area: ");
        double area = Double.parseDouble(sc.nextLine());
        System.out.print("Enter rooms: ");
        int rooms = Integer.parseInt(sc.nextLine());
        System.out.print("Enter price: ");
        double price = Double.parseDouble(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (district, address, area, rooms, price) VALUES(?, ?, ?, ?, ?)");

        try {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setDouble(3, area);
            ps.setInt(4, rooms);
            ps.setDouble(5, price);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void deleteApartment(Scanner sc) throws SQLException {
        System.out.print("Enter apartment address: ");
        String address = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("DELETE FROM Apartments WHERE address = ?");
        try {
            ps.setString(1, address);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void changeApartment(Scanner sc) throws SQLException {
        System.out.print("Enter apartment address: ");
        String address = sc.nextLine();
        System.out.print("Enter new price: ");
        double price = Double.parseDouble(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("UPDATE Apartments SET price = ? WHERE address = ?");
        try {
            ps.setDouble(1, price);
            ps.setString(2, address);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private static void viewApartments() throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments");
        try {
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);
        } finally {
            ps.close();
        }
    }

    private static void fillApartments(Scanner sc) throws SQLException {
        System.out.println("Enter the number of apartments: ");
        int numberOfApartments = Integer.parseInt(sc.nextLine());
        String[] districts = {"Central", "North", "South", "East", "West"};
        String[] streets = {"Main St", "Oak St", "Pine St", "Maple St", "Cedar St"};
        Random rnd = new Random();

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (district, address, area, rooms, price) VALUES(?, ?, ?, ?, ?)");
        try {
            for (int i = 0; i < numberOfApartments; i++) {
                String district = districts[rnd.nextInt(districts.length)];
                String address = (rnd.nextInt(900) + 100) + " " + streets[rnd.nextInt(streets.length)];
                double area = 50 + rnd.nextDouble() * 150;
                int rooms = rnd.nextInt(6) + 1;
                double price = 50000 + rnd.nextDouble() * 450000;

                ps.setString(1, district);
                ps.setString(2, address);
                ps.setDouble(3, area);
                ps.setInt(4, rooms);
                ps.setDouble(5, price);
                ps.executeUpdate();
            }
        } finally {
            ps.close();
        }
    }
    private static void filterByDistrict(Scanner sc) throws SQLException {
        System.out.print("Enter district: ");
        String district = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE district = ?");
        try {
            ps.setString(1, district);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);
        } finally {
            ps.close();
        }
    }

    private static void filterByPriceRange(Scanner sc) throws SQLException {
        System.out.print("Enter minimum price: ");
        double minPrice = Double.parseDouble(sc.nextLine());
        System.out.print("Enter maximum price: ");
        double maxPrice = Double.parseDouble(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE price BETWEEN ? AND ?");
        try {
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);
        } finally {
            ps.close();
        }
    }

    private static void filterByRooms(Scanner sc) throws SQLException {
        System.out.print("Enter number of rooms: ");
        int rooms = Integer.parseInt(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE rooms = ?");
        try {
            ps.setInt(1, rooms);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);
        } finally {
            ps.close();
        }
    }
    private static void printResultSet(ResultSet rs) throws SQLException {
        try {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-20s", md.getColumnName(i));
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-20s", rs.getString(i));
                }
                System.out.println();
            }
        } finally {
            rs.close();
        }
    }

    private static void filterByAreaRange(Scanner sc) throws SQLException {
        System.out.print("Enter minimum area: ");
        double minArea = Double.parseDouble(sc.nextLine());
        System.out.print("Enter maximum area: ");
        double maxArea = Double.parseDouble(sc.nextLine());

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments WHERE area BETWEEN ? AND ?");
        try {
            ps.setDouble(1, minArea);
            ps.setDouble(2, maxArea);
            ResultSet rs = ps.executeQuery();
            printResultSet(rs);
        } finally {
            ps.close();
        }
    }
}
