public class Bit {


    private boolean node;
    // You must implement the logic for these operations –
    // you cannot use the logic (&, &&, |, ||)
    // operators for these operations. You may use “if” or “switch”.
    // Fill in the Bit class in the code skeleton.
    // Most functions have 2 versions – a static version and an instance version;
    // make the instance version call the static version.
    public Bit(boolean value) {

        this.node = value;
    }

    public boolean getValue() {

        return node;
    }

    public void assign(boolean value) {

        if(value){
            node = true;
        }
        if(!value){
            node =false;
        }
    }
    public void and(Bit b2, Bit result) {

        and(this, b2, result);

    }
        //false, true -> answer: false
    public static void and(Bit b1, Bit b2, Bit result) {

        //instance version should call static version
        //t1.and(t2,result); USAGE
        //t1 and t2 are set to true in the test example
        //result is set to true

        //case when they are equal
        if(b1.getValue() == b2.getValue()){
            if(b1.getValue()){
                result.assign(true);
            }
            else if(!b1.getValue()){
                result.assign(false);
            }
        }
        //if values do not even equal to each other, It just returns false
        else if(b1.getValue() != b2.getValue()){
            result.assign(false);
        }
    }

    public void or(Bit b2, Bit result) {

        or(this, b2, result);
    }

    public static void or(Bit b1, Bit b2, Bit result) {

        //case when they are equal
        if(b1.getValue() == b2.getValue()){
            if(b1.getValue()){
                result.assign(true);
            }
            else if(!b1.getValue()){
                result.assign(false);
            }
        }
        //if values do not even equal to each other, It just returns true for OR operation
        else if(b1.getValue() != b2.getValue()){
            result.assign(true);
        }
    }

    public void xor(Bit b2, Bit result) {
        xor(this, b2, result);
    }

    public static void xor(Bit b1, Bit b2, Bit result) {
        //case when they are equal BUT both true is also false
        if(b1.getValue() == b2.getValue()){
                result.assign(false);
        }
        //if values do not even equal to each other, It just returns true for XOR operation
        else if(b1.getValue() != b2.getValue()){
            result.assign(true);
        }
    }

    public static void not(Bit b2, Bit result) {
        //USAGE: t1.not(result)
        //Reverse the operation
        if(b2.getValue() == true){
            result.assign(false);
        }

        else if(b2.getValue() == false) {
            //b2.assign(true);
            result.assign(true);
        }




    }
    public void not(Bit result) {
        not(this, result);
    }
    //Must use the boolean – you may not use other data types.
    //Member(s) should be private. For toString(), use “1” or “0”.
    public String toString() {
        if(getValue()){
            return "1";
        }
        else if(!getValue()){
            return "0";
        }

        return "0";
    }
}
