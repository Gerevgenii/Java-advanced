package info.kgeorgiy.java.advanced.hello;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Assertions;

public final class Util {
    public static final Charset CHARSET;

    private Util() {
    }

    public static String getString(DatagramPacket packet) {
        return getString(packet.getData(), packet.getOffset(), packet.getLength());
    }

    public static String getString(byte[] data, int offset, int length) {
        return new String(data, offset, length, CHARSET);
    }

    private static void send(DatagramSocket socket, DatagramPacket packet, String request) throws IOException {
        byte[] bytes = getBytes(request);
        packet.setData(bytes);
        packet.setLength(packet.getData().length);
        socket.send(packet);
    }

    public static byte[] getBytes(String string) {
        return string.getBytes(CHARSET);
    }

    public static DatagramPacket createPacket(DatagramSocket socket) throws SocketException {
        return new DatagramPacket(new byte[socket.getReceiveBufferSize()], socket.getReceiveBufferSize());
    }

    public static String request(String string, DatagramSocket socket, SocketAddress address) throws IOException {
        send(socket, string, address);
        return receive(socket);
    }

    public static String receive(DatagramSocket socket) throws IOException {
        DatagramPacket inPacket = createPacket(socket);
        socket.receive(inPacket);
        return getString(inPacket);
    }

    public static void send(DatagramSocket socket, String request, SocketAddress address) throws IOException {
        DatagramPacket outPacket = new DatagramPacket(new byte[0], 0);
        outPacket.setSocketAddress(address);
        send(socket, outPacket, request);
    }

    public static String response(String request) {
        return response(request, "Hello, $");
    }

    public static String response(String request, String format) {
        return format.replaceAll("\\$", request);
    }

    public static Callable<int[]> server(String prefix, int threads, double p, DatagramSocket socket) {
        return () -> {
            int[] expected = new int[threads];
            Random random = new Random(7845743984534545453L);

            try {
                while(true) {
                    DatagramPacket packet = createPacket(socket);
                    socket.receive(packet);
                    String request = getString(packet);
                    String message = "Invalid or unexpected request " + request;
                    Assertions.assertTrue(request.startsWith(prefix), message);
                    String[] parts = request.substring(prefix.length()).split("_");
                    Assertions.assertEquals(2, parts.length, message);

                    try {
                        int thread = Integer.parseInt(parts[0]) - 1;
                        int no = Integer.parseInt(parts[1]);
                        Assertions.assertTrue(0 <= thread && thread < expected.length, message);
                        Assertions.assertEquals(expected[thread] + 1, no, message);
                        if (p >= random.nextDouble()) {
                            int var10002 = expected[thread]++;
                            send(socket, packet, response(request));
                        }
                    } catch (NumberFormatException var13) {
                        throw new AssertionError(message);
                    }
                }
            } catch (IOException var14) {
                IOException e = var14;
                if (socket.isClosed()) {
                    return expected;
                } else {
                    throw e;
                }
            }
        };
    }

    static void setMode(String test) {
    }

    static {
        CHARSET = StandardCharsets.UTF_8;
    }
}
