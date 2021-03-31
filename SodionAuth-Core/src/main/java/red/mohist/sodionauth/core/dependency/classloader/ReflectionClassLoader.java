/*
 * Copyright 2021 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.mohist.sodionauth.core.dependency.classloader;

import red.mohist.sodionauth.core.utils.Helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class ReflectionClassLoader {
    private final URLClassLoader classLoader;
    private final Method addUrlMethod;

    public ReflectionClassLoader() throws IllegalStateException {
        if (isJava9OrNewer()) {
            Helper.getLogger().info("It is safe to ignore any warning printed following this message " +
                    "starting with 'WARNING: An illegal reflective access operation has occurred, Illegal reflective " +
                    "access by " + ReflectionClassLoader.class.getName() + "'. This is intended, and will not have any impact on the " +
                    "operation of SodionAuth.");
        }

        ClassLoader classLoader = ReflectionClassLoader.class.getClassLoader();
        /// if (classLoader.getClass().getName().startsWith("net.fabricmc.loader")) {
        //     classLoader = classLoader.getParent();
        // }
        if (classLoader instanceof URLClassLoader) {
            this.classLoader = (URLClassLoader) classLoader;
            try {
                addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException("ClassLoader is not a instance of URLClassLoader");
        }
    }

    private static boolean isJava9OrNewer() {
        try {
            // method was added in the Java 9 release
            Runtime.class.getMethod("version");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }
    public void addJarToClasspath(Path file) {
        try {
            Helper.getLogger().info(file.toUri().toURL().toString());
            this.addUrlMethod.invoke(this.classLoader, file.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
