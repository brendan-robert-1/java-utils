/**
 *
 */
package com.brobert;

import com.brobert.exceptions.NoEnvironmentException;
import com.brobert.exceptions.UnknownOperatingSystemException;
import org.apache.commons.lang.Validate;

import java.io.InputStream;
import java.util.Properties;

/**
 *
 * TEST from windows cl in test-project2
 * EnvironmentProperties is a wrapper on the standard {@link Properties} class.
 * This class uses file naming conventions to choose the correct property file
 * at runtime based on OS and Environment.
 * <p>
 * The Environment is queried from the VM Argument '-Denv.name' which allows
 * users to specify environments at the JVM level. Web servers may be configured
 * to be a DEV or a PROD instance, for example, so that all applications that
 * use EnvironmentProperties will abide by the environment.
 * <p>
 * The OS is also queried to distinguish between Windows and Linux specific
 * properties files for example. This allows us to have 2 seperate properties
 * files such as WINDOWS_DEV_SUFFIX.properties and UNIX_DEV_SUFFIX.properties
 * and the correct property file will be chosen at runtime.
 * <p>
 * This process is designed to be as straightforward and simple as possible and
 * act as a barebones abstraction that takes environments into consideration
 * when choosing a properties file at runtime. The only requirement for clients
 * is to follow 2 naming conventions to ensure this works. The first is to have
 * the properties available on the classpath at runtime with the following
 * format: OS_ENVNAME_SUFFIX.properties. Where OS is the uppercase toString of
 * the Enum OperatingSystem also included in this project. For reference they
 * are: WINDOWS, UNIX, SOLARIS, and MAC. ENVNAME is the exact value of the JVM
 * argument -Denv.name=VALUE. If this is not present a NoEnvironmentException
 * will be thrown. SUFFIX is the unique identifier that you will use in code
 * when getting the Properties Object.
 *
 * @author brobert
 */
public class EnvironmentProperties {
    private Properties properties;

    public enum OperatingSystem {

        WINDOWS, UNIX, MAC, SOLARIS;
    }



    public static void main(String[] args) {
        EnvironmentProperties prop = new EnvironmentProperties("DBCONNECTOR");
        System.out.println(prop.getProperty("prop"));
    }



    public String getProperty(String key) {
        return properties.getProperty(key);
    }



    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }



    public EnvironmentProperties(String fileSuffix) {
        Validate.notEmpty(fileSuffix);
        properties = getEnvironmentProperties(fileSuffix);
    }



    public Properties getProperties() {
        return properties;
    }



    public String getTargetForLogs(String fileSuffix) {
        return "Targeting property file: [" + getTarget(fileSuffix) + "] for file suffix: [" + fileSuffix + "]";
    }



    private String getTarget(String fileSuffix) {
        OperatingSystem os = getOS();
        String environment = getEnvironment();
        return os.toString() + "_" + environment + "_" + fileSuffix + ".properties";
    }



    /**
     * @param fileSuffix The suffix string that you have included in the name of your
     *                   .properties files.
     * @return
     */
    private Properties getEnvironmentProperties(String fileSuffix) {
        Properties prop = new Properties();
        String fileName = getTarget(fileSuffix);
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
            prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }



    /**
     * @return
     */
    private String getEnvironment() {
        String environment = System.getProperty("env.name");
        if (environment == null) {
            throw new NoEnvironmentException("Environment VM argument has not been set. Please add a -Denv.name=YOURENVIRONMENT flag to the VM");
        }
        return environment;
    }



    private OperatingSystem getOS() {
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("win") >= 0) {
            return OperatingSystem.WINDOWS;
        } else if (OS.indexOf("mac") >= 0) {
            return OperatingSystem.MAC;
        } else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
            return OperatingSystem.UNIX;
        } else if (OS.indexOf("sunos") >= 0) {
            return OperatingSystem.SOLARIS;
        } else {
            throw new UnknownOperatingSystemException("Your operating system can not be determined.");
        }
    }
}
