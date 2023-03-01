package banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Bank implementation.
 */
public class Bank implements BankInterface {
    private LinkedHashMap<Long, Account> accounts;
    private long lastId;

    public Bank() {
        accounts = new LinkedHashMap<>();
        lastId = 0;
    }

    private Account getAccount(Long accountNumber) {
        if(this.accounts.containsKey(accountNumber))
            return this.accounts.get(accountNumber);
        return null;
    }

    private long nextId(){
        return ++lastId;
    }

    public Long openCommercialAccount(Company company, int pin, double startingDeposit) {       
        long id = this.nextId();
        accounts.put(id, new CommercialAccount(company, id, pin, startingDeposit));
        return id;
    }

    public Long openConsumerAccount(Person person, int pin, double startingDeposit) {
        long id = this.nextId();
        accounts.put(id,new ConsumerAccount(person, id, pin, startingDeposit));
        return id;
    }

    public double getBalance(Long accountNumber) {
        if(accounts.get(accountNumber) == null)
            return -1;
        return accounts.get(accountNumber).getBalance();
    }

    public void credit(Long accountNumber, double amount) {
        if(accounts.get(accountNumber) != null)
            accounts.get(accountNumber).creditAccount(amount);
    }

    public boolean debit(Long accountNumber, double amount) {
        if(accounts.get(accountNumber) == null)
            return false;

        return accounts.get(accountNumber).debitAccount(amount);
    }

    public boolean authenticateUser(Long accountNumber, int pin) {
        if(accounts.get(accountNumber) == null)
            return false;
        if(!accounts.get(accountNumber).validatePin(pin))
            return false;
        return true;
    }
    
    public void addAuthorizedUser(Long accountNumber, Person authorizedPerson) {
        if(accounts.get(accountNumber) != null){
            if(accounts.get(accountNumber) instanceof CommercialAccount account){
                account.addAuthorizedUser(authorizedPerson);
            }
        }
    }

    public boolean checkAuthorizedUser(Long accountNumber, Person authorizedPerson){
        if(accounts.get(accountNumber) == null && authorizedPerson == null)
            return false;
        if(!(accounts.get(accountNumber) instanceof CommercialAccount))
            return false;
        if(!((CommercialAccount) accounts.get(accountNumber)).isAuthorizedUser(authorizedPerson))
            return false;
        
        return true;
    }

    public Map<String, Double> getAverageBalanceReport() {
        Map<String,List<Double>> average = new HashMap<>();
        
        for(Map.Entry<Long,Account> entry : accounts.entrySet()){
            String className = entry.getValue().getClass().getSimpleName();
            if(!average.containsKey(className))
                average.put(className, new ArrayList<>());
            
            average.get(className).add(entry.getValue().getBalance());
        }

        Map<String, Double> balanceReport = new HashMap<>();
        for(Map.Entry<String,List<Double>> entry : average.entrySet()){
            Double avrg = entry.getValue().stream()
                            .mapToDouble( v -> v.doubleValue())
                            .sum();
            avrg = avrg / entry.getValue().size();

            balanceReport.put(entry.getKey(), avrg);
        }


        return balanceReport;
    }
}
