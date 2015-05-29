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
package org.openmrs.module.blooddonationmanager.api.db.hibernate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.blooddonationmanager.api.db.BloodDonationManagerDAO;
import org.openmrs.module.blooddonationmanager.api.model.BloodDonationManager;
import org.openmrs.module.blooddonationmanager.api.model.BlooddonationManagerForm;
import org.openmrs.module.blooddonationmanager.api.model.PreparedDonorId;
import org.openmrs.order.OrderUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * It is a default implementation of  {@link BloodDonationManagerDAO}.
 */
public class HibernateBloodDonationManagerDAO implements BloodDonationManagerDAO {
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * Set session factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public String getUniqueId() {
		return UUID.randomUUID().toString();
	}

	public void savePreparedId(PreparedDonorId pdi) {
		this.sessionFactory.getCurrentSession().save(pdi);
	}

	@SuppressWarnings("unchecked")
	public List<PreparedDonorId> getUnusedPreparedId(){
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PreparedDonorId.class);
		criteria.add(Expression.eq("used", false));
		return (List<PreparedDonorId>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<PreparedDonorId> getAllPreparedIds(){
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PreparedDonorId.class);
		return (List<PreparedDonorId>) criteria.list();
	}

	public PreparedDonorId getPrepDonorIdbyIdentifier(String identifier) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PreparedDonorId.class);
		criteria.add(Expression.eq("identifier", identifier));
		return (PreparedDonorId) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<BloodDonationManager> getRecordsByPatient(Patient patient) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BloodDonationManager.class);
		criteria.add(Expression.eq("patient", patient));
		criteria.addOrder(Order.desc("bloodBankId"));
		return (List<BloodDonationManager>) criteria.list();
	}

	public BloodDonationManager getRecordById(int id){
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BloodDonationManager.class);
		criteria.add(Expression.eq("bloodBankId",id));
		return (BloodDonationManager) criteria.uniqueResult();
	}

	public BloodDonationManager getRecordByTest(Encounter encounter){
		System.out.println("line 1"+ encounter.getEncounterId());
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BloodDonationManager.class);
		criteria.add(Expression.eq("test",encounter));
		System.out.println("line 2"+ encounter.getEncounterId());
		return (BloodDonationManager) criteria.uniqueResult();
	}

	public void saveBloodBank(BloodDonationManager bloodBank) {
		sessionFactory.getCurrentSession().saveOrUpdate(bloodBank);
	}

	@SuppressWarnings("unchecked")
	public List<BloodDonationManager> getValidStockRecords() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BloodDonationManager.class);
		criteria.add(Expression.eq("voided", false));
		criteria.add(Expression.eq("disposed", false));
		criteria.add(Expression.isNotNull("questionnaire"));
		criteria.add(Expression.isNotNull("test"));
		criteria.add(Expression.eq("bloodResultComplete",false));
		criteria.add(Expression.eq("testComplete",true));
		criteria.addOrder(Order.asc("bloodBankId"));
		return (List<BloodDonationManager>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<BloodDonationManager> getValidStockByTypeConcept(Concept con) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BloodDonationManager.class);
		criteria.add(Expression.eq("voided", false));
		criteria.add(Expression.eq("disposed", false));
		criteria.add(Expression.isNotNull("questionnaire"));
		criteria.add(Expression.isNotNull("test"));
		//criteria.add(Expression.isNotNull("bloodResult"));
		criteria.add(Expression.eq("questionnaireComplete",true));
		criteria.add(Expression.eq("testComplete",true));
		criteria.add(Expression.eq("bloodResultComplete",false));
		criteria.add(Expression.eq("bloodGroup",con));
		criteria.addOrder(Order.desc("expiryDate"));
		return (List<BloodDonationManager>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<BloodDonationManager> getTestableRecords() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BloodDonationManager.class);
		criteria.add(Expression.eq("voided", false));
		criteria.add(Expression.isNotNull("questionnaire"));
		criteria.add(Expression.eq("questionnaireComplete",true));
		criteria.add(Expression.eq("bloodResultComplete",false));
		criteria.add(Expression.eq("testComplete", false));

		criteria.addOrder(Order.desc("bloodBankId"));
		return (List<BloodDonationManager>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<BlooddonationManagerForm> getBloodbankForms(String conceptName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				BlooddonationManagerForm.class);
		criteria.add(Restrictions.eq("conceptName", conceptName));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<BlooddonationManagerForm> getAllBloodbankForms() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				BlooddonationManagerForm.class);
		return criteria.list();
	}

	public BlooddonationManagerForm getBloodbankFormById(Integer id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				BlooddonationManagerForm.class);
		criteria.add(Restrictions.eq("id", id));
		return (BlooddonationManagerForm) criteria.uniqueResult();
	}

	public BlooddonationManagerForm saveBloodbankForm(BlooddonationManagerForm form) {

		return (BlooddonationManagerForm) sessionFactory.getCurrentSession().merge(form);
	}

	public BloodDonationManager getRecordByResult(Encounter encounter) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BloodDonationManager.class);
		criteria.add(Expression.eq("bloodResult",encounter));
		return (BloodDonationManager) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<org.openmrs.Order> getOrders(Date orderStartDate, String phrase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(org.openmrs.Order.class);
		System.out.println("entered here");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = sdf.format(orderStartDate) + " 00:00:00";
		String endDate = sdf.format(orderStartDate) + " 23:59:59";
		OrderType orderType = null;// = Context.getOrderService().getOrderType(Integer.parseInt(Context.getAdministrationService().getGlobalProperty("registration.bloodbankOrderTypeId")));


		String orderTypeName = Context.getAdministrationService().getGlobalProperty("bloodbank.orderTypeName");

		//orderType= OrderUtil.getOrderTypeByName(orderTypeName);

//		System.out.println(orderType.getName());
		criteria.add(Restrictions.eq("orderType", orderType));
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		try {
			System.out.println(dateTimeFormatter.parse(startDate)+ " to " + dateTimeFormatter.parse(endDate));
			criteria.add(Expression.between("startDate", dateTimeFormatter.parse(startDate), dateTimeFormatter.parse(endDate)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		criteria.add(Restrictions.eq("discontinued", false));

		List<Patient> patients = null;
		if (!StringUtils.isBlank(phrase)) {
			patients = Context.getPatientService().getPatients(phrase);
		}
		if (!CollectionUtils.isEmpty(patients))
			criteria.add(Restrictions.in("patient", patients));

		criteria.setMaxResults(30);
		System.out.println(criteria.list().size());
		criteria.addOrder(org.hibernate.criterion.Order.asc("startDate"));
		return criteria.list();
	}


	public boolean isPatientDonor(Integer patientId) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BloodDonationManager.class);
		Patient patient = Context.getPatientService().getPatient(patientId);
		criteria.add(Restrictions.eq("patient", patient));
		System.out.println("In HBm Dao+ "+criteria.list()+" "+patient);
		if( criteria.list() != null ){
			System.out.println("Returning True");
			return true;
		}
		System.out.println("Returning False");
		return false;
	}
}