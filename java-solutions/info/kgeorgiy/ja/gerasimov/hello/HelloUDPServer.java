package info.kgeorgiy.ja.gerasimov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;
import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The class that creates and runs the server
 */
public class HelloUDPServer implements NewHelloServer {
    private final Collection<Server> servers = new LinkedBlockingQueue<>();

    /**
     * main function that runs with the initial parameters (port, count of threads)
     *
     * @param args [port, count of threads]
     */
    public static void main(final String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("You must get correctly arguments!!!");
        try (final HelloServer helloUDPServer = new HelloUDPServer();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            helloUDPServer.start(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1])
            );
            //noinspection StatementWithEmptyBody
            while (bufferedReader.readLine() == null) {
            }

        } catch (Exception e) {
            System.out.println("Unchecked exception" + e);
        }
    }

    /**
     * Starts a new Hello server.
     * This method should return immediately.
     *
     * @param threads number of working threads.
     * @param ports   port no to response format mapping.
     */
    @Override
    public void start(final int threads, Map<Integer, String> ports) {
        final ExecutorService executorService = Executors.newFixedThreadPool(threads);
        ports.forEach((key, value) -> {
            //noinspection LocalCanBeFinal
            DatagramSocket datagramSocket;
            try {
                datagramSocket = new DatagramSocket(key);
                datagramSocket.setSoTimeout(200);
            } catch (SocketException e) {
                return;
                }
            executorService.submit(() -> start(threads, value, executorService, datagramSocket));
        });
    }

    private void start(final int threads, String stringFormat, ExecutorService executorService, DatagramSocket datagramSocket) {
        for (int i = 0; i < threads; i++) {
            executorService.submit(
                    () -> {
                        final DatagramPacket receivedDatagramPacket;
                        try {
                            receivedDatagramPacket = new DatagramPacket(
                                    new byte[datagramSocket.getReceiveBufferSize()],
                                    datagramSocket.getReceiveBufferSize()
                            );
                            while (!datagramSocket.isClosed()) {
                                datagramSocket.receive(receivedDatagramPacket);
                                final byte[] message = (stringFormat.replace("$", new String(
                                        receivedDatagramPacket.getData(),
                                        receivedDatagramPacket.getOffset(),
                                        receivedDatagramPacket.getLength(),
                                        StandardCharsets.UTF_8
                                ))).getBytes(StandardCharsets.UTF_8);
                                datagramSocket.send(new DatagramPacket(
                                        message,
                                        message.length,
                                        receivedDatagramPacket.getSocketAddress()
                                ));
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
            );
        }
        servers.add(new Server(datagramSocket, executorService));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        servers.forEach(Server::close);
        servers.clear();
    }
}