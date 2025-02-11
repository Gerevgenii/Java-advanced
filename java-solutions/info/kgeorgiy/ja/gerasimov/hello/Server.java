package info.kgeorgiy.ja.gerasimov.hello;

import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;

record Server(DatagramSocket datagramSocket, ExecutorService executorService) implements AutoCloseable {
    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        datagramSocket.close();
        executorService.close();
    }
}
