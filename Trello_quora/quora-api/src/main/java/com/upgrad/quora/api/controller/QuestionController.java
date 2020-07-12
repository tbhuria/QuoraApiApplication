package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.*;

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

    //**getAllQuestions**//
    //This endpoint is see all the questions posted in the quora application
    //This endpoint is accessed by any user by just providing the access token as input in the authorization header
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        java.util.List<QuestionEntity> listOfQuestions = new ArrayList<>();
        try {
            String[] bearerAccessToken = authorization.split("Bearer ");
            listOfQuestions = questionBusinessService.getAllQuestions(bearerAccessToken[1]);
        } catch (ArrayIndexOutOfBoundsException are) {
            listOfQuestions = questionBusinessService.getAllQuestions(authorization);
        }

        // QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
        // ListIterator<QuestionEntity> questions = listOfQuestions.listIterator();
        java.util.List<QuestionDetailsResponse> displayQuestionIdAndContent = new ArrayList<>();
        for (QuestionEntity question : listOfQuestions) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse().id(question.getUuid()).
                    content(question.getContent());

            displayQuestionIdAndContent.add(questionDetailsResponse);
        }
        //This method returns QuestionDetailsResponse object along with httpStatus
        return new ResponseEntity<List<QuestionDetailsResponse>>(displayQuestionIdAndContent, HttpStatus.OK);
    }
    //**editQuestionContent**//
    //This method only allows the owner of the question to edit a question
    //To edit a question, this endpoint takes in the questionUuid, access token and the content to be updated from the editRequest.

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization, final QuestionEditRequest editRequest) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity;
        QuestionEntity editedQuestion;
        try {
            String[] userToken = authorization.split("Bearer ");
            questionEntity = questionBusinessService.getQuestion(questionUuid, userToken[1]);
            questionEntity.setContent(editRequest.getContent());
            editedQuestion = questionBusinessService.editQuestion(questionEntity, userToken[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            questionEntity = questionBusinessService.getQuestion(questionUuid, authorization);
            questionEntity.setContent(editRequest.getContent());
            editedQuestion = questionBusinessService.editQuestion(questionEntity, authorization);
        }

        //In normal cases, updating an entity doesn't change the Uuid, meaning questionUuid==updatedUuid.
        // However, we have implemented this feature in case the system later requires to keep track of the updates, for e.g. by adding a suffix after every update like Uuid-1,-2, etc.

        String updatedUuid = editedQuestion.getUuid();
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedUuid).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    //**deleteQuestion**//
    //The admin or the owner of the Question has a privilege of deleting the question
    //This endpoint requests for the questionUuid to be deleted and the questionowner or admin accesstoken in the authorization header
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionIdUuid, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {
        String uuid;
        try {
            String[] accessToken = authorization.split("Bearer");
            uuid = questionBusinessService.deleteQuestion(questionIdUuid, accessToken[1]);
        } catch (ArrayIndexOutOfBoundsException are) {
            uuid = questionBusinessService.deleteQuestion(questionIdUuid, authorization);
        }

        QuestionDeleteResponse authorizedDeleteResponse = new QuestionDeleteResponse().id(uuid).status("QUESTION DELETED");
        //This method returns an object of QuestionDeleteResponse and HttpStatus
        return new ResponseEntity<QuestionDeleteResponse>(authorizedDeleteResponse, HttpStatus.OK);

    }
    
}


