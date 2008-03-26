
/**
 * 
 * @author dtran
 *
 */
public class HelloWorld
{
    public native String sayHello();

    static
    {
        System.loadLibrary("hello");
    }

    public static void main( String[] args )
    {
        HelloWorld app = new HelloWorld();
        System.out.println( app.sayHello() );
    }
}
