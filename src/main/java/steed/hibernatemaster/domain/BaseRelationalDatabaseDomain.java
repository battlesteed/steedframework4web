package steed.hibernatemaster.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import org.hibernate.Hibernate;

import steed.hibernatemaster.util.DaoUtil;
import steed.hibernatemaster.util.HqlGenerator;
import steed.hibernatemaster.util.base.BaseUtil;
import steed.hibernatemaster.util.base.DomainUtil;
import steed.hibernatemaster.util.base.ReflectUtil;
/**
 * 关系型数据库基础实体类
 * @author 战马
 *
 */
public class BaseRelationalDatabaseDomain extends BaseDatabaseDomain{
	
	private static final long serialVersionUID = -3084039108845387366L;
	protected HqlGenerator personalHqlGenerator;
	
	@Transient
	public HqlGenerator getPersonalHqlGenerator() {
		return personalHqlGenerator;
	}
	
	/**
	 * 设置属于本实体类的个性化的hql生成器
	 * @param personalHqlGenerator
	 * @see steed.hibernatemaster.util.SimpleHqlGenerator
	 */
	public void setPersonalHqlGenerator(HqlGenerator personalHqlGenerator) {
		this.personalHqlGenerator = personalHqlGenerator;
	}
	public void initialize(){
		Hibernate.initialize(this);
	}
	/**
	 * 不为null就initialize,避免子类getXXX().initializeAll()时
	 * 先要判断getXXX()是否为null
	 */
	protected void domainInitializeAll(BaseRelationalDatabaseDomain domain){
		if (domain != null) {
			domain.initializeAll();
		}
	}
	/**
	 * initialize set中的domain
	 */
	protected void domainInitializeSetAll(Set<? extends BaseRelationalDatabaseDomain> set){
		if (set != null) {
			for(BaseRelationalDatabaseDomain temp:set){
				temp.initializeAll();
			}
		}
	}
	
	/*
	 * initializeAll的深度，如过为零，则只initialize本身。
	 * @param deep
	 *public void initializeAll(int deep){
		if (deep > 0) {
			
		}else if (deep == 0) {
			this.initialize();
		}
		
	}*/
	public void initializeAll(){
		initialize();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends BaseDatabaseDomain> T smartGet(){
		return (T) DaoUtil.smartGet(this);
	}
	
	@Override
	public boolean update(){
		return DaoUtil.update(this);
	}
	
	@Override
	public boolean delete(){
		return DaoUtil.delete(this);
	}
	
	@Override
	public boolean save(){
		return DaoUtil.save(this);
	}
	
	@Override
	public boolean saveOrUpdate(){
		if (BaseUtil.isObjEmpty(DomainUtil.getDomainId(this))) {
			return save();
		}else {
			BaseDatabaseDomain smartGet = smartGet();
			if (smartGet != null) {
				DaoUtil.evict(smartGet);
				return update();
			}else {
				return save();
			}
		}
	}
	
	@Override
	public boolean updateNotNullField(List<String> updateEvenNull){
		return updateNotNullField(updateEvenNull, true);
	}
	
	@Override
	public boolean updateNotNullField(List<String> updateEvenNull,boolean strictlyMode){
		List<String> list;
		if (updateEvenNull == null) {
			list = new ArrayList<String>();
			for (Field f:ReflectUtil.getAllFields(this)) {
					list.add(f.getName());
			}
		}else {
			list = updateEvenNull;
		}
		return DomainUtil.fillDomain(this.smartGet(), this,list,strictlyMode).update();
	}
	/*public void initializeAll(){
		this.initialize();
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field f:fields){
			try {
				f.setAccessible(true);
				Object obj = f.get(this);
				if (BaseUtil.isObjEmpty(obj)) {
					continue;
				}
				if (obj instanceof BaseDatabaseDomain) {
					((BaseDatabaseDomain) obj).initializeAll();
				}else if (CollectionsUtil.isObjCollections(obj)) {
					Object[] objects = CollectionsUtil.collections2Array(obj);
					if (objects == null || objects.length < 1) {
						continue;
					}
					if (!(objects[0] instanceof BaseDatabaseDomain)) {
						continue;
					}
					for (BaseDatabaseDomain o:(BaseDatabaseDomain[])objects) {
						if (BaseUtil.isObjEmpty(o)) {
							continue;
						}
						o.initializeAll();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}*/
	
	/*	public boolean validate(){
		Class<? extends BaseDatabaseDomain> clazz = this.getClass();
		Properties IDReg = PropertyUtil.getProperties("IDReg.properties");
		String reg = IDReg.getProperty(clazz.getName());
		if (BaseUtil.isStringEmpty(reg)) {
			return true;
		}
		Serializable domainId = DomainUtil.getDomainId(this);
		if (BaseUtil.isObjEmpty(domainId)) {
			return true;
		}
		Pattern p = Pattern.compile(reg);
		return p.matcher(domainId.toString()).find();
	}*/
	@SuppressWarnings("unchecked")
	public <T> List<T> listAll(){
		return (List<T>) DaoUtil.listAllObj(this);
	}
}
