import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

import test.AccountTest;
import test.BrokerTest;
import test.DaoTest;
import test.AccountManagerTest;

/**
 * Removed AccountTest.class
 */
@RunWith(JUnitPlatform.class)
@SelectClasses({DaoTest.class, AccountManagerTest.class, BrokerTest.class})
class TestSuite {
}