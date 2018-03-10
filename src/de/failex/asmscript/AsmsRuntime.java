package de.failex.asmscript;

import java.io.File;

/**
 * Currently supported instructions
 *
 * mov a b
 * add a b
 * sub a b
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

    /** General purpose registers **/
    private long r1;
    private long r2;
    private long r3;
    private long r4;
    private long r5;
    private long r6;
    private long r7;
    private long r8;

    /** Instruction pointer **/
    private long rip;

    public void start (File script) throws AsmRuntimeException, NoSuchFieldException {
        String line = "";
        String[] args = line.split(" ");

        switch (args[0]) {
            case "mov":
                if (args.length < 3) {
                    throw new AsmRuntimeException("Not enough arguments for mov");
                } else {
                    long param1 = 0;
                    //Try to parse hex
                    if (args[1].startsWith("0x")) {
                        param1 = Long.parseLong(args[1], 16);
                    } else {
                        //Dec or register
                        try {
                            param1 = Long.parseLong(args[1]);
                        }
                        catch(NumberFormatException e) {
                            if (args[1].startsWith("r")) {
                                int registernumber = 0;
                                try {
                                    registernumber = Integer.parseInt(args[1].substring(1));
                                    if (registernumber > 8) {
                                        throw new AsmRuntimeException("Unkown register " + args[1]);
                                    }

                                    this.getClass().getField("r" + registernumber).setLong();

                                }
                                catch (NumberFormatException ef) {
                                    throw new AsmRuntimeException("Unkown register " + args[1]);
                                }
                            } else {
                                throw new AsmRuntimeException("Unknown register " + args[1]);
                            }
                        }
                    }
                }
        }
    }
}
