package com.jarveis.frame.sluice.core.datameta;

/**
 * 常量
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public class Constant extends DataMeta {

	public Constant(DataType dataType, Object value) {
		super(dataType, value);

		if (dataType == null) {
			throw new RuntimeException("非法参数：数据类型为空");
		}
	}

	public Constant(Reference ref) {
		super(null, ref);
		this.setReference(true);
	}
}
