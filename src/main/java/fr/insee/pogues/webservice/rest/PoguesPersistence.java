package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.config.auth.UserProvider;
import fr.insee.pogues.config.auth.user.User;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.persistence.service.VariablesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * WebService class for the Instrument Persistence
 * 
 * See the swagger documentation for this service :
 * http://inseefr.github.io/Pogues/en/remote-apis/swagger.html
 * 
 * @author I6VWID
 * 
 *         schemes: - http
 * 
 *         consumes: - application/json
 * 
 *         produces: - application/json
 *
 */
@RestController
@RequestMapping("/api/persistence")
@Tag(name = "Pogues Persistence")
@SecurityRequirement(name = "bearerAuth")
public class PoguesPersistence {

	static final Logger logger = LogManager.getLogger(PoguesPersistence.class);

    @Autowired
	private QuestionnairesService questionnaireService;
    
    @Autowired
	private VariablesService variablesService;
    
    @Autowired
    private Environment env;

	@Autowired
	private UserProvider userProvider;

	private static final String IDQUESTIONNAIRE_PATTERN="[a-zA-Z0-9]*";
	private static final String BAD_REQUEST = "Bad Request";
    private static final String MESSAGE_INVALID_IDENTIFIER = "Identifier %s is invalid";

	@GetMapping("questionnaire/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId  = "getQuestionnaires",
	        summary = "Get questionnaire",
            description = "Gets the questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })	
	public ResponseEntity<Object> getQuestionnaire(
			@PathVariable(value = "id") String id
	) throws Exception {
			JSONObject result = questionnaireService.getQuestionnaireByID(id);
			return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
    @GetMapping("questionnaire/json-lunatic/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "getJsonLunatic",
	        summary = "Get questionnaire",
            description = "Gets the questionnaire with id JsonLunatic {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> getJsonLunatic(
			@PathVariable(value = "id") String id
	) throws Exception {
		try {
			JSONObject result = questionnaireService.getJsonLunaticByID(id);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

	}

    @GetMapping("questionnaires/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
    		operationId = "searchQuestionnaires",
            summary = "Search questionnaires",
            description = "Search questionnaires matching query params"
    )
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "400", description = "Bad request")
	})
    public ResponseEntity<Object> searchQuestionnaires(
            @RequestParam("owner") String owner
    ) throws Exception {
        try {
			List<JSONObject> questionnaires = new ArrayList<>();
            if(null != owner){
                questionnaires.addAll(questionnaireService.getQuestionnairesByOwner(owner));
            }
            return ResponseEntity.status(HttpStatus.OK).body(questionnaires);
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw e;
        }
    }
    
	@GetMapping("questionnaires/search/meta")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "searchQuestionnairesMetadata",
	        summary = "Get questionnaires' metadata",
            description = "Get questionnaires' metadata matching query params"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
	public ResponseEntity<Object> getQuestionnairesMetadata(
            @RequestParam("owner") String owner
	) throws Exception {
		try {
			List<JSONObject> questionnairesMetadata = new ArrayList<>();
            if(null != owner){
                questionnairesMetadata.addAll(questionnaireService.getQuestionnairesMetadata(owner));
            }
            return ResponseEntity.status(HttpStatus.OK).body(questionnairesMetadata);
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw e;
        }
	}
	
