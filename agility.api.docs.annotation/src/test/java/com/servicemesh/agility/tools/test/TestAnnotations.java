package com.servicemesh.agility.tools.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import org.junit.Assert;

public class TestAnnotations {

   @Test
   public void test() {
      Class<?> sbClass = SampleBean.class;
      Class<?> ssClass = SampleService.class;
      
//      logger.info("Class " + sbClass.getName() + " has " + sbClass.getAnnotations().length + " annotations.");
//      logger.info("Class " + ssClass.getName() + " has " + ssClass.getAnnotations().length + " annotations.");
//      logger.info("Class " + sbClass.getName() + " has " + sbClass.getMethods().length + " methods.");
//      logger.info("Class " + ssClass.getName() + " has " + ssClass.getMethods().length + " methods.");
      Assert.assertTrue(sbClass.getAnnotations().length > 0);
      Assert.assertTrue(ssClass.getAnnotations().length > 0);
      Assert.assertTrue(sbClass.getMethods().length > 0);
      Assert.assertTrue(ssClass.getMethods().length > 0);
   }

}
