package Student;

import com.project.grpc.register.User;
import com.project.grpc.register.studentGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class StudentClient {
    public static void main(String[] args) {
        // Connection established with server
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8984).usePlaintext().build();
        // Synchronous communication
        studentGrpc.studentBlockingStub studentBlockingStub = new studentGrpc.studentBlockingStub(managedChannel);

        //Login
        boolean auth = false;
        Scanner inp = new Scanner(System.in);
        while (!auth) {
            System.out.print("Enter user name: ");
            String name = inp.next();
            System.out.print("Enter password: ");
            String pass = inp.next();
            User.LoginRequest loginRequest = User.LoginRequest.newBuilder().
                    setUserName(name).setPassword(pass).build();
            User.Response response = studentBlockingStub.login(loginRequest);
            if (response.getResponseCode() == 200) {
                auth = true;
            }
            System.out.println(response.getResponse());
        }

        //Registration
        System.out.print("Enter registration ID: ");
        long ID = inp.nextLong();
        System.out.print("Enter student name: ");
        String name = inp.next();

        User.RegisterRequest registerRequest = User.RegisterRequest.newBuilder().
                setRegistrationID(ID).setStudentName(name).build();
        User.RegResponse regResponse = studentBlockingStub.register(registerRequest);
        System.out.println(regResponse.getResponse());
    }
    }