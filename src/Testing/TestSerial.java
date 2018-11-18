package Testing;

import java.io.Serializable;

public class TestSerial implements Serializable {

    private String stringField;

    public TestSerial(String stringField){
        this.stringField = stringField;
    }

    public String getStringField() {
        return stringField;
    }

}

