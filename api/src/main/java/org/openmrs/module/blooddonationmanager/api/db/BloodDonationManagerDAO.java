/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.blooddonationmanager.api.db;

import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.api.model.BloodDonationManager;
import org.openmrs.module.blooddonationmanager.api.model.BlooddonationManagerForm;
import org.openmrs.module.blooddonationmanager.api.model.PreparedDonorId;

import java.util.Date;
import java.util.List;

/**
 *  Database methods for {@link BloodDonationManagerService}.
 */
public interface BloodDonationManagerDAO {
	
	/*
	 * Add DAO methods here
	 */
	public void setSessionFactory(SessionFactory sessionFactory);

	public String getUniqueId();

	public void savePreparedId(PreparedDonorId pdi);

	public List<PreparedDonorId> getUnusedPreparedId();

	public PreparedDonorId getPrepDonorIdbyIdentifier(String identifier);

	public List<BloodDonationManager> getRecordsByPatient(Patient patient);

	public BloodDonationManager getRecordById(int id);

	public BloodDonationManager getRecordByTest(Encounter encounter);

	public void saveBloodBank(BloodDonationManager bloodBank);

	public List<BloodDonationManager> getValidStockRecords();

	public List<BloodDonationManager> getTestableRecords();

	public List<BloodDonationManager> getValidStockByTypeConcept(Concept con);

	public List<PreparedDonorId> getAllPreparedIds();

	/**
	 * Get radiology form be concept name
	 *
	 * @param concept
	 * @return
	 */
	public List<BlooddonationManagerForm> getBloodbankForms(String conceptName);

	/**
	 * Get all radiology form
	 *
	 * @return
	 */
	public List<BlooddonationManagerForm> getAllBloodbankForms();

	/**
	 * Get radiology form by id
	 *
	 * @param id
	 * @return
	 */
	public BlooddonationManagerForm getBloodbankFormById(Integer id);

	/**
	 * Save radiology form
	 *
	 * @param form
	 * @return
	 */
	public BlooddonationManagerForm saveBloodbankForm(BlooddonationManagerForm form);

	public BloodDonationManager getRecordByResult(Encounter encounter);

	public List<Order> getOrders(Date date, String phrase);

	public boolean isPatientDonor(Integer patientId);

}