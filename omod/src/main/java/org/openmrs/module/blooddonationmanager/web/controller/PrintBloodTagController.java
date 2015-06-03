package org.openmrs.module.blooddonationmanager.web.controller;

import java.util.ArrayList;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.api.model.BloodDonationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/module/blooddonationmanager/printBloodTag.form")
public class PrintBloodTagController {
	

	@RequestMapping(method = RequestMethod.GET)
	public String printBloodTag(ModelMap model, @RequestParam("bbId") int bbId){
		
		
		BloodDonationManagerService bbs = Context.getService(BloodDonationManagerService.class);
		String donorIdientifier = Context.getAdministrationService().getGlobalProperty("blooddonationmanager.donorIdTypeId");
		BloodDonationManager encounter = bbs.getRecordById(bbId);

		
//		model.addAttribute("donorId", bbs.getRecordById(bbId).getPatient().getPatientIdentifier(Integer.valueOf(donorIdientifier)));
		model.addAttribute("patientId", encounter.getPatient().getPatientIdentifier() );
		model.addAttribute("bb", encounter);
		
		String bg = encounter.getBloodGroup().getName().toString();
		bg = bg.replace("NEGATIVE", "-");
		bg = bg.replace("POSITIVE", "+");
		model.addAttribute("bloodType", bg);
		
		ArrayList<String> tests =  new ArrayList<String>();
		for (Obs obs : encounter.getTest().getAllObs()) {
				if (obs.getConcept().getDatatype().isBoolean() && obs.getValueAsBoolean() == false){
		        String s = obs.getConcept().getName().toString();
		        tests.add(s);
			}
        }
		model.addAttribute("br", tests);
		
		return "/module/blooddonationmanager/printBloodTag";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(){
		return "/module/blooddonationmanager/printBloodTag";
	}

}
