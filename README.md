# Reproduce exception with Powermock/SchemaValidation since 8u162

This sample maven project works with 8u152 and fails with 8u162.
This demonstrates that `javax.xml.validation.SchemaFactory` has become more sensitive
to different classloaders (in the uncommon case that a mocking framework instruments some of the classes).

## Fails only since 8u162

When using the ignores which make sure that `javax.*` and `org.xml` are not mocked and therefore exist in the system classloader:

    @PowerMockIgnore({"javax.*","org.xml.*"})

Then only 8u161 fails:

    testSchema(net.eckenfels.test.ValidatorTest)  Time elapsed: 0.25 sec  <<< ERROR!
    com.sun.org.apache.xerces.internal.impl.dv.DVFactoryException: Schema factory class
        com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl does not extend
        from SchemaDVFactory.
      at com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory.getInstance(SchemaDVFactory.java:75)
      at com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory.getInstance(SchemaDVFactory.java:57)
      at com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader.reset(XMLSchemaLoader.java:1024)
      at com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader.loadGrammar(XMLSchemaLoader.java:556)
      at com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader.loadGrammar(XMLSchemaLoader.java:535)
      at com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory.newSchema(XMLSchemaFactory.java:254)
      at javax.xml.validation.SchemaFactory.newSchema(SchemaFactory.java:638)
      at net.eckenfels.test.ValidatorTest$MockMe.testSchema(ValidatorTest.java:30)
      at net.eckenfels.test.ValidatorTest.testSchema(ValidatorTest.java:37)
      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
      at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
      at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
      at java.lang.reflect.Method.invoke(Method.java:498)
      at org.junit.internal.runners.TestMethod.invoke(TestMethod.java:68)
      at org.powermock.modules.junit4.internal.impl.PowerMockJUnit44RunnerDelegateImpl$PowerMockJUnit44MethodRunner.runTestMethod(PowerMockJUnit44RunnerDelegateImpl.java:326)
      at org.junit.internal.runners.MethodRoadie$2.run(MethodRoadie.java:89)
    ...

## Fails in both versions (different error)

When not mocking `javax.*` (only `com.sun` and `org.xml` have powermock`s classloader.)

    @PowerMockIgnore({"javax.*"})

In this case it fails with 8u152 and 8u162, but with different errors:

8u162 (same error as above):

8u152:

    java.lang.LinkageError: loader constraint violation: when resolving overridden method
      "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory.getErrorHandler()Lorg/xml/sax/ErrorHandler;"
      the class loader (instance of org/powermock/core/classloader/MockClassLoader) of the current class,
      com/sun/org/apache/xerces/internal/jaxp/validation/XMLSchemaFactory, and its superclass
      loader (instance of <bootloader>), have different Class objects for the type org/xml/sax/ErrorHandler used in the signature
      at java.lang.Class.getDeclaredConstructors0(Native Method)
      at java.lang.Class.privateGetDeclaredConstructors(Class.java:2671)
      at java.lang.Class.getConstructor0(Class.java:3075)
      at java.lang.Class.newInstance(Class.java:412)
      at javax.xml.validation.SchemaFactoryFinder.createInstance(SchemaFactoryFinder.java:305)
      at javax.xml.validation.SchemaFactoryFinder._newFactory(SchemaFactoryFinder.java:231)
      at javax.xml.validation.SchemaFactoryFinder.newFactory(SchemaFactoryFinder.java:145)
      at javax.xml.validation.SchemaFactory.newInstance(SchemaFactory.java:213)
      at net.eckenfels.test.ValidatorTest$MockMe.testSchema(ValidatorTest.java:29)
      at net.eckenfels.test.ValidatorTest.testSchema(ValidatorTest.java:37)
    ...

## Solution

When using javax.xml.validation with PowerMock make sure that all or none of the involved packages are instrumented (`{"javax.", "org.xml", "com.sun.org.apache.xerces.*"}`).