package org.openmrs.module.blooddonationmanager.web.controller.editor;

import java.sql.Date;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.api.model.BloodDonationManager;
import org.openmrs.module.blooddonationmanager.api.model.BlooddonationManagerForm;
//import org.openmrs.module.bloodbank.model.BloodbankTest;
import org.openmrs.module.blooddonationmanager.web.controller.util.BloodDonationManagerUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("BloodbankShowFormController")
@RequestMapping("/module/blooddonationmanager/showForm.form")
public class ShowFormController {

	/**
	 * Show form value
	 * 
	 * @param id
	 * @param mode
	 * @param encounterId
	 * @param radiologyTestId
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showForm(
			@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "mode", required = false) String mode,
			@RequestParam(value = "encounterId", required = false) Integer encounterId,
			@RequestParam(value = "conceptId", required = false) Integer conceptId,
			@RequestParam(value = "radiologyTestId", required = false) Integer radiologyTestId,
			@RequestParam(value = "type", required = false) String type,
			Model model) {
		BloodDonationManagerService bbs = (BloodDonationManagerService) Context.getService(BloodDonationManagerService.class);
		Encounter encounter = null;
		BlooddonationManagerForm form = null;
		/*
		if (radiologyTestId != null) {
			RadiologyTest test = rs.getRadiologyTestById(radiologyTestId);
			form = test.getForm();
			encounter = test.getEncounter();

			model.addAttribute("patientIdentifier", test.getPatient()
					.getPatientIdentifier().getIdentifier());
			model.addAttribute("orderId", test.getOrder().getOrderId());
		} else {
			if (id == 0) { // generate default form

				form = rs.getDefaultForm();
			} else { // get the existing form

				form = rs.getRadiologyFormById(id);
			}

			if (encounterId != null) {
				encounter = Context.getEncounterService().getEncounter(
						encounterId);
			}
		}
		*/
		form = bbs.getBloodbankFormById(id);
		if (encounterId != null)
			encounter = Context.getEncounterService().getEncounter(encounterId);
		
		model.addAttribute("form", form);
		model.addAttribute("mode", mode);
		if (encounter != null)
			model.addAttribute("encounterId", encounter.getEncounterId());
		
