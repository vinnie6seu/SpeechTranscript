package com.fuwei.asr.SpeechTranscript.aop.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fuwei.asr.SpeechTranscript.aop.AopOrderEnum;
import com.fuwei.asr.SpeechTranscript.common.exception.GlobalException;
import com.fuwei.asr.SpeechTranscript.common.vo.ResultVo;
import com.fuwei.asr.SpeechTranscript.constant.CodeMsgEnum;
import com.fuwei.asr.SpeechTranscript.util.ResultVoUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler implements Ordered {
	@ExceptionHandler(value = Exception.class) // 捕获Controller抛出的所有Exception异常
	@ResponseStatus(HttpStatus.ACCEPTED) // 返回200,所有的服务器返回情况都在success中处理
	public ResultVo exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {		
		log.error(e.getMessage(), e);		

		ResultVo resultVo = null;	
		if (e instanceof GlobalException) {
			
			// InvalidKaptchaException  验证码错误          ----------
			
			// KickoutException   用户被踢出   -----------			
			
			// 主动抛出的GlobalException异常
			GlobalException ex = (GlobalException) e;
			
			resultVo = ResultVoUtil.error(ex);			
		} else if (e instanceof BindException) {
			// JSR303不与@RequestBody一起使用抛出的BindException参数校验异常
			BindException ex = (BindException) e;
			List<ObjectError> errors = ex.getAllErrors();
			ObjectError error = errors.get(0);
			String msg = error.getDefaultMessage();
			
//			resultVo = ResultVoUtil.error(new GlobalException(CodeMsgEnum.LOGIN_PARA_ERROR, msg));
		} else if(e instanceof MethodArgumentNotValidException) {
			// JSR303与@RequestBody一起使用参数校验会抛出该异常
			MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
			List<ObjectError> errors = ex.getBindingResult().getAllErrors();
			ObjectError error = errors.get(0);
			String msg = error.getDefaultMessage();			
			
//			resultVo = ResultVoUtil.error(new GlobalException(CodeMsgEnum.LOGIN_PARA_ERROR, msg));
		} /* else if (e instanceof AuthenticationException) {
			// 用户未登录
			resultVo = ResultVoUtil.error(new GlobalException(CodeMsgEnum.SHIRO_NO_AUTH));
		} else if (e instanceof DisabledAccountException) {
			// 账户被冻结
			resultVo = ResultVoUtil.error(new GlobalException(CodeMsgEnum.SHIRO_DISABLE_ACCOUNT));	
		} else if (e instanceof CredentialsException) {
			// 账号密码错误
			resultVo = ResultVoUtil.error(new GlobalException(CodeMsgEnum.SHIRO_ERROR_CRDENT));	
		} else if (e instanceof UndeclaredThrowableException) {			
			// 无权访问该资源
			resultVo = ResultVoUtil.error(new GlobalException(CodeMsgEnum.SHIRO_NO_PERMITION));		
		} else if (e instanceof InvalidSessionException || e instanceof UnknownSessionException) {	
			// 如果session失效需要设置
			assertAjax(request, response);
			
			// session超时失效
			resultVo = ResultVoUtil.error(new GlobalException(CodeMsgEnum.SHIRO_SESSION_LOST));	
		} */ else {
			// Exception是最终未知异常
			resultVo = ResultVoUtil.error(CodeMsgEnum.SERVER_ERROR);
		}
		
//		// 异步保存异常日志
//		logPersist(resultVo, e);			
		
		
		return resultVo;		
	}
	
//	// 异步保存日志
//	private void logPersist(ResultVo resultVo, Exception e) {	
//		try {
//			OperateLog operateLog = OperateLogHolder.getOperateLog();
//			if (ToolUtil.isNotEmpty(operateLog)) {
//				LogPersistManager.me().execute(LogPersistTaskFactory.operateLog(BeanFactory.createOperateLog(LogTypeEnum.EXCEPT_LOG.getNum().toString(), operateLog.getLogName(), 11111, operateLog.getClassName(), operateLog.getMethod(), "失败", HttpKit.getIp(), operateLog.getRequest(), e.toString())));	
//			} else {
//				// **************** JSR303参数校验在日志AOP之前,很麻烦????  **********************
//				LogPersistManager.me().execute(LogPersistTaskFactory.operateLog(BeanFactory.createOperateLog(LogTypeEnum.EXCEPT_LOG.getNum().toString(), resultVo.getMsg(), 11111, null, null, "失败", HttpKit.getIp(), HttpKit.getRequestParameters().toString(), e.toString())));
//			}			
//		} finally {
//			log.info("清空OperateLogHolder中暂存日志信息！");
//			// 线程结束时,一定要将ThreadLocal中的信息移除
//			OperateLogHolder.clearOperateLog();			
//		}		
//	}
	
	// session失效设置
	private void assertAjax(HttpServletRequest request, HttpServletResponse response) {
		if (request.getHeader("x-requested-with") != null
				&& request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
			// 如果是ajax请求响应头会有，x-requested-with
			response.setHeader("sessionstatus", "timeout");// 在响应头设置session状态
		}
	}	
	
	@Override
	public int getOrder() {
		return AopOrderEnum.EXCEPT_AOP.getNum();
	}		
}
