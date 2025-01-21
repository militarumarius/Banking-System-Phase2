# Project Stage 2 - J. POO Morgan Chase & Co.
## Name : Militaru Ionut-Marius 323CAb

### Project Description

* The main goal of this phase of the project was to implement new functionalities for the banking system, 
enabling the system to function more effectively and offer a wider range of products, 
such as business accounts and cashback.

### New Functionalities in Phase 2
* In this phase, I extended the banking application with new functionalities to enhance its capabilities:
    - **Business Accounts**: The system now supports business accounts, allowing
  users to manage business-related banking needs.
  - **User Plans and Cashback**: Each user can now have a specific plan associated with their account, 
  which determines the commission they need to pay. The cashback, on the other hand, is determined based on 
  the type of commerciant where the payment is made.

* The codebase has been restructured to support the new functionality

## Implemented Classes on the Phase I of the Project 
* In the actionHandler package, I implemented a class called ActionHandler
  to manage the commands within the banking system. Additionally, I implemented two classes
  to print errors or commands to the output and an enum where I defined the different types
  of errors that can occur.

* In the bank package, I implemented various classes for accounts, cards, users, and the bank database.
  The Card and Account classes are abstract to allow for extension based on the type of card or account
  that will be implemented in the future.

* In the commands package, I implemented a Command interface, followed by various types of commands
  that can occur within the banking system. This design allows for new commands to be added to the banking
  system with ease, as they are independent of the other commands.

* In the transaction package, I implemented three classes: Transaction, TransactionBuilder, and Commerciant,
  along with an enum called TransactionDescription, which contains all types of description that can occur during a
  transaction. I implemented a TransactionBuilder to avoid creating separate classes for every type of
  transaction, where only the constructor would differ.

## Implemented Classes on the Phase II of the Project

* In the actionHandler package, during the second phase of the project, I added a class called CommerciantOutput. 
This class is necessary for displaying business reports. Additionally, the ActionHandler class 
continues to manage the commands within the banking system, and an enum defines 
the various types of errors that can occur.

* During the second phase of the project, I implemented the Business Account with all its functionalities. 
Additionally, I introduced a new plans package, which contains the different types of plans offered by the bank. 
This package also includes a PlanFactory class to facilitate the creation of specific plan types more efficiently.
Depending on the plan type, a specific commission is applied, ensuring each plan has its own fee structure.

* In the Business Account , there is a list of users who manage the account based on their role within the business. 
The users' access and permissions are determined by their role, which dictates the level of control they have over 
the account. The roles and permissions are as follows:
    - Owner: Has full rights over the account, including the ability to set transaction limits for each type 
  of transaction.
    - Manager: Has the right to perform any type of transaction: adding funds, making payments, making transfers, 
        and creating or deleting any card associated with the account.
    - Employee: Can make payments with any card.

* In the commands package, I expanded the system by adding new commands, such as those for withdrawing from savings, 
withdrawing cash, changing transaction limits, and specific commands for managing the Business Account. 
This improves the flexibility of the system, allowing it to handle a wider range of actions while making 
it easier to implement future modifications.

* In the transaction package, I added a class for split transactions, as it was necessary to keep track of the 
transactions that required splitting. The structure of this command was modified at this stage. 
Additionally, I updated the Commerciant class to align with the new commands. Furthermore, I introduced a new 
cashback package, which implements two types of cashback: number of transactions and spending threshold.
For the Business Report, I created special transactions, which I have stored within the Business 
Account to ensure accurate tracking and reporting.


## DESIGN PATTERNS
#### In the project, I implemented four design patterns
* I implemented a **Factory pattern** for account creation to handle the type of account that should be implemented.
I also implemented a Factory pattern for the plan creation to handle the plan type of each user.
* In the transactions, I implemented a **Builder pattern** to handle the different types of transactions
  that can be carried out within the banking system.
* I implemented a **Command pattern** to manage the commands in the banking system, making it
  easier to extend the system by adding new commands.
* I implemented the **Strategy Pattern** in two areas: first, in the cashback implementation, where each 
cashback has its own calculation method, and second, in the plan implementation, where each plan has 
its own set of commissions.

  
### Summary
The second phase of the project focused on extending the banking application with advanced 
features like business accounts, user plans, and cashback functionality. Additionally, the integration 
of the Strategy pattern ensures that these new features are modular and easily maintainable, 
while the code restructuring enhances the system’s scalability and robustness.