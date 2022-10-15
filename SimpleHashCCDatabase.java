public class SimpleHashCCDatabase implements CCDatabase {

    int called = 0;
    float dataBaseEntries = 0;
    final private int notFound = -1;
    private int dataBaseSize = 101;
    BucketOpen[] accountsTable = new BucketOpen[dataBaseSize];


    @Override
    public boolean createAccount(long accountNumber, String name, String address, double creditLimit, double balance) {
        // TODO Auto-generated method stub

        float loadFactor = (dataBaseSize * 0.6f);
        boolean accountCreated = false;

        if(dataBaseEntries + 1 > loadFactor) {

            accountsTable = reSizeTable();
        }

        int insertAtIndex = search(accountNumber, accountsTable);

        if(accountsTable[insertAtIndex] == null || accountsTable[insertAtIndex].isDeleted()) {

            BucketOpen openBucket = new BucketOpen(accountNumber, name, address, creditLimit, balance);

            accountsTable[insertAtIndex] = openBucket;
            accountCreated = true;
            dataBaseEntries++;

            System.out.println("account " + (dataBaseEntries) + " created");

        }


        return accountCreated;
    }

    @Override
    public boolean deleteAccount(long accountNumber) {
        // TODO Auto-generated method stub
        boolean accDeleted = false;
        int accIndex = search(accountNumber, accountsTable);

        System.out.println("*index from search* = " + accIndex);
        if (accountsTable[accIndex] != null && !accountsTable[accIndex].isDeleted()) {
            System.out.println("deleting account.................... ");

            accountsTable[accIndex].setDeleted(true);
            accDeleted = true;
            dataBaseEntries--;
        }



        System.out.println("current DB size: " + dataBaseEntries + "\n");
        return accDeleted;
    }

    @Override
    public boolean adjustCreditLimit(long accountNumber, double newLimit) {
        // TODO Auto-generated method stub
        boolean ajusted = false;
        int accIndex = search(accountNumber, accountsTable);

        if (accountsTable[accIndex] != null && !accountsTable[accIndex].isDeleted()) {
            accountsTable[accIndex].setCreditlimit(newLimit);
            ajusted = true;
        }
        return ajusted;
    }

    @Override
    public String getAccount(long accountNumber) {
        // TODO Auto-generated method stub

        int accIndex = search(accountNumber, accountsTable);

        String userDetailString = null;

        if (accountsTable[accIndex] != null && !accountsTable[accIndex].isDeleted()) {
            userDetailString = accDetailsToString(accIndex);
        }

        return userDetailString;
    }

    @Override
    public boolean makePurchase(long accountNumber, double price) throws Exception {
        // TODO Auto-generated method stub
        boolean purchaseMade = false;

        int accIndex = search(accountNumber, accountsTable);

        if (accountsTable[accIndex] != null && !accountsTable[accIndex].isDeleted()) {
            Double currentBalance = accountsTable[accIndex].getBalance();

            if(accountsTable[accIndex].getCreditlimit() >= (currentBalance + price)) {
                accountsTable[accIndex].setBalance(price + currentBalance);

                purchaseMade = true;
            }
            else {
                throw new Exception("Your balance will exceed the card limit");
            }
        }
        return purchaseMade;
    }

    private int simpleHash(long accountNumber) {

        int hValue = -1;

        //THIS IS A PROBLEM
        short c4 = (short) (accountNumber % 10000);
        accountNumber = accountNumber / 10000;
        short c3 = (short) (accountNumber % 10000);
        accountNumber = accountNumber / 10000;
        short c2 = (short) (accountNumber % 10000);
        accountNumber = accountNumber / 10000;
        short c1 = (short) (accountNumber % 10000);

        hValue = (int) (17 * c1 + Math.pow(17, 2) * c2 + Math.pow(17, 3) * c3 + Math.pow(17, 4) * c4);
        hValue %= dataBaseSize;

        return hValue;
    }

    private int search(Long accountNumber, BucketOpen[] hashTable) {

        int hValue = simpleHash(accountNumber);

        int counter = 0;
        int index = 0;

        //System.out.println("searching using DB size: " + dataBaseSize);
        while(counter < dataBaseSize) {

            index = (hValue + counter) % dataBaseSize;

            //look for matching account
            if(hashTable[index] == null || hashTable[index].isDeleted()) {
                System.out.println("NOT FOUND");
                return index;

            }
            if(hashTable[index].getAccountNumber() == accountNumber ) {
                System.out.println("FOUND");
                return index;

            }


            counter++;
        }

        return notFound;
    }


    private String accDetailsToString(int index) {

        String details =
        Long.toString(accountsTable[index].getAccountNumber())
        + "\n"
        + accountsTable[index].getName()
        + "\n"
        + accountsTable[index].getAddress()
        + "\n"
        + Double.toString(accountsTable[index].getCreditlimit())
        + "\n"
        + Double.toString(accountsTable[index].getBalance());


        return details;
    }

   private BucketOpen[] reSizeTable() {

       System.out.println("accounts recived: " + dataBaseEntries);

       int localentries = 0;
       int prevDBsize = dataBaseSize;

       dataBaseSize = nextPrime((dataBaseSize * 2));

       BucketOpen[] newAccountsTable = new BucketOpen[dataBaseSize];

       System.out.println("building new table with new size.." + dataBaseSize);
       System.out.println("old DB size = " + prevDBsize);

       int bucket = 0;
       while(bucket < prevDBsize) {
           if(accountsTable[bucket] != null && !accountsTable[bucket].isDeleted()) {

               //look for an open bucket via linear probing in the new hashTable
               int index = search(accountsTable[bucket].getAccountNumber(), newAccountsTable);

               if (newAccountsTable[index] == null || newAccountsTable[index].isDeleted()) {
                   newAccountsTable[index] = accountsTable[bucket];
                   localentries++;
               }
           }
           bucket++;
       }

       System.out.println("accounts moved: " + localentries);
       return newAccountsTable;
   }

   private static boolean isPrime(int num) {
        // TODO Auto-generated method stub
        if(num <= 1){
            return false;
        }
        for(int i = 2; i <= num/2; i++)
        {
           if((num % i) == 0)
               return  false;
        }
        return true;

   }


   private static int nextPrime(int N)
   {

       // Base case
       if (N <= 1)
           return 2;

       int prime = N;
       boolean found = false;

       // Loop continuously until isPrime returns
       // true for a number greater than n
       while (!found)
       {
           prime++;

           if (isPrime(prime))
               found = true;
       }

       return prime;
   }


}
























