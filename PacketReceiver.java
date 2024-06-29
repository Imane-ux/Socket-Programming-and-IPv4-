import java.io.*;
import java.net.*;

public class PacketReceiver {
    public static void main(String[] args) {
        final int SERVER_PORT = 49152; // The private port to listen on

        try (
            // Create a server socket bound to the specified port
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        ) {
            System.out.println("Server is running and waiting for client connection...");

            // Wait for a client connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            // Set up input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Receive the encoded data from the client
            String hexEncodedData = in.readLine();
            System.out.println("Received encoded data from client: " + hexEncodedData);

            String header = hexEncodedData.substring(0, 40); // header length is 20 bytes
            System.out.println("Header received:" + header);
            String payload = hexEncodedData.substring(40);
            System.out.println("payload received:" + payload);

            //this to check if it needed to remove padding, but nahh or maybe, test with real zeros. 
            //hexEncodedData= removePadding(hexEncodedData);
            //System.out.println(hexEncodedData);

            //check if chesksum is valid before encoding.//use to see if corrupted.
            Boolean isValid= isChecksumValid(header);
            //System.out.println(isValid);
            if (isValid){
                // Decode the hexadecimal data to ASCII             
                //String decodedData = hexToAscii(hexEncodedData);
                String decodedData = hexToAscii(payload);
                System.out.println("Decoded data: " + decodedData);

                // Send acknowledgment to the client
                out.println("Encoded stream received successfully."); //60 bits or 20 bytes. payload.length()
                System.out.println("The data received from " + hexToIp(header.substring(32,40)) + " is " + decodedData);
                System.out.println("The data has "+ (payload.length()*4) + " bits or "+ (payload.length()/2) + " bytes. Total length of the packet is "+ (hexEncodedData.length()/2) + " bytes.");
                System.out.println("The verification of the checksum demonstrates that the packet received is correct.");

            }else{
                //Sends stream is corrupted message
                out.println("The verification of the checksum demonstrates that the packet received is corrupted. Packet discarded!");
            }

        } catch (IOException e) {
            System.err.println("I/O Exception occurred when setting up server or communicating with client.");
            e.printStackTrace();
        }
    }

    // Function to convert hexadecimal string to ASCII string
    public static String hexToAscii(String hexString) {
        StringBuilder asciiString = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            String hex = hexString.substring(i, i + 2);
            int decimal = Integer.parseInt(hex, 16);
            asciiString.append((char) decimal);
        }
        return asciiString.toString();
    }

    //also doesnt work, but it seems like it s decoding properly  elems with null o , try one payload ends with real 0.
    /*private static String removePadding(String payload) {
        // Remove any padding characters (e.g., '\0')
        //return payload.trim(); didnt work, come to think of it o can add this logic to the decode method
        int endIndex = payload.length();
        // Find the last non-null character index in the payload
        while (endIndex > 0 && payload.charAt(endIndex - 1) == '\0') {
            endIndex--;
        }
        // Return the substring from the start to the last non-null character
        return payload.substring(0, endIndex);
    }*/
    private static boolean isChecksumValid(String header) {
        // Ensure a 20 bytes header 
        if (header.length() != 40) {
            throw new IllegalArgumentException("Header length must be equal to 40 characters.");
        }
    
        // Extracting the checksum for testing purposes (4 characters)
        String checksumStr = header.substring(20, 24);
    
        // Calculate the expected checksum
        int sum = 0;
        for (int i = 0; i < header.length(); i += 4) {
            if (i >= 20 && i < 24) {
                continue; // Skip checksum in calculation just to test
            }
            String hexWord = header.substring(i, i + 4); // Take 4 characters at a time (2 bytes)
            int wordValue = Integer.parseInt(hexWord, 16); // Convert hex to integer
            sum += wordValue; // Add to sum
        }
    
        // Add the extracted checksum to the sum
        int checksum = Integer.parseInt(checksumStr, 16); // Convert checksum hex string to integer
        sum += checksum;
    
        // Handle overflow
        while ((sum >> 16) != 0) {
            sum = (sum & 0xFFFF) + (sum >> 16); // Add carry back to sum
        }
    
        // Take one's complement
        int calculatedChecksum = ~sum & 0xFFFF; // Invert bits and mask with 0xFFFF to get 16-bit result
    
        // Check if calculated checksum is 0x0000 (all bits set to 1)
        return calculatedChecksum == 0x0000;
    }
    private static String hexToIp(String hexString){
        
        if (hexString == null || hexString.length() != 8 || !hexString.matches("[0-9A-Fa-f]+")) {
            throw new IllegalArgumentException("Invalid hexadecimal string for IP address");
        }
    
        // Convert hexadecimal string to integer
        int ipInt = Integer.parseInt(hexString, 16);
    
        // Extract octets from integer and construct IP address string
        StringBuilder ipAddress = new StringBuilder();
        ipAddress.append((ipInt >> 24) & 0xFF).append(".")
                 .append((ipInt >> 16) & 0xFF).append(".")
                 .append((ipInt >> 8) & 0xFF).append(".")
                 .append(ipInt & 0xFF);
    
        return ipAddress.toString();
    }
}

