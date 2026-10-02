import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
        int port = 12345;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server đang chờ kết nối ở port " + port + "...");
            
            // Lắng nghe liên tục để nhận nhiều client (Multithread)
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client mới kết nối: " + socket.getInetAddress().getHostAddress());
                
                // Tạo một Thread mới cho mỗi client để xử lý đồng thời
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }
}

// Lớp xử lý từng Client riêng biệt bằng Thread
class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String inputLine;
            // Đọc chuỗi do client gửi lên
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Nhận từ client (" + socket.getInetAddress() + "): " + inputLine);
                
                // Chuyển thành chữ in hoa
                String upperCaseLine = inputLine.toUpperCase();
                
                // Trả kết quả về cho client
                out.println(upperCaseLine);
            }
        } catch (IOException e) {
            System.out.println("Lỗi xử lý client " + socket.getInetAddress() + ": " + e.getMessage());
        } finally {
            try {
                System.out.println("Client đóng kết nối: " + socket.getInetAddress());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
