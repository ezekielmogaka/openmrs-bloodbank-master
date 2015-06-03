package org.openmrs.module.blooddonationmanager.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.api.model.PreparedDonorId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/module/blooddonationmanager/validateDonorId.form")
public class ValidateExistingDonorIdController{

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	
	@RequestMapping(method = RequestMethod.GET)
	public String init(ModelMap map, @RequestParam("donorPrepId") String id){
		BloodDonationManagerService service = (BloodDonationManagerService)Context.getService(BloodDonationManagerService.class);
		PreparedDonorId predDonorId = service.getPrepDonorIdbyIdentifier( id );
		
		if(predDonorId!=null){
			map.addAttribute("donorId", "yes");
		}else{
			map.addAttribute("donorId", "");
		}
		
		return "/module/blooddonationmanager/validateDonorId";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(ModelMap map, @RequestParam("donorPrepId") String id){
		BloodDonationManagerService service = (BloodDonationManagerService)Context.getService(BloodDonationManagerService.class);
		PreparedDonorId predDonorId = service.getPrepDonorIdbyIdentifier( id );
		
		if(predDonorId!=null){
			map.addAttribute("donorId", "yes");
		}else{
			map.addAttribute("donorId", "");
		}
		
		return "redirect:/module/blooddonationmanager/validateDonorId.form?donorPrepId="+id;
	}

    
}
