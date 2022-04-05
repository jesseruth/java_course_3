import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

import test.AccountTest;
import test.DaoTest;
import test.AccountManagerTest;

@RunWith(JUnitPlatform.class)
@SelectClasses({AccountTest.class, DaoTest.class, AccountManagerTest.class})
class TestSuite {}