		BloodDonationManagerUtil.generateDataFromEncounter(model, encounter, form);
		model.addAttribute("contents", form.getContent());
		return "/module/blooddonationmanager/editor/show";
	}

	/**
	 * Save form values into an existing encounter
	 * 
	 * @param request
	 * @param encounterId
	 *            Id of an existing encounter which should be filled necessary
	 *            information before such as patient, user...
	 * @param model
	 * @return
	 */
	
	@RequestMapping(method = RequestMethod.POST)
	public String saveEncounter(HttpServletRequest request,
			@RequestParam("encounterId") Integer encounterId, Model model) {
		
		Map<String, String> parameters = buildParameterList(request);
		Encounter encounter = Context.getEncounterService().getEncounter(
				encounterId);
		if (encounter != null) {
			for (String key : parameters.keySet()) {
				Concept concept = BloodDonationManagerUtil.searchConcept(key);
				Obs obs = insertValue(encounter, concept, parameters.get(key));
				if (obs.getId() == null)
					encounter.addObs(obs);
			}
			Context.getEncounterService().saveEncounter(encounter);
			System.out.println("Encounter Name = "+encounter.getEncounterType().getName());
			System.out.println("Encounter ID = "+ encounter.getEncounterId());
			System.out.println("Encounter Type ID = "+ encounter.getEncounterType().getId());
			String encName=new String("BBTESTENCOUNTER");
			System.out.println("sadkgajkgkjdga---------------------------------------"+Integer.valueOf(Context.getAdministrationService().getGlobalProperty("bloodbank.test.enctype.id")));
			if(encounter.getEncounterType().getId().toString().equalsIgnoreCase((Context.getAdministrationService().getGlobalProperty("bloodbank.test.enctype.id")))){
				System.out.print("====lallu the 2nd====");
				setTestValues(encounter,parameters);
			}
			if(encounter.getEncounterType().getId().toString().equalsIgnoreCase(Context.getAdministrationService().getGlobalProperty("bloodbank.result.enctype.id"))){
				setResultValues(encounter,parameters);
			}
			model.addAttribute("status", "success");
			
			return "/module/bloodbank/editor/enterForm";
		}
		model.addAttribute("status", "fail");
		return "/module/bloodbank/editor/enterForm";
	}
	
	private void setTestValues(Encounter encounter,Map <String,String> parameters){
		System.out.println("Entered Here");
		BloodDonationManagerService service = Context.getService(BloodDonationManagerService.class);
		System.out.println("eID"+encounter.getEncounterId());
		BloodDonationManager bld =  service.getRecordByTest(encounter);
		System.out.println("Encounter Id = " + bld.getTest().getId());
		bld.setTestComplete(true);
		boolean flag = false;
		Calendar currentDate = Calendar.getInstance();
		
		for (String key : parameters.keySet()) {
			if(key.equalsIgnoreCase("blood typing")){
				Concept concept = BloodDonationManagerUtil.searchConcept(parameters.get(key));
				bld.setBloodGroup(concept);
				System.out.println("Entered Here blood grup "+ concept);
			}
			System.out.println("Key = " + key + " Value" + parameters.get(key));
			if(parameters.get(key).equalsIgnoreCase("yes")){
				
				bld.setVoided(true);
				bld.setVoidReason(key);
				bld.setVoidedBy(Context.getUserContext().getAuthenticatedUser());
				bld.setDateVoided(currentDate.getTime());
				flag=true;
				break;
			}
		}
		if(flag==false){
			bld.setStorageDate(currentDate.getTime());
			currentDate.add(Calendar.MONTH, 3);
			bld.setExpiryDate(currentDate.getTime());
			//CURRENTLY STORING THE WRONG DATE
			
			Encounter enc=new Encounter();
			enc.setCreator(Context.getAuthenticatedUser());
			enc.setDateCreated(new java.util.Date());
	        Location loc = Context.getLocationService().getLocation(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("bloodbank.location.id")));
	        enc.setLocation(loc);
			enc.setPatient(encounter.getPatient());
			enc.setPatientId(encounter.getPatient().getPatientId());
			EncounterType encounterType =  Context.getEncounterService().getEncounterType(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("bloodbank.result.enctype.id")));
			enc.setEncounterType(encounterType);
			Form form = Context.getFormService().getForm(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("bloodbank.result.formId")));
			enc.setVoided(false);
			enc.setProvider(Context.getAuthenticatedUser().getPerson());
			enc.setUuid(UUID.randomUUID().toString());
			enc.setForm(form);
			enc.setEncounterDatetime(new java.util.Date());
			Context.getEncounterService().saveEncounter(enc);
			bld.setBloodResult(enc);
		}
		service.saveBloodBank(bld);
	}
	
	private void setResultValues(Encounter encounter,Map <String,String> parameters){
		System.out.println("Entered Here");
		BloodDonationManagerService service = Context.getService(BloodDonationManagerService.class);
		BloodDonationManager bld =  service.getRecordByResult(encounter);
		System.out.println("Encounter Id = " + bld.getTest().getId());
		bld.setBloodResultComplete(true);
		service.saveBloodBank(bld);
	}

	
	
	@SuppressWarnings("rawtypes")
	private Map<String, String> buildParameterList(HttpServletRequest request) {
		Map<String, String> parameters = new HashMap<String, String>();
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String parameterName = (String) e.nextElement();
			if (!parameterName.equalsIgnoreCase("id"))
				if (!parameterName.equalsIgnoreCase("mode"))
					if (!parameterName.equalsIgnoreCase("encounterId"))
						if (!parameterName.equalsIgnoreCase("redirectUrl"))
							parameters.put(parameterName,
									request.getParameter(parameterName));

		}
		return parameters;
	}
	
	@SuppressWarnings("deprecation")
	private Date ConverttoDate( String date )
	{
		if(date.contains("/")){
			String[] DATE = date.split("/");
			return new Date( Integer.parseInt(DATE[2])-1900 , Integer.parseInt(DATE[1])-1 , Integer.parseInt(DATE[0]) );
		}
		else{
			String[] DATE = date.split("-");
			return new Date( Integer.parseInt(DATE[0])-1900 , Integer.parseInt(DATE[1])-1 , Integer.parseInt(DATE[2]) );
		}
		
	}
	
	private Obs insertValue(Encounter encounter, Concept concept, String value) {

		Obs obs = getObs(encounter, concept);
		obs.setConcept(concept);
		if (concept.getDatatype().getName().equalsIgnoreCase("Text")) 
		{
			value = value.replace("\n", "\\n");
			obs.setValueText(value);
		}
		
		else if( concept.getDatatype().getName().equalsIgnoreCase("Date") )
		{
			if(!value.isEmpty()){
				obs.setValueDatetime(ConverttoDate(value));
			}
		}
		
		else if (concept.getDatatype().getName().equalsIgnoreCase("Numeric"))
		{
			if(!value.isEmpty()){
				obs.setValueNumeric(new Double(value));
			}
			
		} else if (concept.getDatatype().getName().equalsIgnoreCase("Coded")) {
			Concept answerConcept = BloodDonationManagerUtil.searchConcept(value);
			obs.setValueCoded(answerConcept);
		}
		return obs;
	}
	
	private Obs getObs(Encounter encounter, Concept concept) {
		for (Obs obs : encounter.getAllObs()) {
			if (obs.getConcept().equals(concept))
				return obs;
		}
		return new Obs();
	}

}
