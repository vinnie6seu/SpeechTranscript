package com.fuwei.asr.SpeechTranscript.util;

import com.fuwei.asr.SpeechTranscript.common.exception.GlobalException;
import com.fuwei.asr.SpeechTranscript.common.vo.ResultVo;
import com.fuwei.asr.SpeechTranscript.constant.CodeMsgEnum;

/**
 * 返回json串包装
 */
public class ResultVoUtil {
	
	public static ResultVo success() {
		// 也可以指出泛型类型
		ResultVo<Object> resultVO = new ResultVo<>();
		resultVO.setData(null);
		resultVO.setCode(CodeMsgEnum.SERVER_SUCCESS.getCode());
		resultVO.setMsg(CodeMsgEnum.SERVER_SUCCESS.getMsg());
		return resultVO;
	}   	
	
	public static ResultVo success(CodeMsgEnum codeMsg) {
		// 也可以指出泛型类型
		ResultVo<Object> resultVO = new ResultVo<>();
		resultVO.setData(null);
		resultVO.setCode(codeMsg.getCode());
		resultVO.setMsg(codeMsg.getMsg());
		return resultVO;
	}	
	
	public static ResultVo success(CodeMsgEnum codeMsg, Object object) {
		// 也可以指出泛型类型
		ResultVo<Object> resultVO = new ResultVo<>();
		resultVO.setData(object);
		resultVO.setCode(codeMsg.getCode());
		resultVO.setMsg(codeMsg.getMsg());
		return resultVO;
	}
	
	// 错误的时候只传错误信息
    public static ResultVo error(CodeMsgEnum codeMsg) {
        ResultVo resultVO = new ResultVo();
        resultVO.setData(null);
        resultVO.setCode(codeMsg.getCode());
        resultVO.setMsg(codeMsg.getMsg());
        return resultVO;
    }
    
    public static ResultVo error(GlobalException globalException) {
        ResultVo resultVO = new ResultVo();
        resultVO.setData(null);
        resultVO.setCode(globalException.getCode());
        resultVO.setMsg(globalException.getMsg());
        return resultVO;
    }    
}
