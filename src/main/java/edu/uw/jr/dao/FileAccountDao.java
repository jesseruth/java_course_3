package edu.uw.jr.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;

import edu.uw.ext.framework.dao.AccountDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Jesse Ruth
 */
public class FileAccountDao implements AccountDao {
    static Logger logger = LoggerFactory.getLogger(FileAccountDao.class);
    private File servDir;


    @Override
    public Account getAccount(String s) {
        return null;
    }

    @Override
    public void setAccount(Account account) throws AccountException {
        servDir = new File(String.format("target/accounts/%s", account.getName()));
        if (servDir.exists() || servDir.mkdirs()) {
            logger.info("Directory is availabe {}", servDir);
        }
        try {
            File accountFile = new File(servDir, "accounts.data");
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(accountFile));
            AccountSer.write(outputStream, account);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAccount(String s) throws AccountException {

    }

    @Override
    public void reset() throws AccountException {

    }

    @Override
    public void close() throws AccountException {

    }
}
