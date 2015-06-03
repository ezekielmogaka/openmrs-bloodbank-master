package org.openmrs.module.blooddonationmanager.web.controller.queue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
//import org.openmrs.module.hospitalcore.model.RadiologyDepartment;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.web.controller.DonorEncountersController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller("BloodDonationManagerQueueController")
@RequestMapping("/module/blooddonationmanager/queue.form")
public class QueueController {
	
	/*
	@ModelAttribute("investigations")
	public Set<Concept> getAllInvestigations(){
		RadiologyService rs = (RadiologyService) Context.getService(RadiologyService.class);
		RadiologyDepartment department = rs.getCurrentRadiologyDepartment();
		if(department!=null){
			Set<Concept> investigations = department.getInvestigations();
			return investigations;
		}
		return null;
	}*/

	@RequestMapping(method = RequestMethod.GET)
	public String listForms(
			Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dateStr = sdf.format(new Date());
		model.addAttribute("currentDate", dateStr);
		return "/module/blooddonationmanager/queue/list";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String onSubmit(@RequestParam("orderId") int orderId){
		Order order=Context.getOrderService().getOrder(orderId);	
		int patientId=order.getPatient().getPatientId();
		//order.setDiscontinued(true);
		//Context.getOrderService().saveOrder(order);
		DonorEncountersController.newEncounter(patientId);
		
		return "redirect:/module/blooddonationmanager/showDonorEncounters.form?patientId="+patientId;
	}
}
