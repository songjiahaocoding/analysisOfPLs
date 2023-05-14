import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

public class Twenty {
    public static void main(String[] args) throws IOException {
        String f = "setting.properties";
        Properties props = new Properties();
        props.load(new java.io.FileInputStream(f));
        String jar_path = props.getProperty("jar_location");
        try {
            File file = new File(jar_path);
            URLClassLoader child = new URLClassLoader(new URL[] {file.toURI().toURL()});
            Class classToLoad = Class.forName(props.getProperty("class_name"), true, child);
            Method method = classToLoad.getMethod("getWC", String.class);
            method.invoke(classToLoad.newInstance(), args[0]);
        } catch (Throwable e){
            e.printStackTrace();
        }
    }
}


