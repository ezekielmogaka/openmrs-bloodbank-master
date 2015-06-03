package org.openmrs.module.blooddonationmanager.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
//import org.openmrs.module.blooddonationmanager.api.model.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.api.model.BloodDonationManager;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/module/blooddonationmanager/viewStock.form")
public class ViewStockController{

	Log log = LogFactory.getLog(getClass());

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam(value="dispose", required=false) String bbid){
		System.out.println("Entered FFFFFFFFFFFFFFFFFFFFFFF");
		if (bbid != null){
			System.out.println("Entered HHHHHHHHHHHHHHHHHHHHHH");
			if(bbid != ""){
				int id = Integer.valueOf(bbid);
				BloodDonationManagerService bbs = Context.getService(BloodDonationManagerService.class);
				BloodDonationManager bb = bbs.getRecordById(id);
				System.out.println("Dispose bb encounter: " + id);
				log.info("Dispose bb encounter: " + id);
				bb.setDisposed(true);
				bbs.saveBloodBank(bb);
			}
		}
		return "redirect:/module/blooddonationmanager/viewStock.form";
	}

	@RequestMapping(method = RequestMethod.GET)
	public String initForm(Model model){


		BloodDonationManagerService service = Context.getService(BloodDonationManagerService.class);
		
		List<BloodDonationManager> records = service.getValidStockRecords();
		List<String> bloodGroupNames = new ArrayList<String>();
		Map<String, List<BloodDonationManager>> bloodGroups = new HashMap<String, List<BloodDonationManager>>();
		Map<Patient, PatientIdentifier> donorId = new HashMap<Patient, PatientIdentifier>();

		Concept concept = Context.getConceptService().getConcept(Context.getAdministrationService().getGlobalProperty("blooddonationmanager.blood.group.concept"));
		
		String donorIdientifier = Context.getAdministrationService().getGlobalProperty("blooddonationmanager.donorIdTypeId");
		
		for(ConceptAnswer conceptAnswer : concept.getAnswers()){
			Concept con = conceptAnswer.getAnswerConcept();
			bloodGroups.put(con.getName().getName(), service.getValidStockByTypeConcept(con));
			bloodGroupNames.add(con.getName().getName());
		}
		
		Comparator<String> c = new Comparator<String>(){
		    public int compare(String s1, String s2) {
		    	if(s1.split(" ").length > 1 && s2.split(" ").length > 1){
		            return (s1.split(" ")[1].compareTo(s2.split(" ")[1]))*-1;
		    	}else{
		    		return -1;
		    	}
		    }
		};
		
		Collections.sort(bloodGroupNames, c);
		
		for(BloodDonationManager encounter : records){
			
			donorId.put(encounter.getPatient(), encounter.getPatient().getPatientIdentifier());
				
			if(encounter.getExpiryDate().before(new Date())){
				encounter.setExpired(true);
				service.saveBloodBank(encounter);
			}else{
				encounter.setExpired(false);
				service.saveBloodBank(encounter);
			}
			
			if(!encounter.getBloodResultComplete()){
				if((encounter.getBloodResult()!=null 
						&& encounter.getBloodResult().getObs()!=null ? encounter.getBloodResult().getObs().size(): 0)>
				Integer.valueOf(Context.getAdministrationService().getGlobalProperty("blooddonationmanager.result.valid.count"))
				){
					encounter.setBloodResultComplete(true);
					
					Encounter enc = new Encounter();
					enc.setCreator(Context.getAuthenticatedUser());
					enc.setDateCreated(new Date());
					Location loc = Context.getLocationService().getLocation(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("blooddonationmanager.location.id")));
					enc.setLocation(loc);
					enc.setPatient(encounter.getPatient());
					enc.setPatientId(encounter.getPatient().getPatientId());
					EncounterType encounterType =  Context.getEncounterService().getEncounterType(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("blooddonationmanager.question.enctype.id")));
					enc.setEncounterType(encounterType);
					Form form = Context.getFormService().getForm(Integer.valueOf(Context.getAdministrationService().getGlobalProperty("blooddonationmanager.question.formId")));
					enc.setVoided(false);
					enc.setProvider(Context.getAuthenticatedUser().getPerson());
					enc.setUuid(UUID.randomUUID().toString());
					enc.setForm(form);
					enc.setEncounterDatetime(new Date());
					Context.getEncounterService().saveEncounter(enc);
									
					BloodDonationManager bb = new BloodDonationManager();
					bb.setQuestionnaire(enc);
					bb.setDateCreated(new Date());
					bb.setQuestionnaireProvider(Context.getAuthenticatedUser());
					bb.setPatient(encounter.getPatient());
					bb.setQuestionnaireComplete(false);
					bb.setTestComplete(false);
					bb.setBloodResultComplete(false);
					
					service.saveBloodBank(bb);
					List<BloodDonationManager> newMap = bloodGroups.get(encounter.getBloodGroup().getName().toString());
					newMap.remove(encounter);
					bloodGroups.put(encounter.getBloodGroup().getName().toString(), newMap);
				}
			}
		}
		model.addAttribute("bloodGroups", bloodGroups);
		model.addAttribute("bloodGroupsNames", bloodGroupNames);
		model.addAttribute("donorId", donorId);

		return "/module/blooddonationmanager/viewStock";
	}
}