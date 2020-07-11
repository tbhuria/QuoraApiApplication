package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    QuestionBusinessService questionBusinessService;

    //**createQuestion**//
    //This endpoint is used to create Question in the QuoraApplication. Any user can go and access this endpoint and create a question
    //This endpoint requests for the attributes in QuestionRequest and accessToken in the authorization header
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final QuestionEntity questionEntity = new QuestionEntity();
        //the attribute content is set from QuestionRequest , generation of UUID is done using randomUUID() and date is set as current date-time
        // of the creation of Question
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        //The accesstoken can be of any form, Bearer<accessToken> or <accessToken>
        QuestionEntity createdQuestionEntity;
        try {
            String[] bearerAccessToken = authorization.split("Bearer ");
            createdQuestionEntity = questionBusinessService.postQuestion(questionEntity, bearerAccessToken[1]);
        } catch (ArrayIndexOutOfBoundsException are) {
            createdQuestionEntity = questionBusinessService.postQuestion(questionEntity, authorization);
        }
        //The id(questionUUID) and status message(QUESTION CREATED) are returned on successfull creation of the questionEntity
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        //This method returns QuestionResponse Object along with httpStatus
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }
}


