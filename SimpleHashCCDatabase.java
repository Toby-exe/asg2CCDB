public class SimpleHashCCDatabase implements CCDatabase {

    float dataBaseEntries = 0;
    final private int notFound = -1;
    private int dataBaseSize = 101;
    BucketOpen[] accountsTable = new BucketOpen[dataBaseSize];


    @Override
    public boolean createAccount(long accountNumber, String name, String address, double creditLimit, double balance) {
        // TODO Auto-generated method stub


        boolean accountCreated = false;
        float loadFactor = (dataBaseSize * 0.6f);



        if(dataBaseEntries > loadFactor) {
            accountsTable = reSizeTable();
        }

        if(search(accountNumber) == notFound) {

            BucketOpen openBucket = new BucketOpen(accountNumber, name, address, creditLimit, balance);


            int hValue = simpleHash(accountNumber);

            while(accountsTable[hValue] != null) {
                hValue = (hValue + 1) % dataBaseSize;
            }

            accountsTable[hValue] = openBucket;
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
        int accIndex = search(accountNumber);

        if (accIndex != notFound) {
            System.out.println("deleting account... ");

            accountsTable[accIndex].setDeleted(true);
            accDeleted = true;
            dataBaseEntries--;
        }


        System.out.println("current DB size: " + dataBaseEntries);
        return accDeleted;
    }

    @Override
    public boolean adjustCreditLimit(long accountNumber, double newLimit) {
        // TODO Auto-generated method stub
        boolean ajusted = false;
        int accIndex = search(accountNumber);

        if (accIndex != notFound) {
            accountsTable[accIndex].setCreditlimit(newLimit);
            ajusted = true;
        }
        return ajusted;
    }

    @Override
    public String getAccount(long accountNumber) {
        // TODO Auto-generated method stub

        int accIndex = search(accountNumber);

        String userDetailString = null;

        System.out.println(accIndex);
        if (accIndex != notFound) {
            userDetailString = accDetailsToString(accIndex);
        }

        return userDetailString;
    }

    @Override
    public boolean makePurchase(long accountNumber, double price) throws Exception {
        // TODO Auto-generated method stub
        boolean purchaseMade = false;

        int accIndex = search(accountNumber);

        if (accIndex != notFound) {
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

        short c4 = (short) (accountNumber % 1000);
        accountNumber = accountNumber / 1000;
        short c3 = (short) (accountNumber % 1000);
        accountNumber = accountNumber / 1000;
        short c2 = (short) (accountNumber % 1000);
        accountNumber = accountNumber / 1000;
        short c1 = (short) (accountNumber % 1000);

        hValue = (int) (17 * c1 + Math.pow(17, 2) * c2 + Math.pow(17, 3) * c3 + Math.pow(17, 4) * c4);
        hValue %= dataBaseSize;

        return hValue;
    }

    private int search(Long accountNumber) {

        int hValue = simpleHash(accountNumber);

        while(accountsTable[hValue] != null) {


            //look for an empty bucket
           if(accountsTable[hValue].getAccountNumber() == accountNumber && !accountsTable[hValue].isDeleted()) {
               return hValue;
            }



            //if this bucket is not empty check for matching account
            //else if(accountsTable[hValue] == null || accountsTable[hValue].isDeleted()) {
              //  return -1;

            //}

           hValue = (hValue + 1) % dataBaseSize;

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

       int prevDBsize = dataBaseSize;

       dataBaseSize = nextPrime((dataBaseSize * 2));

       BucketOpen[] newAccountsTable = new BucketOpen[dataBaseSize];

       System.out.println("building new table with new size.." + dataBaseSize);
       System.out.println("old DB size = " + dataBaseSize);

       int bucket = 0;
       while(bucket < prevDBsize) {
           if(accountsTable[bucket] != null && !accountsTable[bucket].isDeleted()) {

               int hValue = simpleHash(accountsTable[bucket].getAccountNumber());

               //look for an open bucket via linear probing
               while(newAccountsTable[hValue] != null) {
                   hValue = (hValue + 1) % dataBaseSize;
               }
               newAccountsTable[hValue] = accountsTable[bucket];
           }
           bucket++;
       }

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



























