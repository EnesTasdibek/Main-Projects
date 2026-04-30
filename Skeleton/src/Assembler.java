import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Assembler {
    static HashMap<String, String> opCodes = new HashMap<>();

    //may need to load instructions
    public Assembler() {

        //input = new String[100];
        opCodes = new HashMap<>();
        //return is 01010
        //halt is 00000
        //just add halt for those who are one command
        opCodes.put("return","01010");
        opCodes.put("halt","00000");
        opCodes.put("and","00010");

        opCodes.put("r0", "00000");
        opCodes.put("r1", "00001");
        opCodes.put("r2", "00010");
        opCodes.put("r3", "00011");
        opCodes.put("r4", "00100");
        opCodes.put("r5", "00101");
        opCodes.put("r6", "00110");
        opCodes.put("r7", "00111");
        opCodes.put("r8", "01000");
        opCodes.put("r9", "01001");
        opCodes.put("r10", "01010");
        opCodes.put("r11", "01011");
        opCodes.put("r12", "01100");
        opCodes.put("r13", "01101");
        opCodes.put("r14", "01110");
        opCodes.put("r15", "01111");
        opCodes.put("r16", "10000");
        opCodes.put("r17", "10001");
        opCodes.put("r18", "10010");
        opCodes.put("r19", "10011");
        opCodes.put("r20", "10100");
        opCodes.put("r21", "10101");
        opCodes.put("r22", "10110");
        opCodes.put("r23", "10111");
        opCodes.put("r24", "11000");
        opCodes.put("r25", "11001");
        opCodes.put("r26", "11010");
        opCodes.put("r27", "11011");
        opCodes.put("r28", "11100");
        opCodes.put("r29", "11101");
        opCodes.put("r30", "11110");
        opCodes.put("r31", "11111");

        opCodes.put("subtract", "00101");
        opCodes.put("multiply", "00011");
        opCodes.put("leftshift", "00100");
        opCodes.put("or", "00110");
        opCodes.put("rightshift", "00111");
        opCodes.put("compare", "01011");
        opCodes.put("add", "00001");
        opCodes.put("call", "01001");
        opCodes.put("syscall", "01000");
        opCodes.put("load", "10010");
        opCodes.put("store", "10011");
        opCodes.put("copy", "10100");

        //ble's etc.
        opCodes.put("ble", "01100");
        opCodes.put("blt", "01101");
        opCodes.put("bge", "01110");
        opCodes.put("bgt", "01111");
        opCodes.put("beq", "10000");
        opCodes.put("bne", "10001");

    }
    //Check if the second split is register or not
    public static String[] assemble(String[] input) {

        Word32 binary = new Word32();
        Word32 otherBinary = new Word32();
        new Assembler();
        StringBuilder builder = new StringBuilder();

       for(int j = 0;j< input.length ;j++){
            String[] split= input[j].split(" ");
           if (split.length==1) {
               builder.append(opCodes.get(split[0]));
               builder.append(opCodes.get("halt"));
               builder.append(opCodes.get("halt"));
               builder.append("0");

           }
           else if (split[1].contains("r")){
                    //System.out.println("2R");
                    if(opCodes.containsKey(split[0])){
                        //System.out.println("contains key:" +split[0]);
                        //add is first 5 bits
                        builder.append(opCodes.get(split[0]));
                        //format bit is zero
                        builder.append("0");
                        //source register
                        builder.append(opCodes.get(split[1]));
                        //destination register
                        builder.append(opCodes.get(split[2]));
                        //now we can return assembled string!!!!
                    }
                }
                //call return // 5bit opcode + 11bits signed immediate value
                //no, this is
                else if (split.length ==2 )
                {
                   // StringBuilder builder3 = new StringBuilder();
                    //System.out.println("Call/Return");
                    if(opCodes.containsKey(split[0])){
                        //System.out.println("contains key:" +split[0]);
                        //add is first 5 bits
                        builder.append(opCodes.get(split[0]));
                        //for the case of single return/call
                        TestConverter.fromInt(Integer.parseInt(split[1]), binary);
                        //need bottom 11 bits only
                        for(int i=21;i<32;i++){

                            if(binary.k[i].getValue()){
                                builder.append("1");
                            }
                            else if(!binary.k[i].getValue()){
                                builder.append("0");
                            }
                        }
                        //now we can return assembled string!!!!
                    }

                } else{
               //StringBuilder builder4 = new StringBuilder();
                    //System.out.println("Immediate");
                    if(opCodes.containsKey(split[0])){
                        //System.out.println("contains key:" +split[0]);
                        //add is first 5 bits
                        builder.append(opCodes.get(split[0]));
                        //format bit is 1
                        builder.append("1");
                        //5 bit signed immediate value
                        TestConverter.fromInt(Integer.parseInt(split[1]), otherBinary);
                        //need bottom 5 bits only
                        for(int i=27;i<32;i++){

                            if(otherBinary.k[i].getValue()){
                                builder.append("1");
                            }
                            else if(!otherBinary.k[i].getValue()){
                                builder.append("0");
                            }
                        }
                        //destination register
                        builder.append(opCodes.get(split[2]));
                        //now we can return assembled string!!!!

                    }
                    
                }
            }

        //format bit is 1, immediate
        //if two r's, 2R. Format bit is zero.
        // if add, source register(fist r) is the number after r to binary in 5 bits
        // Destination register is (second r) is the number after r to binary in 5 bits.
        //String[] newOne = builder.toString().split(" ");
        //if one R, immediate. Format bit is 1. signed intermediate value is the number and is in 5 bits.
        //the destination register is 5 bits and the second value after signed  immediate value is stored.

        //return string at their 16th bits
        //save them at 16ths
        int size = builder.length()/16;
        String[] returnVal = new String[size];
        if(builder.length()>=16){


            for(int i=0;i< returnVal.length;i++){
                    returnVal[i] = builder.substring((i*16),(i*16 )+16);
            }
            return returnVal;
        }
        else{
            return new String[]{builder.toString()};
        }
    }

    public static String[] finalOutput(String[] input) {
        //this returns 32 bit line added to assemble()
        //adding zeros to complete to 32bit if odd

        //I used LinkedLists for easy conversion,
        //and also because it was imported originally
        //String[] assembledBits = assemble(input);
        if (input == null) {
            return null;
        }

        LinkedList<String> queue = new LinkedList<>(Arrays.asList(input));

        LinkedList<String> resultList = new LinkedList<>();

        while (!queue.isEmpty()) {
            //FIFO help
            String first16 = queue.poll();
            String second16 = queue.poll();

            //It would be null if that was any odd'TH line
            if (second16 == null) {
                second16 = "0000000000000000";
            }

            resultList.add(first16 + second16);
        }

        return resultList.toArray(new String[0]);

    }
}
