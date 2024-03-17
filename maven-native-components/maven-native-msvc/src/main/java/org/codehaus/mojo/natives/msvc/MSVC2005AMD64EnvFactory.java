/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.codehaus.mojo.natives.msvc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.util.EnvUtil;

/**
 * Equivalent of MSVC2005's vcvarsamd64.bat
 */
public class MSVC2005AMD64EnvFactory extends AbstractMSVC2005EnvFactory {

    @Override
    protected Map<String, String> createEnvs() throws NativeBuildException {
        File vsInstallDir = new File(
                EnvUtil.getEnv(MSVS2005_INSTALL_ENV_KEY, MSVS2005_INSTALL_ENV_KEY, DEFAULT_MSVS2005_INSTALL_DIR));

        if (!vsInstallDir.isDirectory()) {
            throw new NativeBuildException(vsInstallDir.getPath() + " is not a directory.");
        }

        Map<String, String> envs = new HashMap<>();

        envs.put("VSINSTALLDIR", vsInstallDir.getPath());

        File vcInstallDir = new File(vsInstallDir.getPath() + "/VC");
        envs.put("VCINSTALLDIR", vcInstallDir.getPath());

        // TODO get winhome dir
        File frameworkDir = new File(getSystemRoot() + "/Microsoft.NET/Framework64");
        envs.put("FrameworkDir", frameworkDir.getPath());

        String frameworkVersion = "v2.0.50727";
        envs.put("FrameworkVersion", frameworkVersion);

        File frameworkSDKDir = new File(vsInstallDir.getPath() + "/SDK/v2.0 64bit");
        envs.put("FrameworkSDKDir", frameworkSDKDir.getPath());

        File devEnvDir = new File(vsInstallDir.getPath() + "/Common7/IDE");
        envs.put("DevEnvDir", devEnvDir.getPath());

        File platformSDKDir = new File(vcInstallDir.getPath() + "/PlatformSDK");

        // setup new PATH
        String currentPath = System.getProperty("java.library.path");

        String newPath = vcInstallDir.getPath() + "\\BIN\\amd64;" + platformSDKDir.getPath() + "\\BIN\\WIN64\\AMD64;"
                + platformSDKDir.getPath() + "\\BIN;" + frameworkDir.getPath() + "\\" + frameworkVersion + ";"
                + vcInstallDir.getPath() + "\\VCPackages;" + devEnvDir.getPath() + ";" + vcInstallDir.getPath()
                + "\\Common7\\Tools;" + vcInstallDir.getPath() + "\\Common7\\Tools\\Bin;" + vcInstallDir.getPath()
                + "\\SDK\\v2.0\\bin;" + currentPath;

        envs.put("PATH", newPath);

        // setup new INCLUDE PATH
        String currentIncludePath = EnvUtil.getEnv("INCLUDE");

        String newIncludePath = vcInstallDir.getPath() + "\\ATLMFC\\INCLUDE;" + vcInstallDir.getPath() + "\\INCLUDE;"
                + platformSDKDir.getPath() + "\\INCLUDE;" + vcInstallDir.getPath() + "\\SDK\\v2.0\\include;"
                + currentIncludePath;

        envs.put("INCLUDE", newIncludePath);

        //
        // setup new LIB PATH
        //
        String currentLibPath = EnvUtil.getEnv("LIB");

        String newLibPath = vcInstallDir.getPath() + "\\ATLMFC\\LIB\\AMD64;" + vcInstallDir.getPath() + "\\LIB\\AMD64;"
                + platformSDKDir.getPath() + "\\LIB\\AMD64;" + vsInstallDir.getPath() + "\\SDK\\v2.0\\LIB\\AMD64;"
                + currentLibPath;

        envs.put("LIB", newLibPath);

        //
        // setup new LIBPATH
        //

        String currentLibPathPath = EnvUtil.getEnv("LIBPATH");

        String newLibPathPath = vcInstallDir.getPath() + "\\ATLMFC\\LIB\\AMD64" + currentLibPathPath;

        envs.put("LIBPATH", newLibPathPath);

        return envs;
    }
}
