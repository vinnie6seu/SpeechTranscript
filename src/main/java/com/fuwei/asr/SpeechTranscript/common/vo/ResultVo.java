package com.fuwei.asr.SpeechTranscript.common.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * http请求返回的最外层对象
 * 
 * @Data：省略get和set方法
 */
@Data
public class ResultVo<T> implements Serializable {

	/** 错误码. */
    private Integer code;

    /** 提示信息. */
    private String msg;

    /** 具体内容. */
    private T data;
}
