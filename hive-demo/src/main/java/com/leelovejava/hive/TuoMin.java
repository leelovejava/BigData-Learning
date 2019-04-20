package com.leelovejava.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class TuoMin extends UDF {

	public Text evaluate(final Text s) {
		if (s == null) {
			return null;
		}
		String str = s.toString().substring(0, 3) + "***";
		return new Text(str);
	}

}
