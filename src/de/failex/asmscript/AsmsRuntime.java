package de.failex.asmscript;

import java.io.File;
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

    public AsmsRuntime(File script) {
        this.script = script;
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
    private HashMap<String, Integer> labels = new HashMap<>();

    /** script file **/
    private File script;

    /**
     * all lines of the script
     */
    private ArrayList<String> scriptlines = new ArrayList<>();

    /**
     * Main runtime method
     * @param script the script file
     * @throws AsmRuntimeException when there was an runtime exception
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */

    public void start(File script) throws AsmRuntimeException, NoSuchFieldException, IllegalAccessException {
        String line = "";
        int currline = 0;
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
                                    move(this.getClass().getField("r" + registernumber).getLong(this), args[2]);

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
                                    add(this.getClass().getField("r" + registernumber).getLong(this), args[2]);

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
            default:
                //Check for label
                if (args[0].charAt(args[0].length() - 1) == ':') {
                    if (labels.containsKey(args[0].replace(":", ""))) {
                        throw new AsmRuntimeException("Label already exists!");
                    } else {
                        labels.put(args[0].replace(":", ""), currline);
                    }
                } else {
                    throw new AsmRuntimeException("Invalid instruction " + args[0]);
                }
                break;

        }
    }

    /**
     * Moves a decimal number into a "register"
     *
     * @param number   the number to move
     * @param register save in which register
     */
    private void move(long number, String register) throws NoSuchFieldException, IllegalAccessException {
        this.getClass().getField(register).setLong(this, number);
    }

    /**
     * Adds a number to a register
     * @param number the number to add
     * @param register the register to add to
     */
    private void add(long number, String register) throws NoSuchFieldException, IllegalAccessException {
        this.getClass().getField(register).setLong(this, this.getClass().getField(register).getLong(this) + number);
    }

    /**
     * Performs actions with a number and a register
     * @param number the number
     * @param register what register to change
     * @param method 1 = mov, 2 = add, 3 = sub, 4 = div, 5 = mul
     */
    private void changeRegister(long number, String register, int method) {
        //Migrate all methods / majority of the code in start to this method
    }

}
