package edu.uw.jr.dao;

import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of DaoFactory that creates a FileAccountDao instance.
 */
public class FileDaoFactory implements DaoFactory {
    final static Logger logger = LoggerFactory.getLogger(FileDaoFactory.class);

    /**
     * Instantiates an instance of FileAccountDao.
     *
     * @return a new instance of FileAccountDao
     * @throws DaoFactoryException if instantiation fails
     */
    @Override
    public AccountDao getAccountDao() throws DaoFactoryException {
        try {
            return new FileAccountDao();
        } catch (Exception e) {
            final String message = "Unable to create new Account Dao";
            logger.error(message, e);
            throw new DaoFactoryException(message);
        }

    }
}
