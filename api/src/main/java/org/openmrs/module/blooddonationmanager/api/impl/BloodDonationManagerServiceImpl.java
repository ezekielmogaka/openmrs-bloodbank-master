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
package org.openmrs.module.blooddonationmanager.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.blooddonationmanager.api.BloodDonationManagerService;
import org.openmrs.module.blooddonationmanager.api.db.BloodDonationManagerDAO;
import org.openmrs.module.blooddonationmanager.api.model.BloodDonationManager;
import org.openmrs.module.blooddonationmanager.api.model.BlooddonationManagerForm;
import org.openmrs.module.blooddonationmanager.api.model.PreparedDonorId;

import java.util.Date;
import java.util.List;

/**
 * It is a default implementation of {@link BloodDonationManagerService}.
 */
public class BloodDonationManagerServiceImpl extends BaseOpenmrsService implements BloodDonationManagerService {

    private final Log log = LogFactory.getLog(getClass());
    private BloodDonationManagerDAO dao;

    public void setDao(BloodDonationManagerDAO dao) {
        this.dao = dao;
    }

    public String getUniqueId() {
        return getDao().getUniqueId();
    }

    public void savePreparedId(PreparedDonorId pdi) {
        getDao().savePreparedId(pdi);
    }

    public PreparedDonorId getPrepDonorIdbyIdentifier(String identifier) {
        return getDao().getPrepDonorIdbyIdentifier(identifier);
    }

    public List<PreparedDonorId> getAllPreparedIds(){
        return getDao().getAllPreparedIds();
    }

    public List<PreparedDonorId> getUnusedPreparedId() {
        return getDao().getUnusedPreparedId();
    }

    public List<BloodDonationManager> getRecordsByPatient(Patient patient) {
        return getDao().getRecordsByPatient(patient);
    }

    public void saveBloodBank(BloodDonationManager bloodBank) {
        getDao().saveBloodBank(bloodBank);
    }

    public List<BloodDonationManager> getValidStockRecords() {
        return getDao().getValidStockRecords();
    }

    public List<BloodDonationManager> getTestableRecords() {
        return getDao().getTestableRecords();
    }

    public List<BloodDonationManager> getValidStockByTypeConcept(Concept con) {
        return getDao().getValidStockByTypeConcept(con);
    }

    public BloodDonationManager getRecordById(int id) {
        return getDao().getRecordById(id);
    }

    public BloodDonationManager getRecordByTest(Encounter encounter){
        return getDao().getRecordByTest(encounter);
    }


    public BloodDonationManagerDAO getDao() {
        return dao;
    }

    public List<BlooddonationManagerForm> getAllBloodbankForms() {
        return dao.getAllBloodbankForms();
    }

    public List<BlooddonationManagerForm> getBloodbankForms(String conceptName) {
        return dao.getBloodbankForms(conceptName);
    }

    public BlooddonationManagerForm getBloodbankFormById(Integer id) {
        return dao.getBloodbankFormById(id);
    }

    public BlooddonationManagerForm saveBloodbankForm(BlooddonationManagerForm form) {
        return dao.saveBloodbankForm(form);
    }

    public BloodDonationManager getRecordByResult(Encounter encounter) {
        return dao.getRecordByResult(encounter);
    }

    public List<Order> getOrders(Date date, String phrase) {
        System.out.println("Orders created");
        return dao.getOrders(date, phrase);

    }

    public boolean isPatientDonor(Integer PatientId) {
        System.out.println("Reached ServiceImpl");
        return dao.isPatientDonor(PatientId);
    }
}