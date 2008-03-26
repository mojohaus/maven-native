
import junit.framework.*;

public class HelloWorldTest
    extends TestCase {

    public HelloWorldTest(String name)
    {
      super(name);
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    public void testNativeHelloWorld()
        throws Exception
    {
        HelloWorld app = new HelloWorld();
        
        this.assertEquals( "Hello Native World!", app.sayHello() );
    }


}

