package Service;


import Student.StudentClient;
import com.project.grpc.register.User;
import com.project.grpc.register.studentGrpc;
import io.grpc.stub.StreamObserver;
import java.sql.*;

import static java.sql.DriverManager.getConnection;

public class registration extends studentGrpc.studentImplBase{

    //MySQL info
    String url = "jdbc:mysql://localhost:3306/student_registration";
    String user = "root";
    String pass = "";
    @Override
    public void login(User.LoginRequest request, StreamObserver<User.Response> responseObserver) throws SQLException, ClassNotFoundException {


        String userName = request.getUserName();
        String password = request.getPassword();

        //Checking database
        ResultSet resultSet = checkLoginInfo(userName, password);

        //Creating response
        User.Response.Builder response =  new User.Response.Builder();
        while(resultSet.next()) {
            if (resultSet.getInt(1) == 1) {
                response.setResponseCode(200).
                        setResponse("Successfully logged in");
            } else {
                response.setResponseCode(404).setResponse("Wrong username or password");
            }
            break;
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private ResultSet checkLoginInfo(String userName, String password) throws ClassNotFoundException, SQLException {
        //Connecting to MySQL database
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = getConnection(url, user, pass);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM login" +
                " WHERE userName = ? && password = ?)");
        statement.setString(1, userName);
        statement.setString(2, password);
        ResultSet rs = statement.executeQuery();
        return rs;
    }

    @Override
    public void register(User.RegisterRequest request, StreamObserver<User.RegResponse> responseObserver)throws SQLException {
        long regNo = request.getRegistrationID();
        String sub = request.getStudentName();

        //Checking database
        ResultSet resultSet = checkRegInfo(regNo);


        //Creating response
        User.RegResponse.Builder regResponse = new User.RegResponse.Builder();
        while(resultSet.next()){
            if(resultSet.getInt(1) == 1){
                regResponse
                        .setResponse("Registration ID " + regNo + " is already registered")
                        .setResponseCode(500);
            }else{
                Connection connection = getConnection(url, user, pass);
                //Adding new student
                PreparedStatement statement = connection.prepareStatement
                        ("INSERT INTO reg VALUES('"+regNo+"', '"+sub+"')");
                statement.executeUpdate();
                regResponse.setResponse(sub +
                                " with registration ID " + regNo + " is now registered successfully").
                        setResponseCode(300);

            }
            break;
        }
        responseObserver.onNext(regResponse.build());
        responseObserver.onCompleted();
    }

    private ResultSet checkRegInfo(long regID) throws SQLException {
        //Connecting to MySQL database
        Connection connection = getConnection(url, user, pass);
        PreparedStatement statement = connection.prepareStatement
                ("SELECT EXISTS(SELECT * FROM reg WHERE regNo = ?)");
        statement.setInt(1, (int) regID);
        ResultSet rs = statement.executeQuery();
        return rs;
    }







    @Override
    public void logout(User.LogoutRequest request, StreamObserver<User.Response> responseObserver) {
        super.logout(request, responseObserver);
    }


}
