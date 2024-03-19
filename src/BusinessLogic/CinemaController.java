package BusinessLogic;

import java.sql.*;
import java.util.ArrayList;

public class CinemaController {

    // Connessione al database
    private Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost/cinema_db";
        String user = "username";
        String password = "password";
        return DriverManager.getConnection(url, user, password);
    }

    public List<Movie> searchMovies(String query) {
        // Metodo per cercare film nel database
        String SQL_SELECT = "SELECT * FROM movies WHERE title LIKE ?";
        movies = new ArrayList<Movie>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("id")
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("duration"),
                        rs.getFloat("rating"),
                ));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return movies;
    }

    public List<Showtime> getShowtimesForMovie(int movieId, Date date) {
        // Metodo per ottenere gli orari disponibili per un determinato film
        String SQL_SELECT = "SELECT * FROM showtimes WHERE movie_id = ? AND show_date = ?";
        showtimes = new ArrayList<Showtime>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setInt(1, movieId);
            pstmt.setDate(2, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                showtimes.add(new Showtime(
                        rs.getInt("id"),
                        rs.getDate("show_date"),
                        rs.getTime("start_time"),
                        rs.getInt("hall_id")
                ));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return showtimes;
    }

    public boolean bookSeat(int showtimeId, int seatId, int userId) {
        // Metodo per prenotare un posto a sedere per un dato orario di spettacolo
        String SQL_UPDATE = "UPDATE seats SET booked = TRUE, user_id = ? WHERE id = ? AND showtime_id = ?";
        boolean bookingSuccess = false;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, seatId);
            pstmt.setInt(3, showtimeId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                bookingSuccess = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return bookingSuccess;
    }
}
