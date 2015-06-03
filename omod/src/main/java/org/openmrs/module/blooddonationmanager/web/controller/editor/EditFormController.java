package org.openmrs.module.blooddonationmanager.web.controller.editor;

import org.openmrs.api.context.Context;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.api.model.BlooddonationManagerForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("BloodbankEditFormController")
@RequestMapping("/module/blooddonationmanager/editForm.form")
public class EditFormController {

	@ModelAttribute("form")
	public BlooddonationManagerForm getForm(
			@RequestParam(value = "id", required = false) Integer id) {
		if (id != null) {
			BloodDonationManagerService bbs = (BloodDonationManagerService) Context
					.getService(BloodDonationManagerService.class);
			BlooddonationManagerForm form = bbs.getBloodbankFormById(id);
			if (form != null)
				return form;
		}
		return new BlooddonationManagerForm();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm() {
		return "/module/blooddonationmanager/editor/CKEditor";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveForm(@ModelAttribute("form") BlooddonationManagerForm form,
			Model model) {
		BloodDonationManagerService bbs = (BloodDonationManagerService) Context
				.getService(BloodDonationManagerService.class);
		bbs.saveBloodbankForm(form);
		return "redirect:/module/blooddonationmanager/list.form";
	}
}
