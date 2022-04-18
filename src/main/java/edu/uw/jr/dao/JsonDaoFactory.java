package edu.uw.jr.dao;

import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

/**
 * Implementation of DaoFactory that creates a JsonAccountDao instance.
 *
 * @author Jesse Ruth
 */
public class JsonDaoFactory implements DaoFactory {

    /**
     * create new JsonDaoFactory.
     */
    public JsonDaoFactory() {

    }

    /**
     * Instantiates an instance of JsonAccountDao.
     *
     * @return a new instance of JsonAccountDao
     * @throws DaoFactoryException if an error occurs.
     */
    @Override
    public AccountDao getAccountDao() throws DaoFactoryException {
        return new JsonAccountDao();
    }
}
