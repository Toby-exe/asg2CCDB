public class RobinHoodCCDataBase implements CCDatabase {

    int testCount = 0;
    float dataBaseEntries = 0;
    final private int notFound = -1;
    private int dataBaseSize = 101;
    BucketOpen[] accountsTable = new BucketOpen[dataBaseSize];


    @Override
    public boolean createAccount(long accountNumber, String name, String address, double creditLimit, double balance) {
        // TODO Auto-generated method stub

        float loadFactor = (dataBaseSize * 0.65f);
        boolean accountCreated = false;

        if(dataBaseEntries + 1 > loadFactor) {

             reSizeTable();
        }

        BucketOpen openBucket = new BucketOpen(accountNumber, name, address, creditLimit, balance);

        accountCreated = insert(accountsTable, openBucket);

        return accountCreated;
    }

    private void remove (int index) {

        accountsTable[index] = null;

        int nextIndex = index + 1;

        while(accountsTable[nextIndex] != null && accountsTable[nextIndex].getPSL() > 0) {

            accountsTable[index] = accountsTable[nextIndex];

            accountsTable[nextIndex] = null;

            accountsTable[index].setPSL(accountsTable[index].getPSL() - 1);

            index = (index + 1) % dataBaseSize;
            nextIndex = (index + 1) % dataBaseSize;
        }
    }

    @Override
    public boolean deleteAccount(long accountNumber) {
        // TODO Auto-generated method stub
        int index = find(accountNumber);

        if(index == notFound) {
            return false;
        }
        else  {
            remove(index);
            dataBaseEntries--;
            return true;

        }
    }


    @Override
    public boolean adjustCreditLimit(long accountNumber, double newLimit) {
        // TODO Auto-generated method stub
        boolean ajusted = false;
        int accIndex = find(accountNumber);

        if (accIndex != notFound) {
            accountsTable[accIndex].setCreditlimit(newLimit);
            ajusted = true;
        }
        return ajusted;
    }

    @Override
    public String getAccount(long accountNumber) {
        // TODO Auto-generated method stub

        int accIndex = find(accountNumber);

        String userDetailString = null;

        if (accIndex != notFound) {
            userDetailString = accDetailsToString(accIndex);
        }

        return userDetailString;
    }

    @Override
    public boolean makePurchase(long accountNumber, double price) throws Exception {
        // TODO Auto-generated method stub
        boolean purchaseMade = false;

        int accIndex = find(accountNumber);

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

    private int find(Long accountNumber) {

        int index = simpleHash(accountNumber);

        while(accountsTable[index] != null) {


           if (accountsTable[index].getAccountNumber() == accountNumber && !accountsTable[index].isDeleted()) {
                  return index;
           }

           index = (index + 1) % dataBaseSize;


        }

        return notFound;
    }

    private boolean insert(BucketOpen[] hashTable, BucketOpen openBucket) {

        //System.out.println("inserting account: " + openBucket.getAccountNumber());

        openBucket.setPSL(0);

        BucketOpen tmpBucket = null;
        int index = simpleHash(openBucket.getAccountNumber());

        while(hashTable[index] != null ) {

            if(hashTable[index].getAccountNumber() == openBucket.getAccountNumber()) {
                return false;
            }

            if(hashTable[index].getPSL() < openBucket.getPSL()) {


                tmpBucket = hashTable[index];
                hashTable[index] = openBucket;
                openBucket = tmpBucket;
            }
            openBucket.setPSL(openBucket.getPSL() + 1);


            index = (index + 1) % dataBaseSize;

        }

        hashTable[index] = openBucket;

        dataBaseEntries++;


        return true;
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

   private void reSizeTable() {

       int prevDBsize = dataBaseSize;

       dataBaseSize = nextPrime((dataBaseSize * 2));

       BucketOpen[] newAccountsTable = new BucketOpen[dataBaseSize];

       int bucket = 0;
       while(bucket < prevDBsize) {
           if(accountsTable[bucket] != null && !accountsTable[bucket].isDeleted()) {

               //look for an open bucket via linear probing in the new hashTable
               insert(newAccountsTable, accountsTable[bucket]);

           }
           bucket++;
       }

       accountsTable = newAccountsTable;

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

