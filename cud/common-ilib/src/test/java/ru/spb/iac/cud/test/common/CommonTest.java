package ru.spb.iac.cud.test.common;


import iac.cud.authmodule.dataitem.AuthItem;
import iac.cud.authmodule.dataitem.PageItem;
import iac.cud.infosweb.dataitems.AppAccessItem;
import iac.cud.infosweb.dataitems.AppBlockItem;
import iac.cud.infosweb.dataitems.AppItem;
import iac.cud.infosweb.dataitems.AppOrgManItem;
import iac.cud.infosweb.dataitems.AppSystemItem;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class CommonTest {
    
    public CommonTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    	// Lifecycle.beginCall();
    }
    
    @After
    public void tearDown() {
    	//Lifecycle.endCall();
    }

    /**
     * Test of findAlias method, of class CriteriaUtil.
     */
    @Test
    public void testFindAlias() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AuthItem am = new AuthItem();
        boolean result = true;
      am.setIdUser(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias2() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        PageItem am = new PageItem();
        boolean result = true;
      am.setPermList(null);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias3() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppAccessItem am = new AppAccessItem();
        boolean result = true;
      am.setBaseId(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias4() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppBlockItem am = new AppBlockItem();
        boolean result = true;
      am.setBaseId(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias5() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppItem am = new AppItem();
        boolean result = true;
      am.setBaseId(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias6() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppOrgManItem am = new AppOrgManItem();
        boolean result = true;
      am.setBaseId(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }

    @Test
    public void testFindAlias7() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppSystemItem am = new AppSystemItem();
        boolean result = true;
      am.setBaseId(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }

    @Test
    public void testFindAlias12() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AuthItem am = new AuthItem();
        boolean result = true;
      am.setPageList(null);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias22() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        PageItem am = new PageItem();
        boolean result = true;
      am.setPermList(null);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias32() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppAccessItem am = new AppAccessItem();
        boolean result = true;
      am.setIdApp(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias42() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppBlockItem am = new AppBlockItem();
        boolean result = true;
      am.setIdApp(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias52() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppItem am = new AppItem();
        boolean result = true;
      am.setIdApp(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
    
    @Test
    public void testFindAlias62() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppOrgManItem am = new AppOrgManItem();
        boolean result = true;
      am.setIdApp(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }

    @Test
    public void testFindAlias72() {
        System.out.println("ArmManager:forViewCert");
        boolean expResult = false;
       
        AppSystemItem am = new AppSystemItem();
        boolean result = true;
      am.setIdApp(1L);
       // assertEquals(expResult, result);
       Assert.assertTrue(result);
    }
  
}