import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

import test.*;

/**
 * Removed AccountTest.class
 */
@RunWith(JUnitPlatform.class)
@SelectClasses({DaoTest.class, AccountManagerTest.class, BrokerTest.class, PrivateMessageCodecTest.class})
class TestSuite {
}