	@GetMapping("questionnaires/stamps")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "searchQuestionnairesStamps",
	        summary = "Get stamps in database",
            description = "Get stamps with at least one questionnaire saved in database"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
	public ResponseEntity<Object> getQuestionnaireStamps() throws Exception {
		try {
			List<JSONObject> questionnairesStamps = new ArrayList<>();
			questionnairesStamps.addAll(questionnaireService.getQuestionnairesStamps());
            return ResponseEntity.status(HttpStatus.OK).body(questionnairesStamps);
        } catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw e;
        }
	}
	

	@DeleteMapping("questionnaire/{id}")
	@Operation(
			operationId = "deleteQuestionnaire",
	        summary = "Delete questionnaire",
            description = "Delete questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> deleteQuestionnaire(Authentication auth,
			@PathVariable(value = "id") String id
	) throws Exception {
		try {
			questionnaireService.deleteQuestionnaireByID(id);
			User user = userProvider.getUser(auth);
			logger.info("Questionnaire {} deleted by {}", id, user.getName());
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (PoguesException e) {
				logger.error(e.getMessage(), e);
				return ResponseEntity.status(e.getStatus()).body(e.getDetails());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GetMapping("questionnaire/{id}/variables")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId  = "getQuestionnaireVariables",
			summary = "Get the variables of a questionnaire, used for pogues frontend",
			description = "Gets the variables with questionnaire id {id}",
			responses = {
					@ApiResponse(content = @Content(mediaType = "application/json"))}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<Object> getQuestionnaireVariables(
			@PathVariable(value = "id") String id
	) throws Exception {
		try {
			String result = variablesService.getVariablesByQuestionnaire(id);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GetMapping("questionnaire/{id}/vars")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId  = "getQuestionnaireVars",
			summary = "Get the variables of a questionnaire",
			description = "Gets the variables with questionnaire id {id}",
			responses = {
					@ApiResponse(content = @Content(mediaType = "application/json"))}
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<JSONArray> getVariables(
			@PathVariable(value = "id") String id
	) throws Exception {
		try {
			JSONArray result = variablesService.getVariablesByQuestionnaireForPublicEnemy(id);
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@DeleteMapping("questionnaire/json-lunatic/{id}")
	@Operation(
			operationId = "deleteJsonLunatic",
	        summary = "Delete Json Lunatic of a questionnaire",
            description = "Delete the Json Lunatic representation of a  questionnaire with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
//	@OwnerRestricted
	public ResponseEntity<Object> deleteJsonLunatic(
			@PathVariable(value = "id") String id
	) throws Exception {
		try {
			questionnaireService.deleteJsonLunaticByID(id);
			logger.info("Questionnaire {} deleted", id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@GetMapping("questionnaires")
    @Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "getQuestionnaireList",
	        summary = "Get questionnaires",
            description = "Gets the `QuestionnaireList` object"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> getQuestionnaireList() throws Exception {
		try {
			List<JSONObject> questionnaires = questionnaireService.getQuestionnaireList();
			return ResponseEntity.status(HttpStatus.OK).body(questionnaires);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@PutMapping("questionnaire/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "updateQuestionnaire",
	        summary = "Update questionnaire",
            description = "Update a `Questionnaire` object with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> updateQuestionnaire(
			@PathVariable(value = "id") String id,
			@RequestBody JSONObject jsonContent
	) throws Exception {
        try {
			if (id.matches(IDQUESTIONNAIRE_PATTERN)) {
				questionnaireService.updateQuestionnaire(id, jsonContent);
				logger.info("Questionnaire {} updated", id);
			} else {
				throw new PoguesException(400,BAD_REQUEST,String.format(MESSAGE_INVALID_IDENTIFIER,id));
			}
        } catch (PoguesException e) {
			logger.error(e.getMessage(), e);
			return ResponseEntity.status(e.getStatus()).body(e.getDetails());
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            throw e;
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PutMapping("questionnaire/json-lunatic/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "updateJsonLunatic",
	        summary = "Update Json Lunatic",
            description = "Update Json Lunatic of a `Questionnaire` object with id {id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
	public ResponseEntity<Object> updateJsonLunatic(
			@PathVariable(value = "id") String id,
			@RequestBody JSONObject jsonLunatic
	) throws Exception {
        try {
			questionnaireService.updateJsonLunatic(id, jsonLunatic);
			logger.info("Json Lunatic of questionnaire {} updated", id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            throw e;
        }
	}

	@PostMapping("questionnaires")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "createQuestionnaire",
	        summary = "Create Questionnaire",
            description = "Creates a new `Questionnaire`"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Entity already exists")
    })
	public ResponseEntity<Object> createQuestionnaire(
			@RequestBody JSONObject jsonContent
	) throws Exception {
        try {
        	questionnaireService.createQuestionnaire(jsonContent);
			String id = (String) jsonContent.get("id");
			if (id.matches(IDQUESTIONNAIRE_PATTERN)) {
				String dbHost = env.getProperty("fr.insee.pogues.persistence.database.host");
				String apiName = env.getProperty("fr.insee.pogues.api.name");
				String uriQuestionnaire = String.format("http://%s%s/api/persistence/questionnaire/%s",dbHost,apiName,id);
				logger.debug("New questionnaire created , uri : {}",uriQuestionnaire);
				return ResponseEntity.status(HttpStatus.CREATED).header("Location", uriQuestionnaire).build();
    		} else {
    			throw new PoguesException(400,BAD_REQUEST,String.format(MESSAGE_INVALID_IDENTIFIER,id));
    		}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@PostMapping("questionnaires/json-lunatic")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
			operationId = "createJsonLunatic",
	        summary = "Create Json Lunatic of questionnaire",
            description = "Creates a new Json Lunatic entry"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Entity already exists")
    })
	public ResponseEntity<Object> createJsonLunatic(
			@RequestBody JSONObject jsonContent
	) throws Exception {
        try {
			questionnaireService.createJsonLunatic(jsonContent);
			String id = (String) jsonContent.get("id");
			if (id.matches(IDQUESTIONNAIRE_PATTERN)) {
				String dbHost = env.getProperty("fr.insee.pogues.persistence.database.host");
				String apiName = env.getProperty("fr.insee.pogues.api.name");
				String uriJsonLunaticQuestionnaire = String.format("http://%s%s/api/persistence/questionnaire/json-lunatic/%s",dbHost,apiName,id);
				logger.debug("New Json Lunatic created , uri : {}", uriJsonLunaticQuestionnaire);
				return ResponseEntity.status(HttpStatus.CREATED).header("Location", uriJsonLunaticQuestionnaire).build();
			} else {
				throw new PoguesException(400,BAD_REQUEST,String.format(MESSAGE_INVALID_IDENTIFIER,id));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@ExceptionHandler(PoguesException.class)
	public ResponseEntity<ApiError> handlePoguesException(PoguesException pe) {
		logger.error(pe.getMessage(), pe);
		ApiError apiErrorResponse = new ApiError(pe.getStatus(), pe.getMessage(), pe.getDetails());
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.valueOf(pe.getStatus()));
	}
}
