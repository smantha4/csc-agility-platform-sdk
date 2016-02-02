
package com.servicemesh.agility.api;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

import com.servicemesh.agility.tools.annotation.ApiDeprecated;
import com.servicemesh.agility.tools.annotation.ApiField;
import com.servicemesh.agility.tools.annotation.ApiModel;

/**
 * Tests that the model files in the com.servicemesh.agility.api package are annotated for Athena.
 * @author Patrick Crocker
 */
public class AthenaModelTest {

	/**
	 * ANNOTATED_CLASSES is initialized in conjunction with the Wiki page at http://wiki.servicemesh.com:8080/display/dev/Annotation+Verification
	 * As classes are annotated, both the Wiki page and ANNOTATED_CLASSES should be updated. Simple class names are used.
	 * 
	 */
	private final String[] ANNOTATED_CLASSES = {/*"Configuration", "ImportDirectives", "Assetlist", "Asset", "AccessRight", "AccessRightSet", 
         "BlueprintRef", "Cloud", "CloudType", "TargetCloud", "NameValuePair", "ErrorMessage", "AgilityVersion", "VersionList", 
         "Parameter", "Command", "Commands", "AssetTypeMeta", "AssetMatch" ,"PropertyMatch", "PlanEvalRequest", "WorkflowRequest", 
         "PublishRequest", "TopologyStats", "FieldMeta", "TreeNode", "AssetTypeBrief", "ConfigurationRepository", "ConfigurationArtifact", 
         "ConfigurationArtifactType", "ConfigurationResource", "Container", "CustomContainer", "CustomContainerList", "ContainerRights", 
         "Credential", "DeploymentBinding", "Deployment", "DeploymentArtifactConfig", "DeploymentConfiguration", "DeploymentPlan", 
         "DeploymentRequest", "Envelope", "Environment", "EnvironmentType", "Rating", "Comment", "ErrorInfo", "ConfigProperty", 
         "OperatingSystem", "FileSystem", "FileSystemDevice", "ObjectReference", "Variable", "VariableList", "AccessUri", "AccessUriList", 
         "PermissionType", "ProjectRole", "Connection", "VariableValueSet", "Runtime", "SearchTree", "HotswapList", "SecurityRole", 
         "AccessList", "Host", "Location", "Domain", "UserGroup", "Address", "AddressList", "NetworkAddressList", "Alias", "AliasList", 
         "NetworkInterface", "NetworkInterfaceList", "AddressRange", "AddressRangeList", "NetworkServiceType", "NetworkService", 
         "DhcpOptions", "Network", "Item", "VersionedItem", "CustomItem", "CustomItemList", "Link", "LogicalLink", "VersionedItemLink", 
         "DescriptiveLink", "ItemLink", "Linklist", "Package", "PolicyType", "Policy", "PolicyAssignment", "PolicyMeta", "PolicyMetaModel", 
         "LifecyclePolicyMeta", "ConfigurationPolicyMeta", "DeploymentPolicyMeta", "PriceEngine", "Project", "Property", "PropertyDefinition", 
         "PropertyDefinitionList", "PropertyDefinitionReference", "PropertyDefinitionGroup", "InputVariableRequest", "InputVariable", 
         "InputVariableList", "PropertyType", "PropertyTypeValue", "Protocol", "EffectiveProtocol", "EffectiveProtocolRequest", "Proxy", 
         "ArtifactType", "Artifact", "SolutionDeploymentConfig", "SolutionDeployment", "ArtifactRuntimeBinding", "ArtifactConfiguration", 
         "PlatformServiceType", "PlatformService", "ServiceBinding", "ServiceBindingType", "Solution", "Deployer", "ArtifactAttachment", 
         "Repository", "Resource", "ResourceMetric", "ResourceMetrics", "ResourcePolicy", "ResourceMatch", "ResourceRank", "ResourceWeight", 
         "ResourceWeightMetaList", "ResourceWeightMeta", "ResourceWeightInfo", "ResourceMapping", "Script", "ScriptLanguage", "ScriptPropertyReference", 
         "ScriptStatus", "ScriptClasspath", "AssetFilter", "Stack", "Task", "Tasklist", "Template", "Topology", "User", "FieldValidator", 
         "IntegerRange", "IntegerRangeValidator", "IntegerScaleValidator", "NumericRange", "NumericRangeValidator", "NumericScaleValidator", 
         "DateValidator", "StringLengthValidator", "EmailValidator", "RegexValidator", "FieldValidators", "AttachmentLocation", "Attachment", 
         "Architecture", "Product", "State", "DeploymentState", "LaunchItemState", "ScriptState", "ScriptErrorAction", "RaidLevel", "PrimitiveType", 
         "ValueConstraintType", "ImportMode", "LookupFailCode", "ErrorLevel", "ResourceType", "ResourceAffinity", "ScriptType", "AccessListDirection", 
         "DateDirection", "CredentialType", "ProxyType", "ProxyUsage", "AuthType", "RepositoryUsage", "ResourceMetricType", "ScriptVariableRequest", 
         "ScriptVariable", "ScriptVariableList", "ServiceMeshList", "Workload", "WorkflowMeta", "WorkflowTask", "WorkflowTaskMeta", "WorkflowTransitionMeta", 
         "WorkflowTaskList", "Volume", "ValueProvider", "Taxonomy", "VolumeStorageSnapshot", "StorageSnapshot", "Storage", "VolumeStorage", "StoreCategory", 
         "StoreCatalog", "StoreEditionType", "StoreEdition", "StorePrice", "StoreProduct", "StorePublisher", "StoreReleaseType", "StoreRelease", 
         "StoreResource", "StoreProductAdapter", "StoreProductAdapterItem", "StoreProductAdapterItemList", "StoreProductType", 
         "Snapshot", "LdapGroup", "LdapGroupList", "LaunchItem", "LaunchItemDeployment", "Image", "EULA", "DesignAlias", 
         "DesignDeployer", "DesignItem", "DesignContainer", "DesignConnection", "AuthGroup", "AuthGroupList", "AuthenticationType", "Authentication", 
         "AuthenticationList", "AssetType", "AssetTypeList", "MgmtScript", "MgmtScriptGroup", "MgmtScript", "Model", "Instance", "OnboardRequest", 
         "OnboardMeta", "CloudMetaModel", "AssetProperty", "ForeignAssetProperty", "Blueprint", Control, Page, PageLayout, PageLayoutGroup, PageContext*/};
	
