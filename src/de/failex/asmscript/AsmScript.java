package de.failex.asmscript;

import java.io.File;

public class AsmScript {

    private static File scriptfile;

    public static void main(String[] args) {
        if (args.length != 0) {
            switch (args[0]) {
                case "--help":
                case "-h":
                    printHelp();
                    break;
                case "--version":
                case "-v":
                    printVersion();
                    break;
                default:
                    //Check file
                    scriptfile = new File(args[args.length - 1]);
                    if (scriptfile.exists()) {
                        new AsmsRuntime(scriptfile);
                    } else {
                        printUsage();
                    }
            }
        }
    }

    private static void printHelp() {
        System.out.println("AsmScript help menu\n");
        printUsage();
        System.out.println("\npossible arguments: ");
        System.out.println("--help    | -h    -- show this menu");
        System.out.println("--version | -v    -- show version info");
    }

    private static void printUsage() {
        System.out.println("usage: asmscript [arguments...] <file>");
    }

    private static void printVersion() {
        System.out.println("AsmScript Version 0.1 by Felix Naumann");
        System.out.println("This software is free to redistribute under");
        System.out.println("the MIT License as long as you link back");
        System.out.println("to the original project.");
    }
}
