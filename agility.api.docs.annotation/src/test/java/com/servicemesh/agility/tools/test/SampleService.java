package com.servicemesh.agility.tools.test;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.servicemesh.agility.tools.annotation.ApiAcl;
import com.servicemesh.agility.tools.annotation.ApiAction;
import com.servicemesh.agility.tools.annotation.ApiDictionary;
import com.servicemesh.agility.tools.annotation.ApiDictionaryEntry;
import com.servicemesh.agility.tools.annotation.ApiOtherParam;
import com.servicemesh.agility.tools.annotation.ApiParam;
import com.servicemesh.agility.tools.annotation.ApiResource;
import com.servicemesh.agility.tools.annotation.ApiResponse;
import com.servicemesh.agility.tools.annotation.ApiRequest;

@ApiDictionary(entries = {@ApiDictionaryEntry(token = "sampleBean", definition = "com.servicemesh.agility.tools.test.SampleBean"),
                          @ApiDictionaryEntry(token = "list", definition = "List"),
                          @ApiDictionaryEntry(token = "string", definition = "java.lang.String"),
                          @ApiDictionaryEntry(token = "docRoot", definition = "http://www.servicemesh.com/docs/")})

@ApiResource(comment = "comment on resource",
             description = "description on resource",
             protocol = "http",
             version = "1.0.0",
             displayName = "Sample Service",
             externalDocLink = "{{docRoot}}sampleService.html")
@Path("/")
@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON,MediaType.TEXT_HTML})
public class SampleService {
	   
   public SampleService() {
      super();      
   }

   @ApiAction(protocol = "http",                                                  //// overrides value from resource
              displayName = "Get Version",
              comment = "Comment on get version action.",
              description = "Description on get version action.",
              authRequired = "false",
              externalDocLink = "{{docRoot}}getversion.html",
              responses = {@ApiResponse(dataType = "{{string}}", 
                                        responseCode = "200",
                                        message = "success",
                                        comment = "this is for success"),
                           @ApiResponse(dataType = "{{string}}", 
                                        responseCode = "500",
                                        message = "failed",
                                        comment = "this is for failure")},
              acls = {@ApiAcl(name = "user",
                              description = "general agility user")})
   @GET
   @Produces({MediaType.TEXT_HTML})
   public Response getVersion(@ApiOtherParam(name="fields", dataType="java.lang.String", 
                                             paramType="query", 
                                             comment="The query parameters will be extracted and processed by the action.") @Context UriInfo uriInfo) {
      return null;
   }

   @ApiAction(protocol = "http",
              displayName = "Get Samples",
              comment = "Comment on get samples.",
              description = "Description on get samples.",
              authRequired = "true",
              externalDocLink = "{{docRoot}}getsamples.html",
              responses = {@ApiResponse(dataType = "{{sampleBean}}", 
                                        containerType = "{{list}}",
                                        responseCode = "200",
                                        message = "success",
                                        comment = "this is for success"),
                           @ApiResponse(dataType = "{{string}}", 
                                        responseCode = "500",
                                        message = "failed",
                                        comment = "this is for failure")},
              acls = {@ApiAcl(name = "hr",
                              description = "human resources")})
   @Path("/all/samples")
   @GET
   public Response getAllSamples() {
      return null;
   }

   @ApiAction(displayName = "Get Sample",
              comment = "Comment on get sample.",
              description = "Description on get sample.",
              authRequired = "true",
              externalDocLink = "{{docRoot}}getsample.html",
              responses = {@ApiResponse(dataType = "{{sampleBean}}", 
                                        responseCode = "200",
                                        message = "success",
                                        comment = "this is for success"),
                           @ApiResponse(dataType = "{{string}}", 
                                        responseCode = "500",
                                        message = "failed",
                                        comment = "this is for failure")},
              acls = {@ApiAcl(name = "hr",
                              description = "human resources"),
                      @ApiAcl(name = "manager",
                              description = "manager")})
   @Path("/sample/{sampleId}")
   @GET
   public Response getEmployee(@ApiParam(comment = "sample id comment") 
                               @PathParam("sampleId") String sampleId,
                               @ApiParam(comment = "format parameter")
                               @DefaultValue("xml") @QueryParam("format") String format) {
      return null;
   }

   @ApiAction(displayName = "Create Sample",
              comment = "Comment on create sample.",
              description = "Description on create sample.",
              authRequired = "true",
              externalDocLink = "{{docRoot}}createsample.html",
              requests = {@ApiRequest(dataType = "{{sampleBean}}",
                                      comment = "comment on request object",
                                      requiredFields = "id,stringField",
                                      optionalFields = "intField,longField,dateField")},
              responses = {@ApiResponse(dataType = "{{sampleBean}}", 
                                        responseCode = "201",
                                        message = "success",
                                        comment = "this is for success"),
                           @ApiResponse(dataType = "{{string}}", 
                                        responseCode = "500",
                                        message = "failed",
                                        comment = "this is for failure")},
              acls = {@ApiAcl(name = "hr",
                              description = "human resources"),
                      @ApiAcl(name = "manager",
                              description = "manager")})
   @Path("/sample")
   @POST
   public Response createSample() {
      return null;
   }

   @ApiAction(comment = "Comment on create samples.",
              description = "Description on create samples.",
              authRequired = "true",
              externalDocLink = "{{docRoot}}createsamples.html",
              requests = {@ApiRequest(dataType = "{{sampleBean}}",
                                      containerType = "{{list}}",
                                      comment = "comment on request objects",
                                      requiredFields = "*")},
              responses = {@ApiResponse(dataType = "{{sampleBean}}", 
                                        containerType = "{{list}}",
                                        responseCode = "201",
                                        message = "success",
                                        comment = "this is for success"),
                           @ApiResponse(dataType = "{{string}}", 
                                        responseCode = "500",
                                        message = "failed",
                                        comment = "this is for failure")},
              acls = {@ApiAcl(name = "hr",
                              description = "human resources"),
                      @ApiAcl(name = "manager",
                              description = "manager")})
   @Path("/samples")
   @POST
   public Response createSamples() {
      return null;
   }

}
