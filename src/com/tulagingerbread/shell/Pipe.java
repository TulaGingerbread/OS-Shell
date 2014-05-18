package com.tulagingerbread.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Pipe implements Runnable {

    private final InputStream input;
    private final OutputStream output;

    private Pipe(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    public static InputStream pipeAll(Process... processes) {
        Process p1 = processes[0], p2 = p1;
        for (int i = 0, l = processes.length - 1; i < l; i++) {
            p1 = processes[i];
            p2 = processes[i+1];
            new Thread(new Pipe(p1.getInputStream(), p2.getOutputStream())).start();
        }
        return p2.getInputStream();
    }

    public static void pipe(InputStream in, OutputStream out) {
        Thread t = new Thread(new Pipe(in, out));
        t.start();
    }

    @Override
    public void run() {
        byte[] a = new byte[32];
        int r;
        try {
            while ((r = input.read(a)) != -1) {
                output.write(a, 0, r);
                output.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (!input.equals(System.in)) input.close();
                if (!output.equals(System.out) && !output.equals(System.err)) output.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
