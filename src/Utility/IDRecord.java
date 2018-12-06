package Utility;

import java.io.Serializable;

/**
 * Provides a structure for various pieces of identifying information,
 * intended for keeping track of, and exchanging information about,
 * agents, auction houses, and banks. An IDRecord will generally hold:
 * (1) an enum indicating the type of entity;
 * (2) a String for an identifying name
 * (3) an int for storing a numerical ID
 * (4) a String for the entity's hostname (machine location)
 * (5) an int for storing a port number (for accessing/contacting
 *     the entity)
 * created: 11/24/18 by wdc
 * last modified: 12/01/18 by wdc (adding initialBalance setter)
 * previously modified: 11/27/18 by wdc (adding initialBalance field)
 * previously modified: 11/24/18 by wdc (creation)
 * @author Liam Brady (lb)
 * @author Warren D. Craft (wdc)
 * @author Tyler Fenske (thf)
 */
public class IDRecord implements Serializable {

    // enum to identify the type of IDRecord
    public enum RecordType {AGENT, AUCTION_HOUSE, BANK};
    private RecordType recordType;
    private String name;
    private double initialBalance;
    private int numericalID;
    private String hostname;
    private int portNumber;

    // ****************************** //
    //   Constructor(s)               //
    // ****************************** //

    /**
     * The public constructor for an IDRecord, holding identifying info
     * about an agent, auction house, or bank. An IDRecord for a
     * entity will typically be initialized by the entity itself,
     * then (perhaps) augmented by other entities.
     */
    public IDRecord (RecordType recordType, String name,
                     double initialBalance,
                     String hostname, int portNumber) {

        this.recordType = recordType;
        this.name = name;
        this.initialBalance = initialBalance;
        this.hostname = hostname;
        this.portNumber = portNumber;

    }

    // ****************************** //
    //   Getter(s) & Setter(s)        //
    // ****************************** //

    /**
     * Gets the type of IDRecord.
     * @return an enum AGENT, AUCTION_HOUSE, or BANK
     */
    public RecordType getRecordType() {
        return recordType;
    }

    /**
     * Gets the name associated with the IDRecord
     * @return String representing the (usually self-chosen)
     *         name of the entity assoc'd with the IDRecord
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the initial balance associated with the IDRecord (typically
     * used for establishing an agent's account with a Bank).
     * @return double representing the entity's initial balance
     */
    public double getInitialBalance() {
        return initialBalance;
    }

    /**
     * Gets the the numerical ID associated with the IDRecord.
     * This might, e.g., be an account # or some other useful integer
     * identifier.
     * @return int identification number
     */
    public int getNumericalID() {
        return numericalID;
    }

    /**
     * Gets the hostname of the entity associated with the IDRecord
     * -- i.e. the machine where this entity resides or machine
     * where the entity can be accessed.
     * @return String representing the machine id where the entity
     *         resides
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Gets the port number for accessing or communicating with the
     * entity associated with the IDRecord.
     * @return int port number
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * Sets the initialBalance field, which is then used by a Bank in setting
     * up a BankAccount when responding to a request from a client to open an
     * account.
     * @param initialBalance double initial account balance
     */
    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    /**
     * Sets the numerical ID for the entity associated with the
     * IDRecord -- used, e.g., by a Bank to set the entity's bank
     * account number.
     * @param numericalID an int identification number
     */
    public void setNumericalID(int numericalID) {
        this.numericalID = numericalID;
    }

    /**
     * Converting this object to a String shows all bookkeeping fields.
     * @return String representation of this object
     */
    @Override
    public String toString(){
        return recordType + " IDRecord:\n" +
                "Name: " + name + "\n" +
                "NumericalID: " + numericalID + "\n" +
                "HostName: " + hostname + "\n" +
                "PortNumber: " + portNumber + "\n" +
                "InitialBalance: " + initialBalance;
    }
}
