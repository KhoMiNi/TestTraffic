package test_task;

import java.sql.*;

public class LimitsReader implements Runnable {

    private Connection connection = null;
    private String url = "jdbc:postgresql://localhost:5432/postgres";
    private String user = "postgres";
    private String password = "testpassword";
    private String query ="SELECT DISTINCT on (limit_name) limit_name,limit_value,effective_date FROM limits_per_hour GROUP BY limit_name, effective_date, limit_value";
    private long delay = 1200000;

    @Override
    public void run() {
        while(true) {
            connectToDB(url, user, password);
            readDataFromDB(query);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateLimits(String name, int limitValue, Timestamp timestamp) {

        switch(name) {
            case("max"):
                if((timestamp.compareTo(App.getMaxTimestamp())) >= 0)updateMaxLimit(limitValue, timestamp);
                break;
            case("min"):
                if((timestamp.compareTo(App.getMinTimestamp())) >= 0)updateMinLimit(limitValue, timestamp);
                break;
            default:
                System.out.println("Unexpected limit name");
                break;
        }
    }

    private void updateMaxLimit(int limitValue, Timestamp timestamp) {
        App.setMaxlimit(limitValue);
        App.setMaxTimestamp(timestamp);
        System.out.println("Max limit values set to " + App.getMaxlimit());
    }

    private void updateMinLimit(int limitValue, Timestamp timestamp) {
        App.setMinLimit(limitValue);
        App.setMinTimestamp(timestamp);
        System.out.println("Max limit values set to " + App.getMinLimit());
    }

    public void connectToDB(String url, String user, String password){
        try {
            connection = DriverManager.getConnection(url, user, password);

            if (connection != null) {
                System.out.println("You successfully connected to database now");
            } else {
                System.out.println("Failed to make connection to database");
            }


        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }
    }

    public void readDataFromDB(String query){
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                if(rs.getString(1).length()>0)updateLimits(rs.getString(1), rs.getInt(2), rs.getTimestamp(3));
            }
            rs.close();
        }catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }
    }
}
