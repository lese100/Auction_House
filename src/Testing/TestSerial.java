package Testing;

import java.io.Serializable;

/**
 * An object for testing the network transmission of a serializable object.
 * The object simply holds a string and provides a method to retrieve
 * that string.
 * created: 11/17/18 by thf
 * last modified: 11/18/18 by wdc (updating comments)
 * previously modified: 11/17/18 by thf (creation)
 * @author Tyler Fenske (thf)
 * @author Warren D. Craft (wdc)
 * @author Liam Brady (lb)
 */
public class TestSerial implements Serializable {

    private String stringField;

    public TestSerial(String stringField){
        this.stringField = stringField;
    }

    public String getStringField() {
        return stringField;
    }

}