	private static final String JAVA_UTIL   = "java.util.";
	private static final String LIST        = JAVA_UTIL + "List";
   private static final String ARRAY_LIST  = JAVA_UTIL + "ArrayList";
    
    /**
	 * PACKAGE_NAME is the package being checked.
	 *  *_ERROR are the messages printed when errors that fail the test are found.
	 */
    private final String PACKAGE_NAME           = "com.servicemesh.agility.api";
    private final String APIMODEL_ERROR         = ": @ApiModel tag is missing.";
    private final String APIMODEL_QMARKS        = ": @ApiModel tag is missing content.";
    private final String XMLROOT_ERROR          = ": @XmlRootElement is non-existent/incorrect.";
    private final String DEPRECATED_ERROR       = ": @Deprecated tag is missing.";
    private final String APIFIELD_ERROR         = ": @ApiField tag is missing.";
    private final String APIFIELD_QMARKS        = ": @ApiField tag is missing content.";
    private final String DEPRECATED_FIELD_ERROR = ": is missing @Deprecated tag.";
    private final String OBJECT_FACTORY_NAME    = "ObjectFactory";
    private final String PACKAGE_INFO           = "package-info";
    private final String VALIDATE_CONTENT_OPT   = "apidoc_validateContent";
    
	 private List<String> fullSimpleNameList = new ArrayList<String>();                              // full list of simple class names
	 private double numOfClasses = 0; 
	 
	 /*
	  * If set to true,  the test checks for question marks in the content of annotations.
	  * 
	  * This value can be changed via the following JVM system property: -Dapidoc_validateContent=false
	  * The JVM property defaults to true, meaning the test will fail if there are question marks, 
	  * which implies missing content.
	  */
	 private boolean contentCheck = Boolean.parseBoolean(System.getProperty(VALIDATE_CONTENT_OPT, "true"));
	 
