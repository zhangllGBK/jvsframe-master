package com.jarveis.frame.dbs.service;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ant.After;
import com.jarveis.frame.dbs.ant.Before;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.util.Param;

/**
 * @desc Echo服务
 * @author liuguojun
 * @create 2018-04-10
 */
@Function(code = "10002")
public class EchoHtml implements Service {

	public Param callService(Param in) {
		Param out = null;

		try {
			out = new Param(Param.RESP);
			String name = in.getBody().getString("@name");

			out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_SUCCESS);
			out.getBody().addCDATA("Hello," + name);
		} catch (Exception ex) {
			if (out != null) {
				out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
			}
		}

		return out;
	}
}