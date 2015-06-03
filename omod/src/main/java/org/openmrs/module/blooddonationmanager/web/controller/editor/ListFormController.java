package org.openmrs.module.blooddonationmanager.web.controller.editor;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.api.model.BlooddonationManagerForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("sdfsdgdsf")
@RequestMapping("/module/blooddonationmanager/list.form")
public class ListFormController {
		
	@ModelAttribute("forms")
	public List<BlooddonationManagerForm> getAllForms(){
		BloodDonationManagerService bbs = (BloodDonationManagerService) Context.getService(BloodDonationManagerService.class);
		return bbs.getAllBloodbankForms();
	}
	

	@RequestMapping(method = RequestMethod.GET)
	public String listForms(
			Model model) {
		return "/module/blooddonationmanager/editor/list";
	}
}
