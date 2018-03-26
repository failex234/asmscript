package de.failex.asmscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Currently supported instructions
 * <p>
 * mov a b
 * add a b
 * sub a b
 * out a
 * div a b
 * mul a b
 * tst a b
 * jmp <address/label>
 * jne <address/label>
 * jgt <address/label>
 * jlz <address/label>
 * je <address/label>
 * label:
 */
public class AsmsRuntime {

    public enum AsmsMethods {
        ;
        int METHOD_MOV = 1;
        int METHOD_ADD = 2;
        int METHOD_SUB = 3;
        int METHOD_DIV = 4;
        int METHOD_MUL = 5;
    }

    public AsmsRuntime(File script, boolean log) {
        this.script = script;
        try {
            start(script);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * General purpose "registers" / Arithmetic "registers"
     **/
    private long r1;
    private long r2;
    private long r3;
    private long r4;
    private long r5;
    private long r6;
    private long r7;
    private long r8;

    /**
     * Instruction pointer
     **/
    private long rip;

    /**
     * Flags
     */
    private boolean gtflag;
    private boolean ltflag;
    private boolean eqflag;
    private boolean nqflag;

    /**
     * Labels and what line they reference
     */
    private HashMap<String, Long> labels = new HashMap<>();

    /**
     * script file
     **/
    private File script;

    /**
     * all lines of the script
     */
    private ArrayList<String> scriptlines = new ArrayList<>();

    /**
     * Main runtime method
     *
     * @param script the script file
     * @throws AsmRuntimeException    when there was an runtime exception
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */

    public void start(File script) throws AsmRuntimeException, NoSuchFieldException, IllegalAccessException {
        readScript();
        while (rip < scriptlines.size()) {
            String line = scriptlines.get((int) rip);
            String[] args = line.split(" ");

            switch (args[0]) {
                case "mov":
                    if (args.length < 3) {
                        throw new AsmRuntimeException("Not enough arguments for mov");
                    } else {
                        //Try to parse hex
                        if (args[1].startsWith("0x")) {
                            move(Long.parseLong(args[1], 16), args[2]);
                        } else {
                            //Dec or register
                            try {
                                move(Long.parseLong(args[1]), args[2]);
                            } catch (NumberFormatException e) {
                                if (args[1].startsWith("r")) {
                                    int registernumber;
                                    try {
                                        registernumber = Integer.parseInt(args[1].substring(1));
                                        if (registernumber > 8) {
                                            throw new AsmRuntimeException("Unknown register " + args[1]);
                                        }

                                        //Get number from field and then move
                                        move(this.getClass().getDeclaredField("r" + registernumber).getLong(this), args[2]);

                                    } catch (NumberFormatException ef) {
                                        throw new AsmRuntimeException("Unknown register " + args[1]);
                                    }
                                } else {
                                    throw new AsmRuntimeException("Unknown register " + args[1]);
                                }
                            }
                        }
                    }
                    rip++;
                    break;
                case "add":
                    //Literally the same
                    if (args.length < 3) {
                        throw new AsmRuntimeException("Not enough arguments for add");
                    } else {
                        //Try to parse hex
                        if (args[1].startsWith("0x")) {
                            add(Long.parseLong(args[1], 16), args[2]);
                        } else {
                            //Dec or register
                            try {
                                add(Long.parseLong(args[1]), args[2]);
                            } catch (NumberFormatException e) {
                                if (args[1].startsWith("r")) {
                                    int registernumber;
                                    try {
                                        registernumber = Integer.parseInt(args[1].substring(1));
                                        if (registernumber > 8) {
                                            throw new AsmRuntimeException("Unknown register " + args[1]);
                                        }

                                        //Get number from field and then move
                                        add(this.getClass().getDeclaredField("r" + registernumber).getLong(this), args[2]);

                                    } catch (NumberFormatException ef) {
                                        throw new AsmRuntimeException("Unknown register " + args[1]);
                                    }
                                } else {
                                    throw new AsmRuntimeException("Unknown register " + args[1]);
                                }
                            }
                        }
                    }
                    rip++;
                    break;
                case "jmp":
                    if (args.length < 2) {
                        throw new AsmRuntimeException("Not enough argument for jmp");
                    } else {
                        //Check if label exists
                        String label = args[1];

                        if (!labels.containsKey(label)) throw new AsmRuntimeException("Label " + label + " doesn't exist");

                        rip = labels.get(label) + 1;
                    }
                    break;
                default:
                    //Check for label
                    if (args[0].charAt(args[0].length() - 1) == ':') {
                        if (labels.containsKey(args[0].replace(":", ""))) {
                            throw new AsmRuntimeException("Label already exists!");
                        } else {
                            labels.put(args[0].replace(":", ""), rip);
                            rip++;
                        }
                    } else {
                        throw new AsmRuntimeException("Invalid instruction " + args[0]);
                    }
                    break;

            }
        }
        System.out.println("Runtime complete!");
        printAllValues();
    }

    private void readScript() throws AsmRuntimeException {
        //Read each line and save it in scriptlines
        try {
            BufferedReader br = new BufferedReader(new FileReader(script));
            String line = br.readLine();

            while (line != null) {
                scriptlines.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            throw new AsmRuntimeException("Error while reading Asm script");
        }
    }

    /**
     * Moves a decimal number into a "register"
     *
     * @param number   the number to move
     * @param register save in which register
     */
    private void move(long number, String register) throws NoSuchFieldException, IllegalAccessException {
        this.getClass().getDeclaredField(register).setLong(this, number);
    }

    /**
     * Adds a number to a register
     *
     * @param number   the number to add
     * @param register the register to add to
     */
    private void add(long number, String register) throws NoSuchFieldException, IllegalAccessException {
        this.getClass().getDeclaredField(register).setLong(this, this.getClass().getDeclaredField(register).getLong(this) + number);
    }

    /**
     * Prints values from all registers, rip and flags
     */
    private void printAllValues() throws NoSuchFieldException, IllegalAccessException {
        //Go through all registers
        for (int i = 1; i < 9; i++) {
            System.out.printf("r%d: %d\n", i, this.getClass().getDeclaredField("r" + i).getLong(this));
        }
        System.out.printf("rip: %d\n", rip);

        System.out.printf("gtflag: %b\n", gtflag);
        System.out.printf("ltflag: %b\n", ltflag);
        System.out.printf("eqflag: %b\n", eqflag);
        System.out.printf("nqflag: %b\n", nqflag);

        System.out.println("\nLabels:");
        for (String s : labels.keySet()) {
            System.out.printf("%s -> %d\n", s, labels.get(s));
        }
    }

}
