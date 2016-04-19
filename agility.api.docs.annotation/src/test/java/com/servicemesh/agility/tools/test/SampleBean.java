package com.servicemesh.agility.tools.test;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.servicemesh.agility.tools.annotation.ApiDictionary;
import com.servicemesh.agility.tools.annotation.ApiField;
import com.servicemesh.agility.tools.annotation.ApiModel;
import com.servicemesh.agility.tools.annotation.ApiDictionaryEntry;

@ApiDictionary(entries = {@ApiDictionaryEntry(token = "docRoot", definition = "http://www.servicemesh.com/docs/")})

@ApiModel(externalDocLink = "{{docRoot}}sampleBean.html",
          comment = "This is the sample bean",
          description = "This is the sample bean descritpion.")
public class SampleBean {

   @ApiField(comment = "comment on id field",
             systemGenerated = true)
   @XmlElement(required = true)
   private long   id;
   
   @ApiField(comment = "comment on string field")
   @XmlElement(defaultValue = "defaultStringValue")
   private String stringField;
   
   @ApiField(comment = "comment on int field",
             allowableValues = "[0-9]")
   private int    intField;
   
   @ApiField(comment = "comment on long field")
   private Long   longField;
   
   @ApiField(comment = "comment on date field",
             systemGenerated = true)
   private Date   dateField;
   
   @ApiField(comment = "comment on date array field")
   private Date[]   dateArrayField;

   @ApiField(comment = "comment on list field")
   private List<Sample2Bean>   sample2ListField;

   public SampleBean() {
   }

   public String getStringField() {
      return stringField;
   }

   public void setStringField(String stringField) {
      this.stringField = stringField;
   }

   public int getIntField() {
      return intField;
   }

   public void setIntField(int intField) {
      this.intField = intField;
   }

   public Long getLongField() {
      return longField;
   }

   public void setLongField(Long longField) {
      this.longField = longField;
   }

   public Date getDateField() {
      return dateField;
   }

   public void setDateField(Date dateField) {
      this.dateField = dateField;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public Date[] getDateArrayField() {
      return dateArrayField;
   }

   public void setDateArrayField(Date[] dateArrayField) {
      this.dateArrayField = dateArrayField;
   }

   public List<Sample2Bean> getSample2ListField() {
      return sample2ListField;
   }

   public void setSample2ListField(List<Sample2Bean> sample2ListField) {
      this.sample2ListField = sample2ListField;
   }
   
}
