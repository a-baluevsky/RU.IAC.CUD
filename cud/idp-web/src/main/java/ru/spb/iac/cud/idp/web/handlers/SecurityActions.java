package ru.spb.iac.cud.idp.web.handlers;

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

}
