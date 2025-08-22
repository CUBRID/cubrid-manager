package com.cubrid.cubridmanager.core.common.loader;

import com.cubrid.cubridmanager.core.CubridManagerCorePlugin;
import java.io.IOException;
import java.net.URL;
import junit.framework.TestCase;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

public class CubridClassLoaderPoolTest extends TestCase {

    public void testClassLoader() {
        //		try {
        //			String filePath = this.getFilePathInPlugin("/lib/JDBC-8.3.1.0173-cubrid.jar");
        //			CubridClassLoaderPool.getClassLoader(filePath);
        //			ClassLoader loader = CubridClassLoaderPool.getClassLoader(filePath);
        //			assertTrue(loader != null);
        //		} catch (FileNotFoundException e) {
        //		} catch (MalformedURLException e) {
        //		}
        //		Driver driver = CubridClassLoaderPool.getCubridDriver("");
        //		assertTrue(driver == null);
        //		driver = CubridClassLoaderPool.getCubridDriver("jdbcversion");
        //		assertTrue(driver == null);
        //		driver = CubridClassLoaderPool.getCubridDriver(driver);
        //		assertTrue(driver != null);
        //		driver = CubridClassLoaderPool.getCubridDriver((Driver)null);
        //		assertTrue(driver != null);
        //		driver = CubridClassLoaderPool.getCubridDriver((String)null);
        //		assertTrue(driver == null);
        //
        //		try {
        //			CubridClassLoaderPool.getClassLoader("");
        //			assertTrue(false);
        //		} catch (FileNotFoundException e) {
        //			assertTrue(true);
        //		} catch (MalformedURLException e) {
        //			assertTrue(true);
        //		}
    }

    public String getFilePathInPlugin(String filepath) {
        URL fileUrl = null;
        if (CubridManagerCorePlugin.getDefault() == null) {
            fileUrl = this.getClass().getResource(filepath);
        } else {
            Bundle bundle = CubridManagerCorePlugin.getDefault().getBundle();
            URL url = bundle.getResource(filepath);
            try {
                fileUrl = FileLocator.toFileURL(url);
            } catch (IOException e) {
                return null;
            }
        }
        return fileUrl == null ? null : fileUrl.getPath();
    }
}
