package test_task;

import java.sql.*;

public class LimitsReader implements Runnable {
    @Override
    public void run() {
        while(true) {
            Connection connection = null;

            try {
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "testpassword");

                if (connection != null) {
                    System.out.println("You successfully connected to database now");
                } else {
                    System.out.println("Failed to make connection to database");
                }

                PreparedStatement pst = connection.prepareStatement("SELECT DISTINCT on (limit_name) limit_name,limit_value,effective_date FROM limits_per_hour GROUP BY limit_name, effective_date, limit_value");
                ResultSet rs = pst.executeQuery();
                rs.next();
                if ((rs.getTimestamp(3).compareTo(App.getMaxTimestamp())) >= 0) {
                    App.setMaxlimit(rs.getInt(2));
                    App.setMaxTimestamp(rs.getTimestamp(3));
                    System.out.println("Max limit values set to " + App.getMaxlimit());
                }

                rs.next();
                if ((rs.getTimestamp(3).compareTo(App.getMinTimestamp())) >= 0) {
                    App.setMinLimit(rs.getInt(2));
                    App.setMinTimestamp(rs.getTimestamp(3));
                    System.out.println("Min limit values set to " + App.getMinLimit());
                }
            } catch (SQLException e) {
                System.out.println("Connection Failed");
                e.printStackTrace();
                return;
            }
            try {
                Thread.sleep(1200000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
