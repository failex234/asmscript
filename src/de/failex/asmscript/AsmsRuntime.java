package de.failex.asmscript;

import java.io.File;
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

    /** script file **/
    private File script;

    public void start(File script) throws AsmRuntimeException, NoSuchFieldException, IllegalAccessException {
        String line = "";
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

}
