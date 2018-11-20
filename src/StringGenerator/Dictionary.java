package StringGenerator;

import java.util.Random;

public class Dictionary {

    private boolean useOtherWords = false;

    private String[] words = {"bee", "drain", "stiff", "aquatic", "shade", "reflect",
            "multiply", "strengthen", "smart", "coat", "form", "jail", "warm",
            "glamorous", "mitten", "passenger", "hand", "sassy", "birthday",
            "glistening", "crook", "silky", "arithmetic", "unfasten", "greasy",
            "probably", "lunch", "wrestle", "spade", "flesh", "use", "steep",
            "thundering", "direction", "cuddly", "habitual", "labored", "shoe",
            "cactus", "continue"};

    private String[] otherWords = {"zero", "one", "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "ten"};

    public String getRandomWord(){
        Random rand = new Random();
        String result;

        if(useOtherWords){
            result = otherWords[rand.nextInt(otherWords.length)];
        }else{
            result = words[rand.nextInt(words.length)];
        }
        return result;
    }

    public void switchDictionary (){
        useOtherWords = !useOtherWords;
    }
}
