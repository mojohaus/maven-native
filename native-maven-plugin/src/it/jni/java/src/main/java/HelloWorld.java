

public class HelloWorld
{
    public native void sayHello();

    static
    {
        System.loadLibrary("NativeSideImpl");
    }

    public static void main( String[] args )
    {
        HelloWorld app = new HelloWorld();
        app.sayHello();
    }
}
