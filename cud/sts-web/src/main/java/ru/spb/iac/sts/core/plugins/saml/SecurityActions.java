package ru.spb.iac.sts.core.plugins.saml;



class SecurityActions {

  
    static Class loadClass(final Class<?> theClass, final String fullQualifiedName) {
      
            ClassLoader classLoader = theClass.getClassLoader();

            Class<?> clazz = loadClass(classLoader, fullQualifiedName);
            if (clazz == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
                clazz = loadClass(classLoader, fullQualifiedName);
            }
            return clazz;
       }

  
    static Class loadClass(final ClassLoader classLoader, final String fullQualifiedName) {
      
            try {
                return classLoader.loadClass(fullQualifiedName);
            } catch (ClassNotFoundException e) {
            	 return null;
            }
           
       }
}