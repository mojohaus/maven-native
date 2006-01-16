

public class HelloWorld
{
    public native void sayHello();

    static
    {
        System.loadLibrary("hello");
    }

    public static void main( String[] args )
    {
        HelloWorld app = new HelloWorld();
        app.sayHello();
    }
}
