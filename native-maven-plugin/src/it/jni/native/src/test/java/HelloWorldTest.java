
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest
{
    @Test
    public void testNativeHelloWorld()
        throws Exception
    {
        HelloWorld app = new HelloWorld();

        assertEquals( "Hello Native World!", app.sayHello() );
    }
}
