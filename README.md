# Auction House #

The Auction House project is a simulation (and graphical representation) of
a network-based interactive triplet of programs consisting of a Bank, one or
more Auction Houses, and one or more Agents or users who bid on items
available for persual from an Auction House. Agents and Auction Houses open
accounts with the Bank, which serves as a conduit for monetary transactions
between an Agent and an Auction House.

## Getting Started ##

The project can be run directly from the latest trio of .jar file(s):


`Bank.jar`<br>
`AuctionHouse.jar`<br>
`Agent.jar`

which can be downloaded and opened directly on a single computer or on
multiple computers (for example, with Bank, AuctionHouse, and Agent all
running all separate computers simultaneously, but on the same network).
The project could also be run from inside IntelliJ IDEA, with the main
access point being the Main class in the Bank and AuctionHouse packages and
the Agent class in the Agent package.

Because of the connectivity dependence among the three components, it works
best to start the Bank first, then the other two components in either order.

## Running the Program: Participating in an Auction ##

Generally, the most straightforward startup process involves the following
sequence of steps (see more detailed explanation further below):

<blockquote>
(1) Start the Bank;<br>
(2) Start one or more Auction Houses;<br>
(3) Start one or more Agents.<br>
</blockquote>

**(1)** Starting the Bank. The user is presented with a small GUI asking for
a name (optional) and the hostname and port number from which you want to
run the bank. The initial default values are set to work when running all
three components on a single machine. For multiple machines on a single
network, set the hostname to the network-designated name of the computer
(in a computer lab, the machines are often labeled with their network id)
and the desired port number. Both the hostname and port number will be needed
when completing the start-up process for an Auction House and an Agent.

Once you supply the appropriate hostname and port number and click on the
`Create Bank` button, a new GUI appears that lets you monitor the active
account information for the Bank. Along the left-hand side you will see the
designated name, hostname, and port number being used, and in the center
appears a constantly updating table of current bank accounts. The bank
account information includes the unique Bank Account number, the account
type (AGENT vs. AUCTION_HOUSE), the current overall balance, the portion of
the overall balance that has been frozen or held (for a bidding process),
the "available" non-frozen funds remaining from the overall balance, and
any name associated with the account.

As clients connect and disconnect from the Bank, you will see accounts
appear and disappear from the list. As Agents participate in the bidding
process, you will see their balances fluctuate and eventually you will
see money being transferred from Agent accounts to Auction House accounts.

**(2)** Starting an Auction House. The user is presented with a GUI …

**(3)** Starting an Agent. The user is presented with a GUI …

The GUIs for the Bank and an Auction House allow monitoring of activity, but
little else …

**(4)** Agent Participating in an Auction. 

## Extras ##

The project includes … 

## Built With ##

The program was written in Java and JavaFX, using IntelliJ IDEA.

## Authors and Contributors ##

Authors:<br>
Tyler Fenske (thf)<br>
Liam Brady (lb)<br>
Warren D. Craft (wdc)<br>
(along with hints and help, of course, from the
instructor and course TAs). Also see acknowledgments below.

## Acknowledgments ##

THF largely developed …<br>
LB largely developed …<br>
WDC largely developed …<br>
Initial development used Knock!Knock! code supplied by Brooke Chenoweth
which was borrowed and modified from
https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html

## Some Development Notes ##

<b>Thurs, Fri, Sat 11/15 -- 11/17</b>
1st intensive meetings of our trio, reviewing project specifications and
considering plausible designs for the main components of Bank, Agent, and
Auction House. Activities included review and implementation of the
Knock!Knock! code provided by lab instructor Brooke Chenoweth, discussion
of possible Message class structure, and discussion of various aspects of the
design suggested by the class instructor Prof. Roman.

## Design & Design Issues ##

The general design is shown in the instance diagram included in the /doc
directory.

## Known Issues ##

(1)

(2) 

## Further Development ##

The program functions and conforms reasonable well to the general
design specifications. Some immediate next steps in development would
include:

(1) [topic] …

(2) [topic] …

(3) [topic] …



