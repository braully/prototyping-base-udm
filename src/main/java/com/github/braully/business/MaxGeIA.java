package com.github.braully.business;

import com.github.braully.domain.Account;
import com.github.braully.domain.AccountTransaction;
import com.github.braully.persistence.GenericDAO;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaxGeIA {

    public static final Long ID_DEFAULT_ACCOUNT_CREDITO = 4l;
    public static final Long ID_DEFAULT_ACCOUNT_DEBITO = 3l;

    @Autowired
    GenericDAO genericDAO;

    public Account classifie(AccountTransaction accountTransaction) {
        Account account = this.contaContabilAproximada(accountTransaction);
        return account;
    }

    public Account contaContabilAproximada(AccountTransaction accountTransaction) {
        Account account = null;
        if (accountTransaction != null) {
            BigDecimal valueExecuted = accountTransaction.getValueExecuted();
            List<Account> accounts = this.genericDAO.loadCollection(Account.class);
            Map<Account, Double> scores = new HashMap<>();

            if (valueExecuted != null) {
                int intValue = valueExecuted.intValue();
                //Credito
                if (intValue > 0) {
                    account = this.genericDAO.load(ID_DEFAULT_ACCOUNT_CREDITO, Account.class);
//                    for (Account acc : accounts) {
//                        Double score = matchScore(accountTransaction, account);
//                        scores.put(acc, score);
//                    }
                } //Debito
                else if (intValue < 0) {
                    account = this.genericDAO.load(ID_DEFAULT_ACCOUNT_DEBITO, Account.class);
//                    for (Account acc : accounts) {
//                        Double score = matchScore(accountTransaction, account);
//                        scores.put(acc, score);
//                    }
                }
            }
        }
        return account;
    }

    public Double matchScore(AccountTransaction accountTransaction, Account account) {
        Double ret = 0.0;
        BigDecimal valueExecuted = accountTransaction.getValueExecuted();
        if (valueExecuted != null) {
            int intValue = valueExecuted.intValue();
            //Debito
            if (intValue > 0) {

            } //Credito
            else if (intValue < 0) {

            }
        }
        return ret;
    }

    //http://weka.sourceforge.net/doc.dev/weka/classifiers/bayes/NaiveBayesMultinomial.html
    //http://weka.sourceforge.net/doc.dev/weka/classifiers/bayes/NaiveBayesMultinomialText.html
    //https://stackoverflow.com/questions/41935193/simple-text-classification-using-naive-bayes-weka-in-java
    //https://github.com/DivnaP/MovieReviewsClassifier/blob/master/src/rs/fon/is/movieClassification/classification/Trainer.java
    //https://github.com/DivnaP/MovieReviewsClassifier/blob/master/src/rs/fon/is/movieClassification/classification/Classifier.java
    public static class AccountTransactionClassifier {

    }
}
