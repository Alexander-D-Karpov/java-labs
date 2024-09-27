package ru.akarpov.server.manager;

import ru.akarpov.models.*;
import ru.akarpov.server.util.Logger;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseManager {
    private static String dbUrl;

    public static Connection getConnection() throws SQLException {
        if (dbUrl == null) {
            throw new SQLException("URL базы данных не установлен. Пожалуйста, проверьте настройки.");
        }
        return DriverManager.getConnection(dbUrl);
    }

    public static void initializeDatabase(String url) throws SQLException {
        dbUrl = url;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(32) NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS events (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "tickets_count BIGINT NOT NULL, " +
                    "description TEXT, " +
                    "event_type VARCHAR(20))");
            stmt.execute("CREATE TABLE IF NOT EXISTS tickets (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "coordinates_x INT NOT NULL, " +
                    "coordinates_y INT NOT NULL, " +
                    "creation_date TIMESTAMP NOT NULL, " +
                    "price BIGINT, " +
                    "discount BIGINT, " +
                    "ticket_type VARCHAR(20), " +
                    "event_id INT REFERENCES events(id), " +
                    "user_id INT REFERENCES users(id))");
            Logger.info("База данных инициализирована успешно");
        }
    }

    public static User registerUser(String username, String password) {
        if (isUsernameTaken(username)) {
            Logger.warn("Попытка регистрации с уже существующим именем пользователя: " + username);
            return null;
        }

        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password) VALUES (?, ?) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new User(id, username, hashedPassword);
                }
            }
        } catch (SQLException e) {
            Logger.error("Ошибка при регистрации пользователя", e);
        }
        return null;
    }


    private static boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            Logger.error("Ошибка при проверке существования имени пользователя", e);
        }
        return false;
    }

    public static User authenticateUser(String username, String password) {
        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            Logger.error("Ошибка при аутентификации пользователя", e);
        }
        return null;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Ошибка при хэшировании пароля", e);
            return null;
        }
    }


    public static boolean addTicket(Ticket ticket) {
        int eventId = addOrGetEvent(ticket.getEvent());
        if (eventId == -1) {
            return false;
        }
        ticket.getEvent().setId(eventId);

        String sql = "INSERT INTO tickets (name, coordinates_x, coordinates_y, creation_date, price, discount, ticket_type, event_id, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            processTicket(ticket, eventId, pstmt);
            pstmt.setInt(9, ticket.getUserId());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ticket.setId(rs.getLong("id"));
                    return true;
                }
            }
        } catch (SQLException e) {
            Logger.error("Ошибка при добавлении билета в базу данных", e);
        }
        return false;
    }

    private static void processTicket(Ticket ticket, int eventId, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, ticket.getName());
        pstmt.setInt(2, ticket.getCoordinates().getX());
        pstmt.setInt(3, ticket.getCoordinates().getY());
        pstmt.setTimestamp(4, new Timestamp(ticket.getCreationDate().getTime()));
        pstmt.setLong(5, ticket.getPrice());
        pstmt.setLong(6, ticket.getDiscount());
        pstmt.setString(7, ticket.getType().toString());
        pstmt.setInt(8, eventId);
    }


    private static int addOrGetEvent(Event event) {
        String checkSql = "SELECT id FROM events WHERE name = ? AND tickets_count = ? AND description = ? AND event_type = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, event.getName());
            checkStmt.setLong(2, event.getTicketsCount());
            checkStmt.setString(3, event.getDescription());
            checkStmt.setString(4, event.getEventType().toString());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }

            // Если событие не найдено, вставляем новое
            String insertSql = "INSERT INTO events (name, tickets_count, description, event_type) VALUES (?, ?, ?, ?) RETURNING id";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, event.getName());
                insertStmt.setLong(2, event.getTicketsCount());
                insertStmt.setString(3, event.getDescription());
                insertStmt.setString(4, event.getEventType().toString());
                try (ResultSet rsInsert = insertStmt.executeQuery()) {
                    if (rsInsert.next()) {
                        return rsInsert.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            Logger.error("Ошибка при добавлении или получении события", e);
        }
        return -1;
    }


    public static boolean updateTicket(Ticket ticket) {
        int eventId = addOrGetEvent(ticket.getEvent());
        if (eventId == -1) {
            return false;
        }
        ticket.getEvent().setId(eventId);

        String sql = "UPDATE tickets SET name = ?, coordinates_x = ?, coordinates_y = ?, creation_date = ?, " +
                "price = ?, discount = ?, ticket_type = ?, event_id = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            processTicket(ticket, eventId, pstmt);
            pstmt.setLong(9, ticket.getId());
            pstmt.setInt(10, ticket.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.error("Ошибка при обновлении билета в базе данных", e);
        }
        return false;
    }

    public static boolean removeTicket(long id, int userId) {
        String sql = "DELETE FROM tickets WHERE id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.error("Ошибка при удалении билета из базы данных", e);
        }
        return false;
    }

    private static final String SELECT_TICKET_FIELDS =
            "t.id AS t_id, " +
                    "t.name AS t_name, " +
                    "t.coordinates_x AS t_coordinates_x, " +
                    "t.coordinates_y AS t_coordinates_y, " +
                    "t.creation_date AS t_creation_date, " +
                    "t.price AS t_price, " +
                    "t.discount AS t_discount, " +
                    "t.ticket_type AS t_ticket_type, " +
                    "t.user_id AS t_user_id, " +
                    "e.id AS e_id, " +
                    "e.name AS e_name, " +
                    "e.tickets_count AS e_tickets_count, " +
                    "e.description AS e_description, " +
                    "e.event_type AS e_event_type";

    private static final String JOIN_TICKETS_EVENTS =
            "FROM tickets t " +
                    "JOIN events e ON t.event_id = e.id";


    public static List<Ticket> getAllTickets() {
        Logger.info("Получение всех билетов из базы данных");
        List<Ticket> tickets = new ArrayList<>();

        String sql = "SELECT " + SELECT_TICKET_FIELDS + " " + JOIN_TICKETS_EVENTS;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tickets.add(extractTicketFromResultSet(rs));
            }
            Logger.info("Получено " + tickets.size() + " билетов из базы данных");

        } catch (SQLException e) {
            Logger.error("Ошибка при получении билетов из базы данных", e);
        }

        return tickets;
    }

    public static List<Ticket> getUserTickets(int userId) {
        Logger.info("Получение билетов пользователя с ID " + userId);
        List<Ticket> tickets = new ArrayList<>();

        String sql = "SELECT " + SELECT_TICKET_FIELDS + " " + JOIN_TICKETS_EVENTS + " WHERE t.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(extractTicketFromResultSet(rs));
                }
            }

            Logger.info("Получено " + tickets.size() + " билетов пользователя с ID " + userId);

        } catch (SQLException e) {
            Logger.error("Ошибка при получении билетов пользователя из базы данных", e);
        }

        return tickets;
    }

    public static boolean clearUserTickets(int userId) {
        String sql = "DELETE FROM tickets WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.error("Ошибка при очистке билетов пользователя в базе данных", e);
        }
        return false;
    }

    private static Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getLong("t_id"));
        ticket.setName(rs.getString("t_name"));
        ticket.setCoordinates(new Coordinates(
                rs.getInt("t_coordinates_x"),
                rs.getInt("t_coordinates_y")
        ));
        ticket.setCreationDate(rs.getTimestamp("t_creation_date"));
        ticket.setPrice(rs.getLong("t_price"));
        ticket.setDiscount(rs.getLong("t_discount"));
        ticket.setType(TicketType.valueOf(rs.getString("t_ticket_type")));
        ticket.setUserId(rs.getInt("t_user_id"));

        // Создаем и заполняем объект Event
        Event event = new Event();
        event.setId(rs.getInt("e_id"));
        event.setName(rs.getString("e_name"));
        event.setTicketsCount(rs.getLong("e_tickets_count"));
        event.setDescription(rs.getString("e_description"));
        event.setEventType(EventType.valueOf(rs.getString("e_event_type")));

        // Устанавливаем событие в билет
        ticket.setEvent(event);

        return ticket;
    }

}