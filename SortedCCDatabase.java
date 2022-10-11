public class SortedCCDatabase implements CCDatabase {


    private static class Account{

        long accountNumber;
        String name;
        String address;
        double creditlimit;
        double balance;
    }

    int dataBaseSize = 1000;
    Account[] accounts = new Account[dataBaseSize];


    @Override
    public boolean createAccount(long accountNumber, String name, String address, double creditLimit, double balance) {
        //ask about booleans
        boolean accountCreated = false;

        //empty array / first time adding an element
        if (accounts[0] == null) {

            accounts[0] = new Account();
            accounts[0].accountNumber = accountNumber;
            accounts[0].name = name;
            accounts[0].address = address;
            accounts[0].creditlimit = creditLimit;
            accounts[0].balance = balance;

            accountCreated = true;

            System.out.println("*base case*");

        } else {

            System.out.println("\nlast element: " + findLastElement(accounts));

            int insertAtindex = binarySearch(0, findLastElement(accounts), accountNumber);

            System.out.println("correct spot found @ index: " + insertAtindex);

            if (insertAtindex != -1) {
                int j;
                for (j = findLastElement(accounts); j >= 0 && j >= insertAtindex; j--) {
                    accounts[j + 1] = accounts[j];
                }

                System.out.println("inserting at... " + (j+1));

                accounts[insertAtindex] = new Account();
                accounts[insertAtindex].accountNumber = accountNumber;
                accounts[insertAtindex].name = name;
                accounts[insertAtindex].address = address;
                accounts[insertAtindex].creditlimit = creditLimit;
                accounts[insertAtindex].balance = balance;

                accountCreated = true;
            }
        }

        return accountCreated;
    }

    @Override
    public boolean deleteAccount(long accountNumber) {
        // TODO Auto-generated method stub
        boolean accDeleted = false;

        int accIndex = binarySearchForAcc(0, findLastElement(accounts), accountNumber);

        System.out.println("DELETING ACC: ");
        if (accIndex != -1) {
            for (int i = accIndex; i <= findLastElement(accounts); i++) {
                accounts[i] = accounts[i + 1];
            }

            accDeleted = true;
        }

        return accDeleted;
    }

    @Override
    public boolean adjustCreditLimit(long accountNumber, double newLimit) {
        // TODO Auto-generated method stub
        boolean credAjusted = false;

        int accIndex = binarySearchForAcc(0, findLastElement(accounts), accountNumber);

        System.out.println("account found at index:" + accIndex);

        if(accIndex != -1) {
            accounts[accIndex].creditlimit = newLimit;
            credAjusted = true;
        }

        return credAjusted;
    }

    @Override
    public String getAccount(long accountNumber) {
        // TODO Auto-generated method stub
        String userDetails = null;

        int accIndex = binarySearchForAcc(0, findLastElement(accounts), accountNumber);

        System.out.println("account found at index:" + accIndex);

        if(accIndex != -1) {
            userDetails = accDetailsToString(accounts, accIndex);
        }

        return userDetails;
    }

    @Override
    public boolean makePurchase(long accountNumber, double price) throws Exception {
        // TODO Auto-generated method stub
        boolean purchased = false;

        int accIndex = binarySearchForAcc(0, findLastElement(accounts), accountNumber);

        System.out.println("account found at index:" + accIndex);

        if(accIndex != -1) {
            if (accounts[accIndex].creditlimit >= accounts[accIndex].balance + price) {
                accounts[accIndex].balance = accounts[accIndex].balance + price;
                purchased = true;
            }
            else {
                throw new Exception("Your balance will exceed the card limit");
            }
        }



        return purchased;
    }

    private static String accDetailsToString(Account[] accounts, int index) {

        String detail =
        Long.toString(accounts[index].accountNumber)
        + "\n"
        + accounts[index].name
        + "\n"
        + accounts[index].address
        + "\n"
        + Double.toString(accounts[index].creditlimit)
        + "\n"
        + Double.toString(accounts[index].balance);


        return detail;
    }


    private int binarySearch(int left, int right, long accountNumber) {

        int mid;

        while (left <= right) {
            mid = (left + right) / 2;

            if(accountNumber == accounts[mid].accountNumber) {
                return -1;
            }
            if(accountNumber > accounts[mid].accountNumber) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }

        return left;
    }

    private int binarySearchForAcc(int left, int right, long accountNumber) {

        int mid;

        while (left <= right) {
            mid = (left + right) / 2;

            if(accountNumber == accounts[mid].accountNumber) {
                return mid;
            }
            if(accountNumber > accounts[mid].accountNumber) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }

        return -1;
    }


    private static int findLastElement(Account[] accounts) {

        int lastElement = -1;
        boolean found = false;

        for(int i = 0; i < accounts.length && !found; i++ ) {

            if(accounts[i] == null) {
                lastElement = i-1;
                found = true;
            }
        }


        return lastElement;
    }

}
























