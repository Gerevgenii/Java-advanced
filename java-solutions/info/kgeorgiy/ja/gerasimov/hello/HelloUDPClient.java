package info.kgeorgiy.ja.gerasimov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class that creates and runs the client
 */
public class HelloUDPClient implements HelloClient {

    /**
     * Runs Hello client.
     * This method should return when all requests are completed.
     *
     * @param host server host
     * @param port server port
     * @param prefix request prefix
     * @param threads number of request threads
     * @param requests number of requests per thread.
     */
    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
            for (int i = 1; i <= threads; i++) {
                final int finalI = i;
                executorService.submit(() -> {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        socket.setSoTimeout(200);
                        final DatagramPacket receivedDatagramPacket = new DatagramPacket(
                                new byte[socket.getReceiveBufferSize()],
                                socket.getReceiveBufferSize()
                        );
                        for (int j = 1; j <= requests; ) {
                            final String sentMessage = prefix + finalI + "_" + j;
                            final byte[] bytes = sentMessage.getBytes(StandardCharsets.UTF_8);
                            final DatagramPacket sendedDatagramPacket = new DatagramPacket(
                                    bytes,
                                    bytes.length,
                                    new InetSocketAddress(host, port)
                            );
                            try {
                                socket.send(sendedDatagramPacket);
                                socket.receive(receivedDatagramPacket);
                            } catch (IOException e) {
                                continue;
                            }
                            final String string = new String(
                                    receivedDatagramPacket.getData(),
                                    receivedDatagramPacket.getOffset(),
                                    receivedDatagramPacket.getLength(),
                                    StandardCharsets.UTF_8
                            );
                            if (string.equals("Hello, " + sentMessage)) {
                                System.out.println(string);
                                j++;
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    /**
     * main function that runs with the initial parameters (port, count of threads)
     * @param args (host server, host port server, port prefix request, prefix, threads number of request threads requests, number of requests per thread).
     */
    public static void main(String[] args) {
        if (args.length != 5) throw new IllegalArgumentException("You must get correctly arguments!!!");
        final HelloClient helloUDPClient = new HelloUDPClient();
        try {
            helloUDPClient.run(
                    args[0],
                    Integer.parseInt(args[1]),
                    args[2],
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4])
            );
        } catch (Exception e) {
            System.out.println("Unchecked exception" + e);
        }
    }
}
