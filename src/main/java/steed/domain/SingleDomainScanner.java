package steed.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import steed.util.base.BaseUtil;
import steed.util.base.PathUtil;
import steed.util.base.PropertyUtil;
import steed.util.file.FileUtil;

/**
 * 单数据库实体类扫描器
 * @author 战马
 *
 */
public class SingleDomainScanner implements DomainScanner{
	
	@SuppressWarnings("unchecked")
	public List<Class<? extends BaseDatabaseDomain>> scan(String configFile){
		List<Class<? extends BaseDatabaseDomain>> list = new ArrayList<>();
		if(!PropertyUtil.getBoolean("isSignalDatabase")){
			return list;
		}
		String classesPath = PathUtil.getClassesPath();
		
		List<File> allFile = new FileUtil().getAllFile(classesPath,null);
		if (PropertyUtil.getBoolean("devMode")) {
			allFile.addAll(new FileUtil().getAllFile(classesPath.replaceFirst("classes", "test-classes"),null));
			allFile.addAll(new FileUtil().getAllFile(classesPath.replaceFirst("test-classes", "classes"),null));
		}
		
		for (File f:allFile) {
			String absolutePath = f.getAbsolutePath();
			if (!absolutePath.endsWith(".class") || (!absolutePath.contains("domain") && !absolutePath.contains("model"))) {
				continue;
			}
			String replaceAll = absolutePath.substring(absolutePath.indexOf("classes")+"classes.".length()).replaceAll("\\\\", "/").replaceAll("\\/", ".");
			try {
				String domainClassName = replaceAll.substring(0,replaceAll.length() - 6);
				BaseUtil.getLogger().debug("扫描{}",domainClassName);
				Class<?> domainClass = Class.forName(domainClassName);
				if (BaseRelationalDatabaseDomain.class.isAssignableFrom(domainClass)) {
					if (domainClass.getAnnotation(Entity.class) != null) {
						list.add((Class<? extends BaseDatabaseDomain>) domainClass);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}catch (Error e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
