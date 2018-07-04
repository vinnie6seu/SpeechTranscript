package com.fuwei.asr.SpeechTranscript.aop;

/**
 * 切面包裹顺序
 */
public enum AopOrderEnum {

	EXCEPT_AOP(1, "全局异常"),	
	AUTH_AOP(2, "登录检查"),
	LOG_AOP(3, "日志记录"),
	DATA_AOP(4, "数据源切换"),
	TRANSCATION(5, "数据源事务");

	// aop顺序,数字越小,越在外层
	private final Integer num;

	// aop名称
	private final String name;

	private AopOrderEnum(Integer num, String name) {
		this.num = num;
		this.name = name;
	}

	public Integer getNum() {
		return num;
	}

	public String getName() {
		return name;
	}
}