	/**
	 * Tests that the Dzone code successfully gets the specified classes and stores them in an array.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testDzoneCode() throws ClassNotFoundException, IOException {
		Class<?>[] classArray = getClasses(PACKAGE_NAME);
		assertNotNull(classArray);
		assertTrue(classArray.length > 1);
	}
	
	/**
	 * Tests that the models are correctly annotated for Athena, and displays what classes are still to be annotated on passing.
	 * 
	 * If a class that is supposed to be correctly annotated is found to be incorrectly annotated, the test will fail and the faulty 
	 * classes will be displayed.
	 * 
	 * If a class in ANNOTATED_CLASSES cannot be found, it will be ignored and printed at the end of execution.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testAnnotations() throws ClassNotFoundException, IOException {
		Class<?>[] classes = getClasses(PACKAGE_NAME);
		List<Class<?>> allClasses = new ArrayList<Class<?>>();
		List<String> needsAnnotationNames = new ArrayList<String>();                      // classes that are expected to NOT be annotated for Athena
		List<String> successfullyAnnotated = new ArrayList<String>();
		boolean passed = true;
		
		for(int k = 0; k < classes.length; k++) {
		   String curClass = classes[k].getSimpleName();
			if (!curClass.equals(this.getClass().getSimpleName()) && !curClass.equals(OBJECT_FACTORY_NAME) && !curClass.equals(PACKAGE_INFO)) {   
				allClasses.add(classes[k]);                                                // special case:
			}                                                                             // avoids this class (located in same package as files to be checked) and ObjectFactory (JAXB generated class)
			
			fullSimpleNameList.add(classes[k].getSimpleName());
		}
		
		numOfClasses = allClasses.size();
		
		for (int i = 0; i < allClasses.size(); i++) {
			Class<?> clazz = allClasses.get(i);
			String className = clazz.getSimpleName();
			Field[] fields = clazz.getDeclaredFields();
			boolean curPassed = true;
			
			ApiModel model = (ApiModel)clazz.getAnnotation(ApiModel.class);
			XmlRootElement element = (XmlRootElement)clazz.getAnnotation(XmlRootElement.class);
			ApiDeprecated apiDep = (ApiDeprecated)clazz.getAnnotation(ApiDeprecated.class);
			Deprecated dep = (Deprecated)clazz.getAnnotation(Deprecated.class);
			
			if (isAnnotated(className)) {
			   
			   if (!checkApiModel(model)) {
				  System.out.println(className + APIMODEL_ERROR);
				  curPassed = false;
		       }
			   
			   if (!checkXmlRootElement(element, className)) {
				   System.out.println(className + XMLROOT_ERROR);
				   curPassed = false;
			   }
			   
			   if (!checkDeprecated(apiDep, dep)) {
				   System.out.println(className + DEPRECATED_ERROR);
				   curPassed = false;
			   }
			   
			   for(int j = 0; j < fields.length; j++) {
              boolean actualField = checkField(clazz, fields[j]);
					   
				  if(actualField) {
				    apiDep = (ApiDeprecated)fields[j].getAnnotation(ApiDeprecated.class);
					 dep = (Deprecated)fields[j].getAnnotation(Deprecated.class);
					         
					 if (!isFieldAnnotated(fields[j])) {
						System.out.println(className + ": " + fields[j].getName() + APIFIELD_ERROR);
						curPassed = false;
					 }
					 else if (!checkDeprecated(apiDep, dep)) {
						System.out.println(className + ": " + fields[j].getName() + DEPRECATED_FIELD_ERROR);
						curPassed = false;
					 }
					 
					 if (contentCheck && !checkFieldContent(fields[j].getAnnotation(ApiField.class))) {
				       System.out.println(className + ": " + fields[j].getName() + APIFIELD_QMARKS);  
					    curPassed = false;
				    }
					 else if (!contentCheck && !checkFieldContent(fields[j].getAnnotation(ApiField.class))) {
					    System.out.println(className + ": " + fields[j].getName() + APIFIELD_QMARKS); 
					 }
				  }
			   }
		   }
		   else if(!checkXmlRootElement(element, className)) { 
			   System.out.println(className + XMLROOT_ERROR);
			   curPassed = false;
		   }
		   else {
		      needsAnnotationNames.add(className);
		   }
			
			if (contentCheck && !checkModelContent(model.comment(), model.description())) {
	         System.out.println(className + APIMODEL_QMARKS);
			   curPassed = false;
	      }
			else if (!contentCheck && !checkModelContent(model.comment(), model.description())) {
			   System.out.println(className + APIMODEL_QMARKS);
			   System.out.println();
			}
			   
			
			if (curPassed) {
	         successfullyAnnotated.add(className);
	      }
			else {
			   needsAnnotationNames.add(className);
			   passed = false;
			}
			
	    }
		
	    printMessage(needsAnnotationNames, successfullyAnnotated);
	    
	    if (!passed) {
	    	fail();
	    }
	}
	
	private boolean checkModelContent(String comment, String description) {
	   
	   if (comment != null && comment.length() > 0) {
         for (int i = 0; i < comment.length(); i++) {
            if(comment.charAt(i) == '?') {
               return false;
            }
         }
      }
	   else if (comment != null && comment.length() == 0) {
	      return false;
	   }
	   
	   if (description != null && description.length() > 0) {
         for (int i = 0; i < description.length(); i++) {
            if(description.charAt(i) == '?') {
               return false;
            }
         }
      }
      
      return true;
   }

   private boolean checkFieldContent(ApiField field) {
      
      if (field == null) {
         return true;
      }
      
      if (field.comment() != null && field.comment().length() > 0) {
         for (int i = 0; i < field.comment().length(); i++) {
            if(field.comment().charAt(i) == '?') {
               return false;
            }
         }
      }
      
      return true;
   }

   /**
	 * Prints the classes to be annotated.
	 * @param badClassNames the classes that are supposed to be annotated but failed the checks
	 * @param needsAnnotationNames the classes that are not expected to be annotated
	 */
	private void printMessage(List<String> needsAnnotationNames, List<String> successfullyAnnotated) {
	   System.out.println();
      System.out.println("SUCCESS");
      System.out.println("- The following models are correctly annotated for Athena: " + successfullyAnnotated);
      System.out.println("- Percentage of all models that have been annotated: " + (int)(((double)successfullyAnnotated.size()/numOfClasses)*100) + "%");
      
      System.out.println("WARNING");
      System.out.println("- The following models need to be annotated for Athena: " + needsAnnotationNames);
      annotatedClassVerification(needsAnnotationNames);
	}
	
