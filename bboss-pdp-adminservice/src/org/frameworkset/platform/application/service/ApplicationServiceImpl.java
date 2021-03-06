/**
 *  Copyright 2008-2010 biaoping.yin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.frameworkset.platform.application.service;

import com.frameworkset.common.poolman.ConfigSQLExecutor;
import com.frameworkset.orm.transaction.TransactionManager;
import com.frameworkset.util.ListInfo;
import org.apache.commons.lang.StringUtils;
import org.frameworkset.platform.application.entity.Application;
import org.frameworkset.platform.application.entity.ApplicationCondition;
import org.frameworkset.security.ecc.SimpleKeyPair;
import org.frameworkset.web.auth.ApplicationSecretEncrpy;
import org.frameworkset.web.token.TokenHelper;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>Title: ApplicationServiceImpl</p> <p>Description: 应用管理管理业务处理类 </p>
 * <p>bboss</p> <p>Copyright (c) 2007</p> @Date 2016-11-24 15:35:15 @author
 * yinbp @version v1.0
 */
public class ApplicationServiceImpl implements ApplicationService {

	private static org.slf4j.Logger log = LoggerFactory
			.getLogger(org.frameworkset.platform.application.service.ApplicationServiceImpl.class);

	private ConfigSQLExecutor executor;
	public void addApplication(Application application) throws ApplicationException {
		// 业务组件
		try {
			if(StringUtils.isNotEmpty(application.getAppSecret())){
    			
					application.setAppSecretText(application.getAppSecret());
    			
					application.setAppSecret(ApplicationSecretEncrpy.encodePassword(application.getAppSecret()));
    		}else if(StringUtils.isNotEmpty(application.getAppSecretText())){
    			
    			application.setAppSecret(ApplicationSecretEncrpy.encodePassword(application.getAppSecretText()));
    			
    		}
			executor.insertBean("addApplication", application);
		} catch (Throwable e) {
			throw new ApplicationException("add Application failed:", e);
		}

	}
	public void deleteApplication(String appId) throws ApplicationException {
		try {
			executor.delete("deleteByKey", appId);
		} catch (Throwable e) {
			throw new ApplicationException("delete Application failed::appId=" + appId, e);
		}

	}
	public void deleteBatchApplication(String... appIds) throws ApplicationException {
		TransactionManager tm = new TransactionManager();
		try {
			tm.begin();
			executor.deleteByKeys("deleteByKey", appIds);
			tm.commit();
		} catch (Throwable e) {

			throw new ApplicationException("batch delete Application failed::appIds=" + appIds, e);
		} finally {
			tm.release();
		}

	}
	public void updateApplication(Application application) throws ApplicationException {
		try {
			if(StringUtils.isNotEmpty(application.getAppSecret())){
    			
					application.setAppSecretText(application.getAppSecret());
				
					application.setAppSecret(ApplicationSecretEncrpy.encodePassword(application.getAppSecret()));
			}else if(StringUtils.isNotEmpty(application.getAppSecretText())){
				
				application.setAppSecret(ApplicationSecretEncrpy.encodePassword(application.getAppSecretText()));
				
			}
			executor.updateBean("updateApplication", application);
		} catch (Throwable e) {
			throw new ApplicationException("update Application failed::", e);
		}

	}
	public Application getApplication(String appId) throws ApplicationException {
		return _getApplication(appId,true);

	}
	
	public Application getApplicationWithNoKey(String appId) throws ApplicationException {
		return _getApplication(appId,false);

	}
	
	private Application _getApplication(String appId,boolean loadKey) throws ApplicationException {
		try {
			Application bean = executor.queryObject(Application.class, "selectById", appId);
			if(bean.getCertAlgorithm() == null){
				bean.setCertAlgorithm("RSA") ;
			}
			if(loadKey)
			{
//				if(bean.getCertAlgorithm().equals("RSA")){
//					SimpleKeyPair keypair = TokenHelper.getTokenService().getSimpleKeyPair(bean.getAppCode());
//		    		if(keypair != null)
//		    		{
//		    			bean.setPrivateKey(keypair.getPrivateKey());
//		    			bean.setPublicKey(keypair.getPublicKey());
//		    		}
//				}
//				else
				{
					SimpleKeyPair keypair =  TokenHelper.getTokenService().getSimpleKey(bean.getAppCode(),bean.getCertAlgorithm());
		    		if(keypair != null)
		    		{
		    			bean.setPrivateKey(keypair.getPrivateKey());
		    			bean.setPublicKey(keypair.getPublicKey());
		    		}
				}
	    		
			}
			return bean;
		} catch (Throwable e) {
			throw new ApplicationException("get Application failed::appId=" + appId, e);
		}

	}
	public Application getApplicationByAppcode(String appcode) throws ApplicationException {
		return _getApplicationByAppcode(appcode,true);
	}
	
	public Application getApplicationByAppcodeWithNoKey(String appcode) throws ApplicationException {
		return _getApplicationByAppcode(appcode,false);
	}
	private Application _getApplicationByAppcode(String appcode,boolean loadKey) throws ApplicationException {
		try {
			Application bean = executor.queryObject(Application.class, "selectByAppcode", appcode);
			if(bean.getCertAlgorithm() == null){
				bean.setCertAlgorithm("RSA") ;
			}
			if(loadKey)
			{
//				if(bean.getCertAlgorithm().equals("RSA")){
//					SimpleKeyPair keypair = TokenHelper.getTokenService().getSimpleKeyPair(bean.getAppCode());
//		    		if(keypair != null)
//		    		{
//		    			bean.setPrivateKey(keypair.getPrivateKey());
//		    			bean.setPublicKey(keypair.getPublicKey());
//		    		}
//				}
//				else
				{
					SimpleKeyPair keypair =  TokenHelper.getTokenService().getSimpleKey(bean.getAppCode(),bean.getCertAlgorithm());
		    		if(keypair != null)
		    		{
		    			bean.setPublicKey(keypair.getPublicKey());
		    			bean.setPrivateKey(keypair.getPrivateKey());
		    		}
				}
	    		
			}
		
			return bean;
		} catch (Throwable e) {
			throw new ApplicationException("get Application failed::appcode=" + appcode, e);
		}

	}
	public ListInfo queryListInfoApplications(ApplicationCondition conditions, long offset, int pagesize)
			throws ApplicationException {
		ListInfo datas = null;
		try {
			datas = executor.queryListInfoBean(Application.class, "queryListApplication", offset, pagesize, conditions);
		} catch (Exception e) {
			throw new ApplicationException("pagine query Application failed:", e);
		}
		return datas;

	}
	public List<Application> queryListApplications(ApplicationCondition conditions) throws ApplicationException {
		try {
			List<Application> beans = executor.queryListBean(Application.class, "queryListApplication", conditions);
			return beans;
		} catch (Exception e) {
			throw new ApplicationException("query Application failed:", e);
		}

	}
}