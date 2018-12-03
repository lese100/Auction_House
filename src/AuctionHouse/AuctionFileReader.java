package AuctionHouse;

import java.io.*;

/**
 * Provides a class that can read in txt files (even when the program is run
 * from a jar) from the resource folder, and allows for line by line access
 * of each file. Does this by opening a BufferedReader on the file.
 * created: 12/01/2018 by thf
 * last modified: 12/02/2018 by thf
 * @author Liam Brady (lb)
 * @author Warren D. Craft
 * @author Tyler Fenske (thf)
 */
public class AuctionFileReader {

    //BufferedReader object allows reading over a file character by
    //character, or line by line.
    private BufferedReader file;
    private FileReader userFile;


    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * Constructor for a ConfigFileReader, requiring just the filename to be
     * read in, assuming the filename is the name of a File located in the
     * default resources folder within the src folder
     * @param fileName String name of the configuration file being used.
     */
    public AuctionFileReader(String fileName){

        openFileFromPath(fileName);

    }

    /**
     * Constructor for a AuctionFileReader, requiring an actual File object.
     * This is used in the project when files are being accessed outside
     * the default resource folder within the src folder.
     * @param file
     */
    public AuctionFileReader(File file){

        openFile(file);
    }

    /**
     * Opens a file as a stream, then passes that stream to a buffered reader
     * to allow for future string parsing.
     * @param fileName file being opened.
     */
    private void openFileFromPath(String fileName){
        ClassLoader cl = getClass().getClassLoader();
        InputStream in = cl.getResourceAsStream("resource/" +
                fileName);
        if(in != null){
            file = new BufferedReader(new InputStreamReader(in));

        }else{
            System.out.println("File not found");
        }
    }


    /**
     * Open a File object for reading, using FileReader and
     * BufferedReader objects.
     * @param file File object to be accessed/opened for reading
     */
    private void openFile(File file){
        try{
            userFile = new FileReader(file.getAbsolutePath());

            this.file = new BufferedReader(userFile);

        }catch(FileNotFoundException e){
            System.out.println("File not found!");
        }

    }

    /**
     * Returns whether or not the input stream was able to find a file with
     * the provided file name.
     * @return true if file was found, false otherwise
     */
    public boolean fileExists(){
        if(file == null){
            return false;
        }else{
            return true;
        }
    }

    /**
     * Returns the next line of text from the file as a String.
     * @return next line of text, or a null string if at end of file.
     */
    public String getNextLine(){
        String result = null;

        try{
            result = file.readLine();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }

        return result;
    }

    /**
     * Closes the bufferedReader stream. This should be done after all
     * data is read in from the file.
     */
    public void closeFileReader(){
        try{
            file.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}
