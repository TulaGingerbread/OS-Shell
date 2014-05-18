package com.tulagingerbread.shell;

import java.io.IOException;
import java.util.Scanner;

public class Shell implements Runnable {

    private final String line;

    public Shell(String line) {
        this.line = line;
    }

    public static void main(String[] args) {
        System.out.println("Bash-like shell!");
        System.out.print("$");
        Scanner in = new Scanner(System.in);
        try {
            while (in.hasNextLine()) {
                Shell sh = new Shell(in.nextLine());
                Thread t = new Thread(sh);
                t.start();
                t.join(0);
                System.out.print("\n$");
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder(line.split("\\s+"));
            Process p = null;
            try {
                p = pb.start();
            }
            catch (IOException e) {
                try {
                    pb = new ProcessBuilder("cmd", "/c", line);
                    p = pb.start();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            finally {
                Pipe.pipe(p.getErrorStream(), System.err);
                Pipe.pipe(p.getInputStream(), System.out);
                System.out.println("Exit value: " + p.waitFor());
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
