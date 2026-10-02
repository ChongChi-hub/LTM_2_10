import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class TCPClient extends JFrame {
    private JTextField inputField;
    private JTextArea outputArea;
    private JButton sendButton;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public TCPClient() {
        // Thiết lập giao diện Swing
        setTitle("TCP Client (In hoa chuỗi)");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Canh giữa màn hình
        setLayout(new BorderLayout(10, 10));

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputField = new JTextField();
        sendButton = new JButton("Gửi");
        
        inputPanel.add(new JLabel("Nhập chuỗi: "), BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Vùng hiển thị kết quả
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10),
                BorderFactory.createLineBorder(Color.GRAY)
        ));

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Xử lý sự kiện nút Gửi
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Xử lý sự kiện nhấn Enter ở ô nhập
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Kết nối tới Server khi khởi động giao diện
        connectToServer();
    }

    private void connectToServer() {
        try {
            // Thay "localhost" bằng IP của server nếu chạy trên máy khác
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputArea.append("=> Đã kết nối tới Server thành công.\n\n");
        } catch (IOException e) {
            outputArea.append("=> Lỗi: Không thể kết nối tới Server!\n" + e.getMessage() + "\n\n");
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (message != null && !message.trim().isEmpty()) {
            if (out != null && socket != null && !socket.isClosed()) {
                // Gửi chuỗi lên server
                out.println(message);
                outputArea.append("Bạn gửi: " + message + "\n");
                
                try {
                    // Nhận kết quả từ server và in ra màn hình
                    String response = in.readLine();
                    if (response != null) {
                        outputArea.append("Server trả về: " + response + "\n");
                    } else {
                        outputArea.append("=> Bị ngắt kết nối với server.\n");
                    }
                } catch (IOException ex) {
                    outputArea.append("=> Lỗi khi đọc kết quả từ server.\n");
                }
            } else {
                outputArea.append("=> Chưa kết nối được với Server.\n");
            }
            // Xóa ô nhập sau khi gửi
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        // Chạy giao diện Swing trên Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TCPClient().setVisible(true);
            }
        });
    }
}
