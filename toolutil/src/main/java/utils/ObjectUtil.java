package utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 两类间Fields Name 相同的值赋值(不区分大小写)
 * 
 * @createDate Mar 17, 2011 9:42:55 AM
 * @author zy
 * @version 1.0.0
 * @modifyAuthor
 * @modifyDate
 */
public class ObjectUtil {

	/**
	 * 两类间Fields Name 相同的值赋值(不区分大小写)
	 * 
	 * @param src
	 *            源类
	 * @param target
	 *            目标类
	 * @return null 赋值失败
	 */
	public static void copyToObject(Object src, Object target) {
		if (src == null) {
			return;
		}
		try {
			BeanInfo beanSrc = Introspector.getBeanInfo(src.getClass(), Object.class);
			PropertyDescriptor[] srcFields = beanSrc.getPropertyDescriptors();

			BeanInfo beanTarget = Introspector.getBeanInfo(target.getClass(), Object.class);
			PropertyDescriptor[] targetFields = beanTarget.getPropertyDescriptors();
			
			for (PropertyDescriptor srcField : srcFields) {
				for (PropertyDescriptor targetField : targetFields) {
					// 字段名相同并且类型一致,不区分大小写
					if (srcField.getName().toLowerCase().equals(targetField.getName().toLowerCase())&& srcField.getPropertyType().getName().equals(targetField.getPropertyType().getName())) {
						Method readMethod = srcField.getReadMethod();
						Object value = readMethod.invoke(src);
						if (value == null) {
							continue;
						}

						// 参数值
						Object setParamValue = value;
						Object paramlist[] = new Object[1];
						paramlist[0] = setParamValue;
						Method writeMethod = targetField.getWriteMethod();
						writeMethod.invoke(target, paramlist);
					}
				}
			}
		} catch (Exception e) {
			target = null;
			e.printStackTrace();
		}
	}

	public static List<String> viewObject(Object src) {
		List<String> viewBean = new ArrayList<String>();
		if (src == null) {
			return viewBean;
		}
		try {
			BeanInfo beanSrc = Introspector.getBeanInfo(src.getClass(), Object.class);
			PropertyDescriptor[] srcFields = beanSrc.getPropertyDescriptors();
			for (PropertyDescriptor srcField : srcFields) {
				// 字段名相同并且类型一致,不区分大小写
				Method readMethod = srcField.getReadMethod();
				Object value = readMethod.invoke(src, (Object)null);
				if (value == null) {
					continue;
				}
				// 递归
				if (srcField.getPropertyType().isArray()
						&& !srcField.getPropertyType().getComponentType().isPrimitive()
						&& !(srcField.getPropertyType().getComponentType().newInstance() instanceof String)) {
					// 处理数组---不是基本类型并且不是String类型
					viewObject(value);
				} else if (!srcField.getPropertyType().isPrimitive()
						&& !(srcField.getPropertyType().newInstance() instanceof String)) {
					// 不是基本类型并且不是String类型
					viewObject(value);
				} else {
					// 参数值
					System.out.println(srcField.getDisplayName() + "=" + value);
					// TempCacheLog.getInstance().writeTempLog(srcField.getDisplayName()
					// + "=" + value);
					viewBean.add(srcField.getDisplayName() + "=" + value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return viewBean;
	}
}
