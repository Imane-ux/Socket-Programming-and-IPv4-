import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PacketSender {
    
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost"; //  As it always resolves to the IP address 127.0.0.1 (local machine)
        final String serverIP = "127.0.0.1"; //InetAddress.getByName(SERVER_ADDRESS).getHostAddress(); 
        //we need to make the above variable? but thsis souls solve everywhere test
        final int SERVER_PORT = 49152; // Private server port chosen
        final int HEADER_LENGTH = 20; //for now.

        try (
            // Establish socket connection to the server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);//to send text data over the network through the socket.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //to receive? text data over the network through the socket.
            Scanner scanner = new Scanner(System.in);
        ) {
            // Read input from the user
            System.out.print("Enter data to send: ");
            String payloadData = scanner.nextLine();

            // Ensure payload length + header length is divisible by 8
            String paddedPayload = checkPadding(payloadData, HEADER_LENGTH);

            //converts the length of payload to hex to added to header
            String hexPacketLength= decimalToHex(paddedPayload.length()+ HEADER_LENGTH);
            
            String checksum="0000";// intials checksum value before it gets calculated

            // Generate header
            String header = buildHeader(serverIP, serverIP, checksum, hexPacketLength);
            System.out.println("initialheader: " + header);

            // ca;culate checkum here from header
            checksum= calculateChecksum(header);
            //here we'll set header to the a new one with new checksum.
            header = buildHeader(serverIP, serverIP, checksum, hexPacketLength);
            System.out.println("new header: " + header);

            // Encode input data to hexadecimal
            String hexEncodedData = asciiToHex(paddedPayload);

            // Send the encoded data to the server
            //out.println(hexEncodedData);
            out.println( header + hexEncodedData);
            // Wait for acknowledgment from the server
            String response = in.readLine();
            System.out.println("Server response: " + response);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_ADDRESS);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O Exception occurred when communicating with the server.");
            e.printStackTrace();
        }
    }

    // Function to convert ASCII string to hexadecimal string
    public static String asciiToHex(String asciiString) {
        StringBuilder hexString = new StringBuilder();
        for (char ch : asciiString.toCharArray()) {
            hexString.append(String.format("%02X", (int) ch)); // Use uppercase format for consistency
        }
        return hexString.toString();
    }

    private static String buildHeader(String sourceIP, String destinationIP, String checksum, String packetLength ) { //to build the header 
            String versionIHL = "45"; // Version (4) and IHL (5) combined in hexadecimal
            String typeOfService = "00";
            String totalLength = packetLength;
            //String totalLength = "0028"; // Total length field (40 bytes = 20 bytes header + 20 bytes payload)
            String identification = "1c46";
            String flagsFragmentOffset = "4000";
            String TTL = "40"; // Time to Live (TTL)
            String protocol = "06"; // Protocol (TCP)
            String checkSum = checksum; // Use the provided checksum value
            String sourceIPAddress = ipToHex(sourceIP); // Convert source IP address to hexadecimal
            String destinationIPAddress = ipToHex(destinationIP); // Convert destination IP address to hexadecimal
            //String payload = "434f4c4f4d4249412032202d204d4553"; // Payload hex data added here or in main same
        
            // Construct the complete header
            StringBuilder header = new StringBuilder();
            header.append(versionIHL)
                  .append(typeOfService)
                  .append(totalLength)
                  .append(identification)
                  .append(flagsFragmentOffset)
                  .append(TTL)
                  .append(protocol)
                  .append(checkSum)
                  .append(sourceIPAddress)
                  .append(destinationIPAddress);
                  //.append(payload);
        
            return header.toString();
    }

    // Private method to add padding to payload
    private static String checkPadding(String payload, int headerLength) {
        int payloadLength = payload.getBytes().length;
        int totalLength = headerLength + payloadLength;

        if (totalLength % 8 != 0) {
            int paddingNeeded = 8 - (totalLength % 8); // Calculate padding needed
            String padding = "\0".repeat(paddingNeeded); // Create padding string with zeros

            // Append padding to the payload
            payload = payload + padding;
        }
        return payload;
    }

    // Method to convert IP address from string to hexadecimal
    private static String ipToHex(String ipAddress) {
        StringBuilder hexIP = new StringBuilder();
        String[] octets = ipAddress.split("\\.");

        for (String octet : octets) {
            int decimal = Integer.parseInt(octet); // Convert the octet to integer
            String hex = Integer.toHexString(decimal); // Convert the integer to hexadecimal
            hexIP.append(String.format("%02X", Integer.parseInt(hex, 16))); // Ensure two-digit hex representation
        }

        return hexIP.toString();
    }

    private static String calculateChecksum(String initialHeader){
        // Ensure the header length is multiple of 4 (since each byte is represented by 2 hex characters)
        /*if (initialHeader.length() % 4 != 0) {
            throw new IllegalArgumentException("Header length must be a multiple of 4.");
        }*/// cuz it issss.

        int sum = 0;
        // Sum up 16-bit words (2 bytes) from the header
        for (int i = 0; i < initialHeader.length(); i += 4) {
            String hexWord = initialHeader.substring(i, i + 4); // Take 4 characters at a time (2 bytes)
            int wordValue = Integer.parseInt(hexWord, 16); // Convert hex to integer
            sum += wordValue; // Add to sum
        }

        // Handle overflow
        while ((sum >> 16) != 0) {
            sum = (sum & 0xFFFF) + (sum >> 16); // Add carry back to sum
        }

        // Take one's complement
        int checksum = ~sum & 0xFFFF; // Invert bits and mask with 0xFFFF to get 16-bit result

        // Format as 4-digit hexadecimal
        return String.format("%04X", checksum);
    }

    private static String decimalToHex(int decimalValue) {
        // Convert decimal to hexadecimal string with leading zeros
        String hexString = String.format("%04X", decimalValue);
        
        return hexString;
    }

}