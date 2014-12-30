package ru.spb.iac.cud.sts.core;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

class SecurityActions {

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

}