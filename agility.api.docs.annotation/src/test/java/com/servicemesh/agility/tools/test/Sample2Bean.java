package com.servicemesh.agility.tools.test;

import javax.xml.bind.annotation.XmlElement;
import com.servicemesh.agility.tools.annotation.ApiDictionary;
import com.servicemesh.agility.tools.annotation.ApiField;
import com.servicemesh.agility.tools.annotation.ApiModel;
import com.servicemesh.agility.tools.annotation.ApiDictionaryEntry;

@ApiDictionary(entries = {@ApiDictionaryEntry(token = "docRoot", definition = "http://www.servicemesh.com/docs/")})

@ApiModel(externalDocLink = "{{docRoot}}sample2Bean.html",
          comment = "This is the sample2 bean",
          description = "This is the sample2 bean descritpion.")
public class Sample2Bean {

   @ApiField(comment = "comment on id field",
             systemGenerated = true)
   @XmlElement(required = true)
   private long   id;
   
   @ApiField(comment = "comment on string field")
   @XmlElement(defaultValue = "")
   private String stringField;

   @ApiField(comment = "comment on people field")
   private People peopleField;

   public Sample2Bean() {
   }

   public String getStringField() {
      return stringField;
   }

   public void setStringField(String stringField) {
      this.stringField = stringField;
   }

   public People getPeopleField() {
      return peopleField;
   }

   public void setPeopleField(People peopleField) {
      this.peopleField = peopleField;
   }

}