	/**
	 * Verifies the class names in ANNOTATED_CLASSES.
	 * @param expected the list of classes not in ANNOTATED_CLASSES
	 */
	private void annotatedClassVerification(List<String> expected) {
		List<String> skippedClasses = new ArrayList<String>();
		
		for(int i = 0; i < ANNOTATED_CLASSES.length; i++) {
			String curClass = ANNOTATED_CLASSES[i];
			
			if (!fullSimpleNameList.contains(curClass) && !expected.contains(curClass)) {
				skippedClasses.add(curClass);
			}
		}
		
		System.out.println("- The following models could not be found and were ignored: " + skippedClasses);
	}
	
	/**
	 * Compares the className parameter to the element of ANNOTATED_CLASSES.
	 * @param className the simple name of the current class
	 * @return true if the class is annotated, false otherwise
	 */
	private boolean isAnnotated(String className) {
		
		if (ANNOTATED_CLASSES.length == 0) {                                 // special case:
			return true;                                                      // checks all the classes if the array is empty  
		}
		
		for (int i = 0; i < ANNOTATED_CLASSES.length; i++) {	
			if (ANNOTATED_CLASSES[i].equals(className))	{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks the @XmlRootElement annotation for existence and name
	 * @param element the @XmlRootElement annotation
	 * @param className the simple name of the current class
	 * @return
	 */
	private boolean checkXmlRootElement(XmlRootElement element, String className) {
		
		if (className.equals("Configuration")) {                              // special case:
			return (element.name().equals("ConfigSettings") ? true : false);   // Configuration intentionally left alone  TODO
		}
		else if (className.equals("package-info")) {                          // special case:
			return true;                                                       // package-info is a JAXB generated file
		}
		else if (element == null) {
			return false;
		}
		else if (!element.name().equals(className)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks whether proper annotations are present at the class level.
	 * @param model an ApiModel annotation
	 * @param element an XmlRootElement annotation
	 * @param className the simple name of a class
	 * @return true if the annotations are present
	 */
	private boolean checkApiModel(ApiModel model) {
		
		if (model == null) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks whether the proper annotations are present at the field level.
	 * @param field a Field object
	 * @return true if the annotations are present
	 */
	private boolean isFieldAnnotated(Field field) {
		
		if (field != null && field.getAnnotation(ApiField.class) == null) {
			if (field.getAnnotation(XmlElements.class) == null && field.getAnnotation(XmlElementRefs.class) == null) { 
			   return false;                                                 // special case:
				                                                              // if a choice type, ApiField will not be present but XmlElements or XmlElementRefs should be
			}                                                                // i.e. FileValidators.java
		}
		
		return true;
	}
	
	/**
	 * Checks whether the class has a particular field.
	 * @param clazz the class that is being checked
	 * @param field a Field object
	 * @return true if the class contains the field
	 */
	private boolean checkField(Class<?> clazz, Field field) {
		HashMap<String, Field> fieldMap = getClassProperties(clazz);
		return fieldMap.containsValue(field);
	}
	
	/**
	 * Checks the @ApiDeprecated tag along with its counterpart @Deprecated
	 * @param apiDep the @ApiDeprecated tag
	 * @param dep the @Deprecated tag
	 * @return true if the @ApiDeprecated is absent or if both @ApiDeprecated
	 *         and @Deprecated are present
	 */
	private boolean checkDeprecated(ApiDeprecated apiDep, Deprecated dep) {
		
		if (apiDep != null && dep == null) {
			return false;
		}
		
		return true;
	}
	
	
	/**
     * This method will guess at property name by removing the first three characters ("get" or "set" expected)
	 * and changing the first character to meet camel case requirements.
	 * 
	 * code from Henry Crocker
	 * 
	 * @param String methodName - name of the method; getter or setter expected
     * 
	 * @return String - guessed name of a class property
	 */
    private static String getPropertyNameFromMethodName(String methodName) {
	   String retVal = "";

	   if ((methodName != null) && !methodName.isEmpty()) {
	     String fieldName = methodName.substring(3); // remove "set"/"get"
	     retVal = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);  // change first char for camel case
	   }

	   return retVal;
	  }
	
	/**
    * This method will get a list of the fields defined for a class.  The original idea was use the POJOs
    * (Java Bean pattern) objects created by JAXB and use the setters to determine the fields; however,
    * list properties do not have setters.  Using getters could return fields that we should not be 
    * mocking.  The solution was to simply get the list of fields and mock them.  JAXB generates protected
    * or private fields so it is necessary to change the access before they can be set.
    * 
    * Given a map is used here, no duplicates will exist (because a getter and setter was found for field)
    * 
    * code from Henry Crocker
    * 
    * @param Class clazz - type to be mocked
    * 
    * @return HashMap<String, Field> - a map of fields found in the property
    */
     private static HashMap<String, Field> getClassProperties(Class<?> clazz) {
      HashMap<String, Field> map = new HashMap<String, Field>();

      if (clazz != null) {
        String  className    = clazz.getName();
        Class<?> superClass    = clazz.getSuperclass();
        Method[] publicMethods = clazz.getMethods();
        
        for (int i = 0; i < publicMethods.length; i++) {
            Method    m          = publicMethods[i];
            String    mName      = m.getName();
            Class<?>[] mParams    = m.getParameterTypes();
            int        pCnt      = mParams.length;
            Class<?>  returnType = m.getReturnType();
            String    rtName    = returnType.getName();
        
            // setters that take a single parameter (JavaBean pattern) and getters that return list type
            if ((mName.startsWith("set") && (pCnt == 1)) ||
                ((mName.startsWith("get") && (pCnt == 0) && (rtName.startsWith(LIST) || rtName.startsWith(ARRAY_LIST))))) {
              String fieldName = getPropertyNameFromMethodName(mName);
              
              try {
                  map.put(fieldName, clazz.getDeclaredField(fieldName));  // if it shows with getter and setter, only one will appear
              }
              catch (NoSuchFieldException fe) {
                  // this may be found later by the superclass
                  if (superClass == null) {
                    String errMsg = "An exception occurred while finding field " + fieldName + " in class " + className;
                    System.out.println(errMsg);
                  }
              }
              catch (Throwable t) {
                  String errMsg = "An exception occurred while finding field " + fieldName + " in class " + className;
                  System.out.println(errMsg);
              }
            }
        }
        
        if (clazz.getSuperclass() != null) {
            map.putAll(getClassProperties(clazz.getSuperclass()));
        }
      }
      else {
        System.out.println("Could not extract fields for class because the parameter is null.");
      }
      
      return map;
  }
	
	/**
	* Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	* 
	* code came from http://www.dzone.com/snippets/get-all-classes-within-package
	* 
	* @param packageName The base package
	* @return The classes
	* @throws ClassNotFoundException
	* @throws IOException
	*/
	private static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
	
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		
		return classes.toArray(new Class[classes.size()]);
	}
	 
	/**
	* Recursive method used to find all classes in a given directory and subdirs.
	* 
	* code came from http://www.dzone.com/snippets/get-all-classes-within-package
	*
	* @param directory   The base directory
	* @param packageName The package name for classes found inside the base directory
	* @return The classes
	* @throws ClassNotFoundException
	*/
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		
		if (!directory.exists()) {
			return classes;
		}
		
		File[] files = directory.listFiles();
		
		for (File file : files) {                         //edited to no longer load sub-directories
			if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		
		return classes;
	}
}