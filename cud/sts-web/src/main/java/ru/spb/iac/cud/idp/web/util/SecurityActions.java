package ru.spb.iac.cud.idp.web.util;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

 public class SecurityActions {

	static Class loadClass(final Class<?> theClass,
			final String fullQualifiedName) {
		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			return AccessController
					.doPrivileged(new PrivilegedAction<Class<?>>() {
						public Class run() {
							ClassLoader classLoader = theClass.getClassLoader();

							Class<?> clazz = loadClass(classLoader,
									fullQualifiedName);
							if (clazz == null) {
								classLoader = Thread.currentThread()
										.getContextClassLoader();
								clazz = loadClass(classLoader,
										fullQualifiedName);
							}
							return clazz;
						}
					});
		} else {
			ClassLoader classLoader = theClass.getClassLoader();

			Class<?> clazz = loadClass(classLoader, fullQualifiedName);
			if (clazz == null) {
				classLoader = Thread.currentThread().getContextClassLoader();
				clazz = loadClass(classLoader, fullQualifiedName);
			}
			return clazz;
		}
	}

	static Class loadClass(final ClassLoader classLoader,
			final String fullQualifiedName) {
		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			return AccessController
					.doPrivileged(new PrivilegedAction<Class<?>>() {
						public Class run() {
							try {
								return classLoader.loadClass(fullQualifiedName);
							} catch (ClassNotFoundException e) {
							}
							return null;
						}
					});
		} else {
			try {
				return classLoader.loadClass(fullQualifiedName);
			} catch (ClassNotFoundException e) {
			}
			return null;
		}
	}

	static URL loadResource(final Class<?> clazz, final String resourceName) {
		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			return AccessController.doPrivileged(new PrivilegedAction<URL>() {
				public URL run() {
					URL url = null;
					ClassLoader clazzLoader = clazz.getClassLoader();
					url = clazzLoader.getResource(resourceName);

					if (url == null) {
						clazzLoader = Thread.currentThread()
								.getContextClassLoader();
						url = clazzLoader.getResource(resourceName);
					}

					return url;
				}
			});
		} else {
			URL url = null;
			ClassLoader clazzLoader = clazz.getClassLoader();
			url = clazzLoader.getResource(resourceName);

			if (url == null) {
				clazzLoader = Thread.currentThread().getContextClassLoader();
				url = clazzLoader.getResource(resourceName);
			}

			return url;
		}
	}

	static void setSystemProperty(final String key, final String value) {
		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					System.setProperty(key, value);
					return null;
				}
			});
		} else {
			System.setProperty(key, value);
		}
	}

	static String getSystemProperty(final String key, final String defaultValue) {
		
			return System.getProperty(key, defaultValue);
	}

	static ClassLoader getTCCL() {
		if (System.getSecurityManager() != null) {
			return AccessController
					.doPrivileged(new PrivilegedAction<ClassLoader>() {
						public ClassLoader run() {
							return Thread.currentThread()
									.getContextClassLoader();
						}
					});
		} else {
			return Thread.currentThread().getContextClassLoader();
		}
	}

	static void setTCCL(final ClassLoader paramCl) {
		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged(new PrivilegedAction<Void>() {
				public Void run() {
					Thread.currentThread().setContextClassLoader(paramCl);
					return null;
				}
			});
		} else {

			Thread.currentThread().setContextClassLoader(paramCl);
		}
	}
}